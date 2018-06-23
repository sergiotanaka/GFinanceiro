package org.pinguin.pomodoro.gui.report;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.pinguin.pomodoro.domain.task.TaskState;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class ReportPane extends BorderPane {

	private final TableView<ReportRow> tableView;

	public ReportPane() {
		this.setPadding(new Insets(10.0));
		tableView = buildTableView();
		this.setCenter(tableView);
	}

	@SuppressWarnings("unchecked")
	private TableView<ReportRow> buildTableView() {
		final TableView<ReportRow> tableView = new TableView<>();

		final TableColumn<ReportRow, LocalDateTime> timestampColumn = new TableColumn<>("Data/Hora");
		timestampColumn.setPrefWidth(90.0);
		timestampColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
		timestampColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");
			return new TableCell<ReportRow, LocalDateTime>() {
				@Override
				protected void updateItem(LocalDateTime item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						super.setText(null);
						super.setGraphic(null);
					} else {
						super.setText(item.format(formatter));
						super.setGraphic(null);
					}
				}
			};
		});

		final TableColumn<ReportRow, String> nameColumn = new TableColumn<>("Tarefa");
		nameColumn.setPrefWidth(300.0);
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		final TableColumn<ReportRow, TaskState> beforeColumn = new TableColumn<>("Antes");
		beforeColumn.setPrefWidth(100.0);
		beforeColumn.setCellValueFactory(new PropertyValueFactory<>("before"));

		final TableColumn<ReportRow, TaskState> afterColumn = new TableColumn<>("Estado");
		afterColumn.setPrefWidth(100.0);
		afterColumn.setCellValueFactory(new PropertyValueFactory<>("after"));

		tableView.getColumns().addAll(timestampColumn, nameColumn, afterColumn);

		return tableView;
	}

	public void setItems(final ObservableList<ReportRow> items) {
		tableView.setItems(items);
	}

}
