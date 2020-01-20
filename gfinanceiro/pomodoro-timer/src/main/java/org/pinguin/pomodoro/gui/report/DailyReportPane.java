package org.pinguin.pomodoro.gui.report;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

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
		try {
			this.setPadding(new Insets(10.0));
			tableView = buildTTableView();
			tableView.setShowRoot(false);
			final TreeItem<DailyReportRow> root = new TreeItem<DailyReportRow>(new DailyReportRow());
			tableView.setRoot(root);

			this.setCenter(tableView);
		} catch (Exception e) {
			throw e;
		}
	}

	@Inject
	private void init() {
		try {
			final List<DailyReportItem> report = reportService.retrieveDailyReport();
			Collections.sort(report, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
			for (final DailyReportItem item : report) {
				final TreeItem<DailyReportRow> dayTreeItem = new TreeItem<DailyReportRow>(
						new DailyReportRow(item.getDate(), "", null, null, item.getDuration()));
				for (DailyReportItem taskItem : item.getSubItems()) {
					final TreeItem<DailyReportRow> taskTreeItem = new TreeItem<DailyReportRow>(new DailyReportRow(null,
							resolveTaskName(taskItem.getTaskId()), null, null, taskItem.getDuration()));
					for (DailyReportItem periodItem : taskItem.getSubItems()) {
						taskTreeItem.getChildren().add(new TreeItem<DailyReportRow>(new DailyReportRow(null, null,
								periodItem.getStart(), periodItem.getEnd(), periodItem.getDuration())));
					}
					dayTreeItem.getChildren().add(taskTreeItem);
				}

				tableView.getRoot().getChildren().add(dayTreeItem);
			}
		} catch (Exception e) {
			throw e;
		}
	}

	private String parentPath(final Long parentTaskId) {
		if (parentTaskId == null) {
			return "";
		}
		final Task parent = em.find(Task.class, parentTaskId);
		if (parent == null) {
			return "";
		}
		String path = parentPath(parent.getParentId());
		return path.isEmpty() ? parent.getName() : path + "/" + parent.getName();
	}

	private String resolveTaskName(final Long taskId) {
		final Task task = em.find(Task.class, taskId);
		if (task == null) {
			return "Tarefa excluída [" + taskId + "]";
		}
		final String path = parentPath(task.getParentId());
		return path.isEmpty() ? task.getName() : path + "/" + task.getName();
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

		final TreeTableColumn<DailyReportRow, LocalDateTime> startColumn = new TreeTableColumn<>("Início");
		startColumn.setPrefWidth(50.0);
		startColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("start"));
		startColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return new TreeTableCell<DailyReportRow, LocalDateTime>() {
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

		final TreeTableColumn<DailyReportRow, LocalDateTime> endColumn = new TreeTableColumn<>("Fim");
		endColumn.setPrefWidth(50.0);
		endColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("end"));
		endColumn.setCellFactory(e -> {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			return new TreeTableCell<DailyReportRow, LocalDateTime>() {
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

		final TreeTableColumn<DailyReportRow, Long> durationColumn = new TreeTableColumn<>("Duração");
		durationColumn.setPrefWidth(50.0);
		durationColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("duration"));
		durationColumn.setCellFactory(e -> {
			return new TreeTableCell<DailyReportRow, Long>() {
				@Override
				protected void updateItem(Long item, boolean empty) {
					super.updateItem(item, empty);
					if (item == null) {
						super.setText(null);
						super.setGraphic(null);
					} else {
						long hours = item / 60;
						long minutes = item % 60;
						super.setText(String.format("%02d:%02d", hours, minutes));
						super.setGraphic(null);
					}
				}
			};
		});

		tTableView.getColumns().addAll(dateColumn, taskNameColumn, startColumn, endColumn, durationColumn);

		return tTableView;
	}

}
