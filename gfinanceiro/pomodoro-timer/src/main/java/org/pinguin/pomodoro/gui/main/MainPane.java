package org.pinguin.pomodoro.gui.main;

import java.time.LocalTime;

import org.pinguin.pomodoro.domain.task.Task;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
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
import javafx.util.converter.DefaultStringConverter;

public class MainPane extends BorderPane {

	private final TableView<TaskRow> taskTableView;
	private final Label remainingLbl = new Label("25:00");
	private final Button cancelBtn = new Button("Cancelar");

	private boolean executing = false;

	private long duration = 25 * 60 * 1000;
	private long start = 0;

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
		grid.add(new HBox(startBtn), 0, 2, 2, 1);

		startBtn.setOnAction(e -> {
			if (executing) {
				executing = false;
			} else {
				executing = true;
				start = System.currentTimeMillis();
				new Thread(() -> {
					while (executing) {
						long remaining = duration - (System.currentTimeMillis() - start);
						LocalTime remainTime = LocalTime.ofSecondOfDay(remaining / 1000);
						Platform.runLater(() -> remainingLbl.textProperty().set(remainTime.toString()));
						if (remaining <= 0) {
							break;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e1) {
						}
					}
				}).start();
			}
		});

		final Button testBtn = new Button("Teste");
		testBtn.setOnAction(e -> System.out.println(taskTableView.getItems()));

		grid.add(testBtn, 0, 3, 2, 1);

		final ColumnConstraints cc1 = new ColumnConstraints();
		cc1.setHgrow(Priority.NEVER);
		final ColumnConstraints cc2 = new ColumnConstraints();
		cc2.setHgrow(Priority.ALWAYS);
		grid.getColumnConstraints().addAll(cc1, cc2);

		this.centerProperty().set(grid);
	}

	public void setItems(final ObservableList<TaskRow> items) {
		taskTableView.setItems(items);
	}

	private TableView<TaskRow> buildTaskTableView() {
		final TableView<TaskRow> tableView = new TableView<>();
		// Tornar editavel
		tableView.setEditable(true);
		// Tornar celula selecionavel
		tableView.getSelectionModel().setCellSelectionEnabled(true);

		final TableColumn<TaskRow, String> nameColumn = new TableColumn<>("Tarefa");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		nameColumn.setCellFactory(tc -> new TextFieldTableCell<>(new DefaultStringConverter()));

		final TableColumn<TaskRow, Boolean> doneColumn = new TableColumn<>("Pronto");
		doneColumn.setCellValueFactory(new PropertyValueFactory<>("done"));
		doneColumn.setCellFactory(CheckBoxTableCell.forTableColumn(doneColumn));

		tableView.getColumns().add(nameColumn);
		tableView.getColumns().add(doneColumn);

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
					tableView.getItems().remove(sel);
					tableView.getItems().add(indexOfSel - 1, sel);
				} else if (ctrlDown.match(e)) {
					if (indexOfSel == tableView.getItems().size() - 1) {
						return;
					}
					tableView.getItems().remove(sel);
					tableView.getItems().add(indexOfSel + 1, sel);
				}
			}
		});

		tableView.onKeyPressedProperty().set(e -> {
			if (e.getCode().equals(KeyCode.INSERT)) {
				tableView.getItems().add(new TaskRow(new Task()));
			} else if (e.getCode().equals(KeyCode.DELETE)) {
				final TaskRow sel = tableView.getSelectionModel().getSelectedItem();
				if (sel != null) {
					tableView.getItems().remove(sel);
				}
			}
		});

		return tableView;
	}

}
