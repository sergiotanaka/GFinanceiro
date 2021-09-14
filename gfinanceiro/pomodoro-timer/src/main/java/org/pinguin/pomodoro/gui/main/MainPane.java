package org.pinguin.pomodoro.gui.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.pinguin.pomodoro.domain.pomodoro.Pomodoro;
import org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent;
import org.pinguin.pomodoro.domain.pomodoro.PomodoroState;
import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.task.TaskState;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransitionRepository;
import org.pinguin.pomodoro.domain.transition.Transition;
import org.pinguin.pomodoro.gui.mini.MiniPane;
import org.pinguin.pomodoro.gui.report.DailyReportPane;
import org.pinguin.pomodoro.gui.report.ReportPane;
import org.pinguin.pomodoro.gui.report.ReportRow;
import org.pinguin.pomodoro.gui.timer.Timer.State;
import org.pinguin.pomodoro.gui.util.Scroller;
import org.pinguin.pomodoro.service.report.ReportService.Period;

import com.google.inject.Injector;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableView;
import javafx.scene.image.Image;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainPane extends BorderPane {

	@SuppressWarnings("unused")
	private static final DataFormat SERIALIZED_MIME_TYPE = new DataFormat("application/x-java-serialized-object");

	@Inject
	private Injector injector;
	@Inject
	private EntityManager em;
	@Inject
	private TaskRepository taskRepo;
	@Inject
	private TaskStateTransitionRepository taskStateTranstRepo;

	private Runnable callFocus;
	private final DoubleProperty progressProp = new SimpleDoubleProperty(0);
	private final StringProperty remainingProp = new SimpleStringProperty();
	private final StringProperty executingTasks = new SimpleStringProperty();

	// Componentes graficos //
	private TreeTableView<TaskRow> taskTableView;
	private Label remainingLbl;
	private Button pauseBtn;
	private Button stopBtn;

	// Classes de apoio //
	private Pomodoro actual = new Pomodoro();
	private Scroller scroller = new Scroller();

	public MainPane() {
	}

	@Inject
	public void init() {
		// Margem
		this.setPadding(new Insets(10.0));

		taskTableView = injector.getInstance(TaskTreeTableView.class);
		remainingLbl = createRemainingLabel();
		pauseBtn = createPauseButton();
		stopBtn = createStopButton();
		final Button startBtn = createStartButton();
		scroller.setup(taskTableView);

		final GridPane grid = new GridPane();
		grid.setGridLinesVisible(false);
		grid.setHgap(5.0);
		grid.setVgap(5.0);
		grid.add(new Label("Tempo restante: "), 0, 0);
		grid.add(remainingLbl, 1, 0);
		grid.add(taskTableView, 0, 1, 2, 1);
		grid.add(new HBox(3.0, startBtn, pauseBtn, stopBtn), 0, 2, 2, 1);

		final Button saveBtn = createSaveButton();
		final Button clockBtn = createClockButton();
		final Button reportBtn = createReportButton();
		final Button dailyReportBtn = createDailyReportButton();
		final Button refreshBtn = createRefreshButton();
		grid.add(new HBox(3.0, saveBtn, refreshBtn, clockBtn, reportBtn, dailyReportBtn), 0, 3, 2, 1);

		configConstraints(grid);

		this.centerProperty().set(grid);

		this.actual.stateProperty().addListener((r, o, n) -> {
			System.out.println(String.format("%s - Mudança de estado: %s -> %s", new Date(), o, n));
			em.persist(new Transition(o, n));
		});

		this.actual.setOnTimeout(() -> {
			if (callFocus != null) {
				callFocus.run();
			}
			stopAllTasks();
			playAlarm();
		});

		final Thread thread1 = new Thread(() -> {
			while (true) {
				long remaining = actual.getRemaining();
				if (actual.stateProperty().get().equals(PomodoroState.EXECUTING)
						|| actual.stateProperty().get().equals(PomodoroState.RESTING)) {
					remaining -= (System.currentTimeMillis() - actual.getLastUpdate());
				}
				if (remaining <= 0) {
					remaining = 0;
				}

				long seconds = (remaining / 1000) % 60;
				long minutes = ((remaining / 1000) - seconds) / 60;
				Platform.runLater(() -> remainingProp.set(String.format("%02d:%02d", minutes, seconds)));

				final long finalRemaining = remaining;
				Platform.runLater(() -> {
					final double totalTime = actual.stateProperty().get().equals(PomodoroState.RESTING) ? 5.0 : 25.0;
					progressProp.set(1.0 - ((finalRemaining * 1.0) / (totalTime * 60.0 * 1000.0)));
				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
				}
			}
		});
		thread1.setDaemon(true);
		thread1.start();

		// Thread para atualizacao do tempo gasto
		final Thread thread2 = new Thread(() -> {
			while (true) {
				updateSpentTime();
				try {
					Thread.sleep(1000 * 10);
				} catch (InterruptedException e1) {
				}
			}
		});
		thread2.setDaemon(true);
		thread2.start();

	}

	private void configConstraints(final GridPane grid) {
		final ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHgrow(Priority.ALWAYS);
		final ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(cc1, cc2);

		final RowConstraints rc1 = new RowConstraints();
		rc1.setVgrow(Priority.NEVER);
		final RowConstraints rc2 = new RowConstraints();
		rc2.setPrefHeight(700.0);
		rc2.setVgrow(Priority.ALWAYS);
		final RowConstraints rc3 = new RowConstraints();
		rc3.setVgrow(Priority.NEVER);
		final RowConstraints rc4 = new RowConstraints();
		rc4.setVgrow(Priority.NEVER);
		grid.getRowConstraints().addAll(rc1, rc2, rc3, rc4);
	}

	private Button createRefreshButton() {
		final Button refreshBtn = new Button("Atualizar");
		refreshBtn.setOnAction(e -> {
			final ObservableList<TaskRow> items = FXCollections.observableArrayList();
			taskRepo.getAllUndone().forEach(t -> items.add(buildTaskRow(t)));
			this.setItems(items);
		});
		return refreshBtn;
	}

	private Button createDailyReportButton() {
		final Button dailyReportBtn = new Button("R. diário");
		dailyReportBtn.setOnAction(e -> {
			try {
				final Stage reportStage = new Stage();
				reportStage.getIcons().add(new Image(MainPane.class.getResourceAsStream("/META-INF/256x256bb.jpg")));
				final DailyReportPane pane = injector.getInstance(DailyReportPane.class);
				reportStage.setScene(new Scene(pane));
				reportStage.setTitle("Relatório diário");

				reportStage.sizeToScene();
				reportStage.toFront();
				Platform.runLater(reportStage::centerOnScreen);

				reportStage.show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
		return dailyReportBtn;
	}

	private Button createReportButton() {
		final Button reportBtn = new Button("Relatório");
		reportBtn.setOnAction(e -> {
			final Stage reportStage = new Stage();
			reportStage.getIcons().add(new Image(MainPane.class.getResourceAsStream("/META-INF/256x256bb.jpg")));
			final ReportPane pane = new ReportPane();
			reportStage.setScene(new Scene(pane));
			reportStage.setTitle("Relatório");
			final ObservableList<ReportRow> items = FXCollections.observableArrayList();
			getHistory().forEach(st -> {
				final Task task = st.getTaskId() == null ? null : em.find(Task.class, st.getTaskId());
				items.add(new ReportRow(st.getTimeStamp(),
						task != null ? task.getName() : st.getTaskId() != null ? st.getTaskId().toString() : "no ref",
						st.getBefore(), st.getAfter()));
			});
			pane.setItems(items);

			reportStage.sizeToScene();
			reportStage.toFront();
			Platform.runLater(reportStage::centerOnScreen);

			reportStage.show();

		});
		return reportBtn;
	}

	/**
	 * @return
	 */
	private Button createClockButton() {
		final Button clockBtn = new Button("Relógio");
		clockBtn.setOnAction(e -> {
			final Stage clock = new Stage(StageStyle.TRANSPARENT);
			clock.getIcons().add(new Image(MainPane.class.getResourceAsStream("/META-INF/256x256bb.jpg")));
			clock.setTitle("Timer");
			final MiniPane miniPane = new MiniPane();
			progressProp.addListener((r, o, n) -> miniPane.getTimer().setProgress((double) n));
			if (actual.stateProperty().get().equals(PomodoroState.EXECUTING)
					|| actual.stateProperty().get().equals(PomodoroState.RESTING)) {
				miniPane.getTimer().stateProperty().set(State.RUNNING);
			} else if (actual.stateProperty().get().equals(PomodoroState.STOPPED)
					|| actual.stateProperty().get().equals(PomodoroState.PAUSED)) {
				miniPane.getTimer().stateProperty().set(State.STOPPED);
			}
			actual.stateProperty().addListener((r, o, n) -> {
				if (n.equals(PomodoroState.EXECUTING) || n.equals(PomodoroState.RESTING)) {
					miniPane.getTimer().stateProperty().set(State.RUNNING);
				} else if (n.equals(PomodoroState.STOPPED) || n.equals(PomodoroState.PAUSED)) {
					miniPane.getTimer().stateProperty().set(State.STOPPED);
				}
			});
			miniPane.getTimer().setOnEvent(t -> {
				switch (t) {
				case STARTED:
					actual.onEvent(PomodoroEvent.START);
					break;
				case STOPPED:
					actual.onEvent(PomodoroEvent.PAUSE);
					break;
				default:
					break;
				}
			});

			miniPane.getRemainingLabel().textProperty().bind(remainingProp);
			miniPane.getTooltip().textProperty().bind(executingTasks);

			final Delta dragDelta = new Delta();
			final Scene scene = new Scene(miniPane);
			clock.setScene(scene);

			miniPane.setOnMousePressed(me -> {
				dragDelta.x = clock.getX() - me.getScreenX();
				dragDelta.y = clock.getY() - me.getScreenY();
				scene.setCursor(Cursor.MOVE);
			});
			miniPane.setOnMouseReleased(me -> scene.setCursor(Cursor.HAND));
			miniPane.setOnMouseDragged(me -> {
				clock.setX(me.getScreenX() + dragDelta.x);
				clock.setY(me.getScreenY() + dragDelta.y);
			});
			miniPane.setOnMouseEntered(me -> {
				if (!me.isPrimaryButtonDown()) {
					scene.setCursor(Cursor.HAND);
				}
			});
			miniPane.setOnMouseExited(me -> {
				if (!me.isPrimaryButtonDown()) {
					scene.setCursor(Cursor.DEFAULT);
				}
			});

			miniPane.setCloseWindow(clock::close);

			clock.sizeToScene();
			clock.toFront();
			Platform.runLater(clock::centerOnScreen);

			clock.show();
		});
		return clockBtn;
	}

	/**
	 * 
	 * @return
	 */
	private Button createSaveButton() {
		final Button saveBtn = new Button("Salvar");
		saveBtn.setOnAction(e -> {
			em.getTransaction().commit();
			em.getTransaction().begin();
		});
		return saveBtn;
	}

	/**
	 * 
	 * @return
	 */
	private Button createStartButton() {
		final Button startBtn = new Button("Iniciar");
		startBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.START));
		return startBtn;
	}

	/**
	 * @return
	 */
	private Label createRemainingLabel() {
		final Label remainingLbl = new Label();
		remainingLbl.textProperty().bind(remainingProp);
		return remainingLbl;
	}

	/**
	 * @return
	 */
	private Button createPauseButton() {
		final Button pauseBtn = new Button("Pausar");
		pauseBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.PAUSE));
		return pauseBtn;
	}

	/**
	 * @return
	 */
	private Button createStopButton() {
		final Button stopBtn = new Button("Parar");
		stopBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.FINISH));
		return stopBtn;
	}

	private void updateSpentTime() {
		Platform.runLater(() -> {
			taskTableView.getRoot().getChildren().forEach(ti -> {
				sumChildSpentTime(ti);
			});
		});
	}

	private long sumChildSpentTime(final TreeItem<TaskRow> item) {
		long sum = calcSpentTime(item.getValue().getTask().getId());
		for (final TreeItem<TaskRow> ti : item.getChildren()) {
			sum += sumChildSpentTime(ti);
		}
		item.getValue().spentProperty().set((int) (sum / 60));
		return sum;
	}

	private long calcSpentTime(final Long taskId) {
		final List<TaskStateTransition> transitions = taskStateTranstRepo.retrieveByTaskId(taskId);

		final List<Period> periodList = new ArrayList<>();

		for (final TaskStateTransition transition : transitions) {
			switch (transition.getAfter()) {
			case DONE:
			case STOPPED:
				if (periodList.isEmpty()) {
					periodList.add(new Period());
				}
				final Period last = periodList.get(periodList.size() - 1);
				if (last.getEnd() != null) {
					System.out.println("warn! final do periodo preenchido.");
				} else {
					last.setEnd(transition.getTimeStamp());
				}
				break;
			case EXECUTING:
				final Period period = new Period();
				period.setStart(transition.getTimeStamp());
				periodList.add(period);
				break;
			default:
				break;
			}
		}

		long sum = 0;
		for (Period p : periodList) {
			sum += p.duration();
		}

		return sum;
	}

	// records relative x and y co-ordinates.
	private static class Delta {
		double x, y;
	}

	public void updateExecutingTasks() {
		final StringBuilder sb = new StringBuilder();
		getAllTaskTreeItems(taskTableView.getRoot()).forEach(ti -> {
			if (ti.getValue().getTask().getState().equals(TaskState.EXECUTING)) {
				sb.append(ti.getValue().getTask().getName()).append(System.lineSeparator());
			}
		});
		Platform.runLater(() -> executingTasks.set(sb.toString()));
	}

	public StringProperty remainingProperty() {
		return remainingProp;
	}

	private void stopAllTasks() {
		Platform.runLater(() -> {
			getAllTaskTreeItems(taskTableView.getRoot()).forEach(ti -> {
				final ObjectProperty<TaskState> stateProperty = ti.getValue().stateProperty();
				if (stateProperty.get().equals(TaskState.EXECUTING)) {
					stateProperty.set(TaskState.STOPPED);
				}
			});
		});
	}

	private List<TreeItem<TaskRow>> getAllTaskTreeItems(TreeItem<TaskRow> ref) {
		final List<TreeItem<TaskRow>> result = new ArrayList<>();
		ref.getChildren().forEach(ti -> {
			result.add(ti);
			result.addAll(getAllTaskTreeItems(ti));
		});
		return result;
	}

	private void playAlarm() {
		try (InputStream is = getClass().getResourceAsStream("/META-INF/alarm.wav");
				BufferedInputStream bis = new BufferedInputStream(is);
				AudioInputStream ais = AudioSystem.getAudioInputStream(bis);) {
			final Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void setCallFocus(Runnable callFocus) {
		this.callFocus = callFocus;
	}

	private void listenChilds(final TreeItem<TaskRow> item) {
		item.getChildren().addListener((ListChangeListener<TreeItem<TaskRow>>) c -> {
			while (c.next()) {
				if (c.wasRemoved()) {
					int sum = 0;
					for (final TreeItem<TaskRow> child : item.getChildren()) {
						sum += child.getValue().estimatedProperty().get();
					}
					item.getValue().estimatedProperty().set(sum);
				}
			}
		});
	}

	public void setItems(final ObservableList<TaskRow> items) {
		final TreeItem<TaskRow> root = new TreeItem<TaskRow>(new TaskRow(new Task()));
		taskTableView.setRoot(root);

		// Transformando em Map
		final Map<Long, TreeItem<TaskRow>> map = items.stream()
				.collect(Collectors.toMap(r -> r.getTask().getId(), r -> {
					final TreeItem<TaskRow> newItem = new TreeItem<TaskRow>(r);
					listenChilds(newItem);
					return newItem;
				}));

		// Montando a arvore
		for (final Entry<Long, TreeItem<TaskRow>> entry : map.entrySet()) {
			final TreeItem<TaskRow> treeItem = entry.getValue();
			final Long parentId = treeItem.getValue().getTask().getParentId();
			if (parentId != null) {
				if (map.containsKey(parentId)) {
					map.get(parentId).getChildren().add(treeItem);
				} else {
					treeItem.getValue().getTask().setParentId(null);
					root.getChildren().add(treeItem);
				}
			} else {
				root.getChildren().add(treeItem);
			}
		}

		// Reordenar pelo indice
		sortTree(root);
	}

	private void sortTree(final TreeItem<TaskRow> parent) {
		parent.getChildren()
				.sort((o1, o2) -> o1.getValue().getTask().getIndex().compareTo(o2.getValue().getTask().getIndex()));
		for (final TreeItem<TaskRow> treeItem : parent.getChildren()) {
			sortTree(treeItem);
		}
	}

	private TaskRow buildTaskRow(final Task task) {
		final TaskRow taskRow = new TaskRow(task);
		taskRow.stateProperty().addListener((r, o, n) -> {
			em.persist(new TaskStateTransition(task.getId(), o, n));
			updateExecutingTasks();
		});
		taskRow.estimatedProperty().addListener((r, o, n) -> {
			updateParentEstimated(taskRow);
		});
		return taskRow;
	}

	public void updateParentEstimated(TaskRow taskRow) {
		final Optional<TreeItem<TaskRow>> found = getAllTaskTreeItems(this.taskTableView.getRoot()).stream()
				.filter(ti -> ti.getValue() == taskRow).findAny();
		if (found.isPresent()) {
			final TreeItem<TaskRow> parent = found.get().getParent();
			if (parent != null) {
				int sum = 0;
				for (final TreeItem<TaskRow> child : parent.getChildren()) {
					sum += child.getValue().estimatedProperty().get();
				}
				parent.getValue().estimatedProperty().set(sum);
			}
		}
	}

	public List<TaskStateTransition> getHistory() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TaskStateTransition> cq = cb.createQuery(TaskStateTransition.class);
		final Root<TaskStateTransition> taskTransition = cq.from(TaskStateTransition.class);
		cq.select(taskTransition).orderBy(cb.asc(taskTransition.get("timeStamp")));
		return em.createQuery(cq).getResultList();
	}

}
