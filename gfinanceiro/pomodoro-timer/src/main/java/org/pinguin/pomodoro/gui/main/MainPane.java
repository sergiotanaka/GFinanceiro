package org.pinguin.pomodoro.gui.main;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent;
import org.pinguin.pomodoro.domain.pomodoro.PomodoroState;
import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.task.TaskState;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.domain.transition.Transition;
import org.pinguin.pomodoro.gui.mini.MiniPane;
import org.pinguin.pomodoro.gui.report.DailyReportPane;
import org.pinguin.pomodoro.gui.report.ReportPane;
import org.pinguin.pomodoro.gui.report.ReportRow;
import org.pinguin.pomodoro.gui.timer.Timer.State;

import com.google.inject.Injector;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
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
import javafx.stage.StageStyle;
import javafx.util.Callback;

public class MainPane extends BorderPane {

	private Runnable callFocus;

	private final DoubleProperty progressProp = new SimpleDoubleProperty(0);

	@Inject
	private Injector injector;
	@Inject
	private EntityManager em;
	@Inject
	private TaskRepository taskRepo;

	private final TreeTableView<TaskRow> taskTableView;
	private final Label remainingLbl = new Label();
	private final Button pauseBtn = new Button("Pausar");
	private final Button stopBtn = new Button("Parar");

	private final StringProperty remainingProp = new SimpleStringProperty();
	private final StringProperty executingTasks = new SimpleStringProperty();

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

		startBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.START));
		pauseBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.PAUSE));
		stopBtn.setOnAction(e -> actual.onEvent(PomodoroEvent.FINISH));

		final Button testBtn = new Button("Salvar");
		testBtn.setOnAction(e -> {
			em.getTransaction().commit();
			em.getTransaction().begin();
		});

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

		grid.add(new HBox(testBtn, clockBtn, reportBtn, dailyReportBtn), 0, 3, 2, 1);

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
		}).start();

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
		nameColumn.setPrefWidth(300.0);
		nameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
		nameColumn.setCellFactory(TextFieldTreeTableCell.forTreeTableColumn());

		final TreeTableColumn<TaskRow, TaskState> stateColumn = new TreeTableColumn<>("Estado");
		stateColumn.setPrefWidth(100.0);
		stateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("state"));
		stateColumn.setCellFactory(ComboBoxTreeTableCell.forTreeTableColumn(TaskState.values()));

		final TreeTableColumn<TaskRow, TaskState> actionColumn = new TreeTableColumn<>("Estado");
		actionColumn.setPrefWidth(160.0);
		actionColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("state"));
		final Callback<TreeTableColumn<TaskRow, TaskState>, TreeTableCell<TaskRow, TaskState>> actionColCellFactory = new Callback<TreeTableColumn<TaskRow, TaskState>, TreeTableCell<TaskRow, TaskState>>() {

			@Override
			public TreeTableCell<TaskRow, TaskState> call(TreeTableColumn<TaskRow, TaskState> param) {
				final TreeTableCell<TaskRow, TaskState> cell = new TreeTableCell<TaskRow, TaskState>() {
					final HBox hBox = new HBox(5.0);
					final Button execBtn = new Button();
					final Button finishBtn = new Button("Concluir");
					{
						hBox.getChildren().addAll(execBtn, finishBtn);
						execBtn.setOnAction(e -> {
							final TaskRow taskRow = this.getTreeTableRow().getTreeItem().getValue();
							if (taskRow.stateProperty().get().equals(TaskState.STOPPED)) {
								taskRow.stateProperty().set(TaskState.EXECUTING);
							} else if (taskRow.stateProperty().get().equals(TaskState.EXECUTING)) {
								taskRow.stateProperty().set(TaskState.STOPPED);
							}
						});
						finishBtn.setOnAction(e -> this.getTreeTableRow().getTreeItem().getValue().stateProperty()
								.set(TaskState.DONE));
					}

					@Override
					public void updateItem(TaskState item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
							setText(null);
						} else {
							if (item.equals(TaskState.STOPPED) || item.equals(TaskState.DONE)) {
								execBtn.setText("Executar");
							} else if (item.equals(TaskState.EXECUTING)) {
								execBtn.setText("Parar");
							}
							execBtn.setDisable(item.equals(TaskState.DONE));
							finishBtn.setDisable(item.equals(TaskState.DONE));

							setGraphic(hBox);
							setText(null);
						}
					}
				};
				return cell;
			}
		};
		actionColumn.setCellFactory(actionColCellFactory);

		tableView.getColumns().add(nameColumn);
		// tableView.getColumns().add(stateColumn);
		tableView.getColumns().add(actionColumn);

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
		taskRow.stateProperty().addListener((r, o, n) -> {
			em.persist(new TaskStateTransition(task.getId(), o, n));
			updateExecutingTasks();
		});
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
