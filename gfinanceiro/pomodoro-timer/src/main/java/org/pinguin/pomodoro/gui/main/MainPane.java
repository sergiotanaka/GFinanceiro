package org.pinguin.pomodoro.gui.main;

import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.FINISH;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.PAUSE;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.START;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.EXECUTING;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.RESTING;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.pinguin.pomodoro.domain.pomodoro.Pomodoro;
import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.task.TaskState;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.domain.transition.Transition;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
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
import javafx.util.converter.DefaultStringConverter;

public class MainPane extends BorderPane {

	private Runnable callFocus;
	private Consumer<String> updateRemaining;

	@Inject
	private EntityManager em;
	@Inject
	private TaskRepository taskRepo;

	private final TableView<TaskRow> taskTableView;
	private final Label remainingLbl = new Label("25:00");
	private final Button pauseBtn = new Button("Pausar");
	private final Button stopBtn = new Button("Parar");

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

		startBtn.setOnAction(e -> {
			actual.onEvent(START);
			new Thread(() -> {
				while (actual.getState().equals(EXECUTING) || actual.getState().equals(RESTING)) {
					actual.updateRemaining();
					final LocalTime remainTime = LocalTime.ofSecondOfDay(actual.getRemaining() / 1000);
					Platform.runLater(() -> remainingLbl.textProperty().set(remainTime.toString()));
					if (updateRemaining != null) {
						updateRemaining.accept(remainTime.toString());
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
					}
				}
			}).start();
		});
		pauseBtn.setOnAction(e -> actual.onEvent(PAUSE));
		stopBtn.setOnAction(e -> {
			actual.onEvent(FINISH);
			new Thread(() -> {
				while (actual.getState().equals(RESTING)) {
					actual.updateRemaining();
					LocalTime remainTime = LocalTime.ofSecondOfDay(actual.getRemaining() / 1000);
					Platform.runLater(() -> remainingLbl.textProperty().set(remainTime.toString()));
					if (updateRemaining != null) {
						updateRemaining.accept(remainTime.toString());
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e1) {
					}
				}
			}).start();
		});

		final Button testBtn = new Button("Salvar");
		testBtn.setOnAction(e -> {
			em.getTransaction().commit();
			em.getTransaction().begin();
		});

		grid.add(testBtn, 0, 3, 2, 1);

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

		this.actual.addListener((b, a) -> em.persist(new Transition(b, a)));
		this.actual.setOnTimeout(() -> {
			if (callFocus != null) {
				callFocus.run();
			}
			playAlarm();
		});
	}

	private void playAlarm() {
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(getClass().getResourceAsStream("/META-INF/alarm.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(ais);
			clip.start();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public void setCallFocus(Runnable callFocus) {
		this.callFocus = callFocus;
	}

	public void setUpdateRemaining(Consumer<String> updateRemaining) {
		this.updateRemaining = updateRemaining;
	}

	public void setItems(final ObservableList<TaskRow> items) {
		taskTableView.setItems(items);
	}

	private TableView<TaskRow> buildTaskTableView() {
		final TableView<TaskRow> tableView = new TableView<>();
		// tableView.setPrefHeight(200.0);
		// tableView.setMaxHeight(400.0);

		// Tornar editavel
		tableView.setEditable(true);
		// Tornar celula selecionavel
		tableView.getSelectionModel().setCellSelectionEnabled(true);

		final TableColumn<TaskRow, String> nameColumn = new TableColumn<>("Tarefa");
		nameColumn.setPrefWidth(200.0);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setCellFactory(tc -> new TextFieldTableCell<>(new DefaultStringConverter()));

		final TableColumn<TaskRow, TaskState> stateColumn = new TableColumn<>("Estado");
		stateColumn.setPrefWidth(100.0);
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
		stateColumn.setCellFactory(ComboBoxTableCell.forTableColumn(TaskState.values()));

		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(stateColumn);

		final KeyCodeCombination ctrlUp = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ctrlDown = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
		tableView.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (ctrlUp.match(e) || ctrlDown.match(e)) {
				final TaskRow sel = tableView.getSelectionModel().getSelectedItem();
				if (sel == null) {
					return;
				}
				int indexOfSel = tableView.getItems().indexOf(sel);
				if (ctrlUp.match(e)) {
					if (indexOfSel == 0) {
						return;
					}
					switchIdx(sel, tableView.getItems().get(indexOfSel - 1));
					tableView.getItems().remove(sel);
					tableView.getItems().add(indexOfSel - 1, sel);
				} else if (ctrlDown.match(e)) {
					if (indexOfSel == tableView.getItems().size() - 1) {
						return;
					}
					switchIdx(sel, tableView.getItems().get(indexOfSel + 1));
					tableView.getItems().remove(sel);
					tableView.getItems().add(indexOfSel + 1, sel);
				}
			}
		});

		tableView.onKeyPressedProperty().set(e -> {
			if (e.getCode().equals(KeyCode.INSERT)) {
				final Task newTask = new Task();
				newTask.setIndex(taskRepo.getNextIndex());
				tableView.getItems().add(buildTaskRow(newTask));
				em.persist(newTask);
			} else if (e.getCode().equals(KeyCode.DELETE)) {
				final TaskRow sel = tableView.getSelectionModel().getSelectedItem();
				if (sel != null) {
					tableView.getItems().remove(sel);
					em.remove(sel.getTask());
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

	public List<Transition> getAllTransitions() {
		CriteriaQuery<Transition> cq = em.getCriteriaBuilder().createQuery(Transition.class);
		cq.select(cq.from(Transition.class));
		return em.createQuery(cq).getResultList();
	}

}
