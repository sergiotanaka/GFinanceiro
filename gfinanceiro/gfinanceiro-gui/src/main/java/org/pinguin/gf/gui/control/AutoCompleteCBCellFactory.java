package org.pinguin.gf.gui.control;

import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class AutoCompleteCBCellFactory<S, T> implements Callback<TableColumn<S, T>, TableCell<S, T>> {

	private ObservableList<T> items;
	private StringConverter<T> converter;

	public AutoCompleteCBCellFactory(final StringConverter<T> converter, final ObservableList<T> items) {
		this.converter = converter;
		this.items = items;
	}

	@Override
	public TableCell<S, T> call(TableColumn<S, T> param) {
		final TableCell<S, T> cell = new TableCell<S, T>() {

			private AutoCompleteComboBox<T> comboBox = null;

			/** {@inheritDoc} */
			@Override
			public void startEdit() {
				if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
					return;
				}
				if (comboBox == null) {
					comboBox = new AutoCompleteComboBox<>();
					comboBox.setConverter(converter);
					comboBox.setOriginalItems(items);
					comboBox.onActionProperty()
							.set(e -> this.commitEdit(comboBox.getSelectionModel().getSelectedItem()));
				}

				comboBox.getSelectionModel().select(getItem());

				super.startEdit();
				setText(null);
				setGraphic(comboBox);
			}

			/** {@inheritDoc} */
			@Override
			public void cancelEdit() {
				super.cancelEdit();

				setText(converter.toString(getItem()));
				setGraphic(null);
			}

			/** {@inheritDoc} */
			@Override
			public void updateItem(T item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					this.setText(null);
					this.setGraphic(null);
				} else {
					if (this.isEditing()) {
						if (comboBox != null) {
							comboBox.getSelectionModel().select(getItem());
						}
						this.setText(null);
						if (getGraphic() == null) {
							this.setGraphic(comboBox);
						}
					} else {
						this.setText(converter.toString(getItem()));
						this.setGraphic(null);
					}
				}
			}

			@Override
			public void commitEdit(T newValue) {
				super.commitEdit(newValue);
			}

		};

		return cell;
	}

	public static <S, T> AutoCompleteCBCellFactory<S, T> forTableColumn(final StringConverter<T> converter,
			final ObservableList<T> items) {
		return new AutoCompleteCBCellFactory<S, T>(converter, items);
	}

}
