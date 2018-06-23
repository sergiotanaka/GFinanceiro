package org.pinguin.pomodoro.gui.report;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.service.report.ReportService;
import org.pinguin.pomodoro.service.report.ReportService.DailyReportItem;

import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.layout.BorderPane;

public class DailyReportPane extends BorderPane {

	@Inject
	private EntityManager em;
	@Inject
	private ReportService reportService;
	private final TreeTableView<DailyReportRow> tableView;

	public DailyReportPane() {
		this.setPadding(new Insets(10.0));
		tableView = buildTTableView();
		tableView.setShowRoot(false);
		final TreeItem<DailyReportRow> root = new TreeItem<DailyReportRow>(new DailyReportRow());
		tableView.setRoot(root);

		this.setCenter(tableView);
	}

	@Inject
	private void init() {

		for (DailyReportItem item : reportService.retrieveDailyReport()) {
			final TreeItem<DailyReportRow> dayTreeItem = new TreeItem<DailyReportRow>(
					new DailyReportRow(item.getDate(), "", null, null));
			for (DailyReportItem taskItem : item.getSubItems()) {
				final Task task = em.find(Task.class, taskItem.getTaskId());
				final TreeItem<DailyReportRow> taskTreeItem = new TreeItem<DailyReportRow>(
						new DailyReportRow(null, task.getName(), null, null));
				for (DailyReportItem periodItem : taskItem.getSubItems()) {
					taskTreeItem.getChildren().add(new TreeItem<DailyReportRow>(
							new DailyReportRow(null, null, periodItem.getStart(), periodItem.getEnd())));
				}

				dayTreeItem.getChildren().add(taskTreeItem);
			}

			tableView.getRoot().getChildren().add(dayTreeItem);
		}
	}

	@SuppressWarnings("unchecked")
	private TreeTableView<DailyReportRow> buildTTableView() {
		final TreeTableView<DailyReportRow> tTableView = new TreeTableView<>();

		final TreeTableColumn<DailyReportRow, LocalDate> dateColumn = new TreeTableColumn<>("Data");
		dateColumn.setPrefWidth(90.0);
		dateColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("date"));
		dateColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
			return new TreeTableCell<DailyReportRow, LocalDate>() {
				@Override
				protected void updateItem(LocalDate item, boolean empty) {
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

		final TreeTableColumn<DailyReportRow, String> taskNameColumn = new TreeTableColumn<>("Tarefa");
		taskNameColumn.setPrefWidth(300.0);
		taskNameColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("taskName"));

		final TreeTableColumn<DailyReportRow, LocalTime> startColumn = new TreeTableColumn<>("Início");
		startColumn.setPrefWidth(50.0);
		startColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("start"));
		startColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return new TreeTableCell<DailyReportRow, LocalTime>() {
				@Override
				protected void updateItem(LocalTime item, boolean empty) {
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

		final TreeTableColumn<DailyReportRow, LocalTime> endColumn = new TreeTableColumn<>("Fim");
		endColumn.setPrefWidth(50.0);
		endColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("end"));
		endColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return new TreeTableCell<DailyReportRow, LocalTime>() {
				@Override
				protected void updateItem(LocalTime item, boolean empty) {
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

		tTableView.getColumns().addAll(dateColumn, taskNameColumn, startColumn, endColumn);

		return tTableView;
	}

}
