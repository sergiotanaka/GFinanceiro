package org.pinguin.pomodoro.gui.main;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskRepository;
import org.pinguin.pomodoro.domain.task.TaskState;
import org.pinguin.pomodoro.domain.taskstatetransition.TaskStateTransition;
import org.pinguin.pomodoro.gui.description.DescriptionPane;

import com.google.inject.Injector;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.ComboBoxTreeTableCell;
import javafx.scene.control.cell.TextFieldTreeTableCell;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * TreeTableView de Tasks.
 */
public class TaskTreeTableView extends TreeTableView<TaskRow> {

	private static final DataFormat SERIALIZED_MIME_TYPE = buildMimeType();

	@Inject
	private Injector injector;
	@Inject
	private EntityManager em;
	@Inject
	private TaskRepository taskRepo;

	private final StringProperty executingTasks = new SimpleStringProperty();

	/**
	 * Construtor.
	 */
	public TaskTreeTableView() {
	}

	@Inject
	public void init() {
		this.setShowRoot(false);
		this.setMaxHeight(Double.MAX_VALUE);

		// Tornar editavel
		this.setEditable(true);
		// Tornar celula selecionavel
		this.getSelectionModel().setCellSelectionEnabled(true);
		this.setRowFactory(rowFactory());

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
						finishBtn.setOnAction(e -> {
							doneChilds(this.getTreeTableRow().getTreeItem());
							this.getTreeTableRow().getTreeItem().getValue().stateProperty().set(TaskState.DONE);
						});
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

		this.getColumns().add(nameColumn);
		this.getColumns().add(actionColumn);

		final KeyCodeCombination ctrlUp = new KeyCodeCombination(KeyCode.UP, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ctrlDown = new KeyCodeCombination(KeyCode.DOWN, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ctrlIns = new KeyCodeCombination(KeyCode.INSERT, KeyCombination.CONTROL_DOWN);
		final KeyCodeCombination ins = new KeyCodeCombination(KeyCode.INSERT);
		final KeyCodeCombination del = new KeyCodeCombination(KeyCode.DELETE);

		this.addEventHandler(KeyEvent.KEY_RELEASED, e -> {
			if (ctrlUp.match(e) || ctrlDown.match(e) || ins.match(e) || del.match(e) || ctrlIns.match(e)) {
				final TreeItem<TaskRow> sel = this.getSelectionModel().getSelectedItem();
				final ObservableList<TreeItem<TaskRow>> items = sel != null ? sel.getParent().getChildren()
						: this.getRoot().getChildren();
				int indexOfSel = sel != null ? items.indexOf(sel) : -1;
				if (ctrlUp.match(e) && sel != null) {
					if (indexOfSel == 0) {
						return;
					}
					switchIdx(sel.getValue(), items.get(indexOfSel - 1).getValue());
					items.remove(sel);
					items.add(indexOfSel - 1, sel);
					this.getSelectionModel().select(sel);
				} else if (ctrlDown.match(e) && sel != null) {
					if (indexOfSel == items.size() - 1) {
						return;
					}
					switchIdx(sel.getValue(), items.get(indexOfSel + 1).getValue());
					items.remove(sel);
					items.add(indexOfSel + 1, sel);
					this.getSelectionModel().select(sel);
				} else if (ctrlIns.match(e) && sel != null) {
					final Task newTask = new Task();
					newTask.setIndex(taskRepo.getNextIndex());
					newTask.setParentId(sel.getValue().getTask().getId());
					final TreeItem<TaskRow> newItem = new TreeItem<TaskRow>(buildTaskRow(newTask));
					sel.setExpanded(true);
					sel.getChildren().add(newItem);
					em.persist(newTask);
					this.getSelectionModel().select(newItem);
					this.edit(this.getSelectionModel().getSelectedIndex(), nameColumn);
				} else if (ins.match(e)) {
					// Deslocar os proximos
					final Task newTask = new Task();

					final TreeItem<TaskRow> newItem = new TreeItem<TaskRow>(buildTaskRow(newTask));
					TreeItem<TaskRow> parent = null;
					if (sel != null) {
						parent = sel.getParent();
						newItem.getValue().getTask().setParentId(parent.getValue().getTask().getId());
					} else {
						parent = this.getRoot();
					}
					parent.getChildren().add(indexOfSel + 1, newItem);
					// Deslocando os indices
					shift(indexOfSel, parent.getChildren());
					this.getSelectionModel().select(newItem);
					this.edit(this.getSelectionModel().getSelectedIndex(), nameColumn);
				} else if (del.match(e) && sel != null) {
					if (this.getEditingCell() == null) {
						// Fazer cascata?
						removeChilds(sel);
						sel.getParent().getChildren().remove(sel);
						em.remove(sel.getValue().getTask());
					}
				}
			}
		});

		// create a menu
		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem descrItem = new MenuItem("Descrição");
		descrItem.setOnAction(e -> {

			final TreeItem<TaskRow> sel = this.getSelectionModel().getSelectedItem();
			if (sel == null) {
				return;
			}

			final Stage stage = new Stage();
			stage.getIcons().add(new Image(MainPane.class.getResourceAsStream("/META-INF/256x256bb.jpg")));
			final DescriptionPane pane = injector.getInstance(DescriptionPane.class);
			pane.taskProperty().setValue(sel.getValue().getTask());
			pane.setCloseAction(c -> stage.close());
			stage.setScene(new Scene(pane));
			stage.setTitle("Descrição");
			stage.sizeToScene();
			stage.toFront();
			Platform.runLater(stage::centerOnScreen);
			stage.show();
		});
		contextMenu.getItems().add(descrItem);
		this.setContextMenu(contextMenu);

	}

	private void doneChilds(final TreeItem<TaskRow> ref) {
		for (TreeItem<TaskRow> child : ref.getChildren()) {
			doneChilds(child);
			child.getValue().stateProperty().set(TaskState.DONE);
		}
	}

	private void removeChilds(final TreeItem<TaskRow> ref) {
		for (TreeItem<TaskRow> child : ref.getChildren()) {
			removeChilds(child);
			em.remove(child.getValue().getTask());
		}
		ref.getChildren().clear();
	}

	private void shift(final int indexOfSel, final ObservableList<TreeItem<TaskRow>> list) {
		// Deslocando os indices
		for (int i = indexOfSel + 1; i < list.size() - 1; i++) {
			final Task task = list.get(i).getValue().getTask();
			final Task nextTask = list.get(i + 1).getValue().getTask();
			task.setIndex(nextTask.getIndex());
			em.persist(task);
		}
		// Ultima task
		final Task last = list.get(list.size() - 1).getValue().getTask();
		last.setIndex(taskRepo.getNextIndex());
		em.persist(last);

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

	private Callback<TreeTableView<TaskRow>, TreeTableRow<TaskRow>> rowFactory() {
		return new Callback<TreeTableView<TaskRow>, TreeTableRow<TaskRow>>() {

			@Override
			public TreeTableRow<TaskRow> call(TreeTableView<TaskRow> param) {

				TreeTableRow<TaskRow> row = new TreeTableRow<>();
				row.setOnDragDetected(event -> {
					if (!row.isEmpty()) {
						Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
						db.setDragView(row.snapshot(null, null));
						ClipboardContent cc = new ClipboardContent();
						cc.put(SERIALIZED_MIME_TYPE, row.getIndex());
						db.setContent(cc);
						event.consume();
					}
				});

				row.setOnDragOver(event -> {
					Dragboard db = event.getDragboard();
					if (acceptable(db, row)) {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						event.consume();
					}
				});

				row.setOnDragDropped(event -> {
					Dragboard db = event.getDragboard();
					if (acceptable(db, row)) {
						int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
						final TreeItem<TaskRow> item = TaskTreeTableView.this.getTreeItem(index);
						item.getParent().getChildren().remove(item);
						final TreeItem<TaskRow> target = getTarget(row);
						target.getChildren().add(item);
						event.setDropCompleted(true);
						TaskTreeTableView.this.getSelectionModel().select(item);
						event.consume();

						item.getValue().getTask().setParentId(target.getValue().getTask().getId());
					}
				});
				return row;
			}
		};
	}

	private boolean acceptable(Dragboard db, TreeTableRow<TaskRow> row) {
		boolean result = false;
		if (db.hasContent(SERIALIZED_MIME_TYPE)) {
			int index = (Integer) db.getContent(SERIALIZED_MIME_TYPE);
			if (row.getIndex() != index) {
				TreeItem<TaskRow> target = getTarget(row);
				TreeItem<TaskRow> item = this.getTreeItem(index);
				result = !isParent(item, target);
			}
		}
		return result;
	}

	private TreeItem<TaskRow> getTarget(TreeTableRow<TaskRow> row) {
		TreeItem<TaskRow> target = this.getRoot();
		if (!row.isEmpty()) {
			target = row.getTreeItem();
		}
		return target;
	}

	// prevent loops in the tree
	private boolean isParent(TreeItem<TaskRow> parent, TreeItem<TaskRow> child) {
		boolean result = false;
		while (!result && child != null) {
			result = child.getParent() == parent;
			child = child.getParent();
		}
		return result;
	}

	public void updateExecutingTasks() {
		final StringBuilder sb = new StringBuilder();
		getAllTaskTreeItems(this.getRoot()).forEach(ti -> {
			if (ti.getValue().getTask().getState().equals(TaskState.EXECUTING)) {
				sb.append(ti.getValue().getTask().getName()).append(System.lineSeparator());
			}
		});
		Platform.runLater(() -> executingTasks.set(sb.toString()));
	}

	private List<TreeItem<TaskRow>> getAllTaskTreeItems(TreeItem<TaskRow> ref) {
		final List<TreeItem<TaskRow>> result = new ArrayList<>();
		ref.getChildren().forEach(ti -> {
			result.add(ti);
			result.addAll(getAllTaskTreeItems(ti));
		});
		return result;
	}

	private static DataFormat buildMimeType() {
		DataFormat mimeType = DataFormat.lookupMimeType("application/x-java-serialized-object");
		if (mimeType == null) {
			mimeType = new DataFormat("application/x-java-serialized-object");
		}
		return mimeType;
	}

}
