package org.pinguin.pomodoro.gui.main;

import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.FINISH;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.PAUSE;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.START;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.pinguin.pomodoro.domain.pomodoro.PomodoroState;
import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.task.TaskState;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.domain.transition.Transition;
import org.pinguin.pomodoro.gui.mini.MiniPane;
import org.pinguin.pomodoro.gui.timer.Timer.State;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class MainPane extends BorderPane {

	private Runnable callFocus;

	private final DoubleProperty progressProp = new SimpleDoubleProperty(0);

	@Inject
	private EntityManager em;
	@Inject
	private TaskRepository taskRepo;

	private final TreeTableView<TaskRow> taskTableView;
	private final Label remainingLbl = new Label();
	private final Button pauseBtn = new Button("Pausar");
	private final Button stopBtn = new Button("Parar");

	private final StringProperty remainingProp = new SimpleStringProperty();

	private Pomodoro actual = new Pomodoro();

	public MainPane() {
		// Margem
		this.setPadding(new Insets(10.0));

		taskTableView = buildTaskTableView();

		final GridPane grid = new GridPane();
		grid.setGridLinesVisible(false);
		grid.setHgap(5.0);
		grid.setVgap(5.0);
		grid.add(new Label("Tempo restante: "), 0, 0);
		grid.add(remainingLbl, 1, 0);
		grid.add(taskTableView, 0, 1, 2, 1);
		final Button startBtn = new Button("Iniciar");
		grid.add(new HBox(startBtn, pauseBtn, stopBtn), 0, 2, 2, 1);

		startBtn.setOnAction(e -> actual.onEvent(START));
		pauseBtn.setOnAction(e -> actual.onEvent(PAUSE));
		stopBtn.setOnAction(e -> actual.onEvent(FINISH));

		final Button testBtn = new Button("Salvar");
		testBtn.setOnAction(e -> {
			em.getTransaction().commit();
			em.getTransaction().begin();
		});

		final Button clockBtn = new Button("Relógio");
		clockBtn.setOnAction(e -> {
			final Stage clock = new Stage();
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
					actual.onEvent(START);
					break;
				case STOPPED:
					actual.onEvent(PAUSE);
					break;
				default:
					break;
				}
			});
			miniPane.getRemainingLabel().textProperty().bind(remainingProp);
			clock.setScene(new Scene(miniPane));
			clock.sizeToScene();
			clock.toFront();
			Platform.runLater(clock::centerOnScreen);

			clock.show();

		});

		final Button reportBtn = new Button("Relatório");
		reportBtn.setOnAction(e -> {
			getHistory().forEach(st -> {
				final Task task = st.getTaskId() == null ? null : em.find(Task.class, st.getTaskId());
				System.out.println(String.format("%s - %s [%s > %s]", st.getTimeStamp(),
						task != null ? task.getName() : st.getTaskId(), st.getBefore(), st.getAfter()));
			});
		});

		grid.add(new HBox(testBtn, clockBtn, reportBtn), 0, 3, 2, 1);

		final ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHgrow(Priority.ALWAYS);
		final ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(cc1, cc2);

		final RowConstraints rc1 = new RowConstraints();
		final RowConstraints rc2 = new RowConstraints();
		rc2.setPrefHeight(200.0);
		final RowConstraints rc3 = new RowConstraints();
		grid.getRowConstraints().addAll(rc1, rc3, rc3);

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

		remainingLbl.textProperty().bind(remainingProp);

		new Thread(() -> {
			while (true) {
				long remaining = actual.getRemaining();
				if (actual.stateProperty().get().equals(PomodoroState.EXECUTING)
						|| actual.stateProperty().get().equals(PomodoroState.RESTING)) {
					remaining -= (System.currentTimeMillis() - actual.getLastUpdate());
				}
				if (remaining <= 0) {
					remaining = 0;
				}
				final long finalRemaining = remaining;
				final LocalTime remainTime = LocalTime.ofSecondOfDay(remaining / 1000);
				Platform.runLater(() -> remainingProp.set(remainTime.toString()));
				Platform.runLater(() -> {
					final double totalTime = actual.stateProperty().get().equals(PomodoroState.RESTING) ? 5.0 : 25.0;
					progressProp.set(1.0 - ((finalRemaining * 1.0) / (totalTime * 60.0 * 1000.0)));
				});
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
				}
			}
		}).start();

	}

	public StringProperty remainingProperty() {
		return remainingProp;
	}

	private void stopAllTasks() {
		Platform.runLater(() -> stopChildTasks(taskTableView.getRoot()));
	}

	private void stopChildTasks(TreeItem<TaskRow> treeItem) {
		treeItem.getChildren().forEach(ti -> {
			final ObjectProperty<TaskState> stateProperty = ti.getValue().stateProperty();
			if (stateProperty.get().equals(TaskState.EXECUTING)) {
				stateProperty.set(TaskState.STOPPED);
			}
			stopChildTasks(ti);
		});
	}

	private void playAlarm() {
		try {
			final AudioInputStream ais = AudioSystem
					.getAudioInputStream(getClass().getResourceAsStream("/META-INF/alarm.wav"));
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

	public void setItems(final ObservableList<TaskRow> items) {
		final TreeItem<TaskRow> root = new TreeItem<TaskRow>(new TaskRow(new Task()));
		taskTableView.setRoot(root);

		// Transformando em Map
		final Map<Long, TreeItem<TaskRow>> map = items.stream()
				.collect(Collectors.toMap(r -> r.getTask().getId(), r -> new TreeItem<TaskRow>(r)));

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
	}

	private TreeTableView<TaskRow> buildTaskTableView() {
		final TreeTableView<TaskRow> tableView = new TreeTableView<>();
		tableView.setShowRoot(false);

		// Tornar editavel
		tableView.setEditable(true);
		// Tornar celula selecionavel
		tableView.getSelectionModel().setCellSelectionEnabled(true);

		final TreeTableColumn<TaskRow, String> nameColumn = new TreeTableColumn<>("Tarefa");
		nameColumn.setPrefWidth(200.0);
		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		final TreeTableColumn<TaskRow, TaskState> stateColumn = new TreeTableColumn<>("Estado");
		stateColumn.setPrefWidth(100.0);
		stateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("state"));
		stateColumn.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(TaskState.values()));

		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(stateColumn);

		final KeyCodeCombination ctrlUp = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ctrlDown = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ctrlIns = new KeyCodeCombination(KeyCode.INSERT, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ins = new KeyCodeCombination(KeyCode.INSERT);
		final KeyCodeCombination del = new KeyCodeCombination(KeyCode.DELETE);

		tableView.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (ctrlUp.match(e) || ctrlDown.match(e) || ins.match(e) || del.match(e) || ctrlIns.match(e)) {
				final TreeItem<TaskRow> sel = tableView.getSelectionModel().getSelectedItem();
				final ObservableList<TreeItem<TaskRow>> items = sel != null ? sel.getParent().getChildren()
						: tableView.getRoot().getChildren();
				int indexOfSel = sel != null ? items.indexOf(sel) : -1;
				if (ctrlUp.match(e) && sel != null) {
					if (indexOfSel == 0) {
						return;
					}
					switchIdx(sel.getValue(), items.get(indexOfSel - 1).getValue());
					items.remove(sel);
					items.add(indexOfSel - 1, sel);
					tableView.getSelectionModel().select(sel);
				} else if (ctrlDown.match(e) && sel != null) {
					if (indexOfSel == items.size() - 1) {
						return;
					}
					switchIdx(sel.getValue(), items.get(indexOfSel + 1).getValue());
					items.remove(sel);
					items.add(indexOfSel + 1, sel);
					tableView.getSelectionModel().select(sel);
				} else if (ctrlIns.match(e) && sel != null) {
					final Task newTask = new Task();
					newTask.setIndex(taskRepo.getNextIndex());
					newTask.setParentId(sel.getValue().getTask().getId());
					final TreeItem<TaskRow> newItem = new TreeItem<TaskRow>(buildTaskRow(newTask));
					sel.setExpanded(true);
					sel.getChildren().add(newItem);
					em.persist(newTask);
					tableView.getSelectionModel().select(newItem);
				} else if (ins.match(e)) {
					final Task newTask = new Task();
					newTask.setIndex(taskRepo.getNextIndex());
					final TreeItem<TaskRow> newItem = new TreeItem<TaskRow>(buildTaskRow(newTask));
					TreeItem<TaskRow> parent = null;
					if (sel != null) {
						parent = sel.getParent();
						newItem.getValue().getTask().setParentId(parent.getValue().getTask().getId());
					} else {
						tableView.getRoot();
					}
					parent.getChildren().add(indexOfSel + 1, newItem);
					em.persist(newTask);
					tableView.getSelectionModel().select(newItem);
				} else if (del.match(e) && sel != null) {
					// TODO fazer cascata?
					sel.getParent().getChildren().remove(sel);
					em.remove(sel.getValue().getTask());
				}
			}
		});

		return tableView;
	}

	private TaskRow buildTaskRow(final Task task) {
		final TaskRow taskRow = new TaskRow(task);
		taskRow.stateProperty().addListener((r, o, n) -> em.persist(new TaskStateTransition(task.getId(), o, n)));
		return taskRow;
	}

	private void switchIdx(final TaskRow one, final TaskRow another) {
		final Long aux = one.getTask().getIndex();
		one.getTask().setIndex(another.getTask().getIndex());
		another.getTask().setIndex(aux);
	}

	public List<TaskStateTransition> getHistory() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TaskStateTransition> cq = cb.createQuery(TaskStateTransition.class);
		final Root<TaskStateTransition> taskTransition = cq.from(TaskStateTransition.class);
		cq.select(taskTransition).orderBy(cb.asc(taskTransition.get("timeStamp")));
		return em.createQuery(cq).getResultList();
	}

}
