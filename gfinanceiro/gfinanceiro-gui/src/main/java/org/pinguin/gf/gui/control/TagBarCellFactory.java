package org.pinguin.gf.gui.control;

import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class TagBarCellFactory<S, T>
		implements Callback<TableColumn<S, ObservableList<T>>, TableCell<S, ObservableList<T>>> {

	private ObservableList<T> candidates;
	private StringConverter<T> converter;

	public TagBarCellFactory(final StringConverter<T> converter, final ObservableList<T> candidates) {
		this.converter = converter;
		this.candidates = candidates;
	}

	public static <S, T> TagBarCellFactory<S, T> forTableColumn(final StringConverter<T> converter,
			final ObservableList<T> items) {
		return new TagBarCellFactory<S, T>(converter, items);
	}

	@Override
	public TableCell<S, ObservableList<T>> call(TableColumn<S, ObservableList<T>> param) {
		final TableCell<S, ObservableList<T>> cell = new TableCell<S, ObservableList<T>>() {

			private TagBar<T> tagBar = null;

			/** {@inheritDoc} */
			@Override
			public void startEdit() {
				if (!isEditable() || !getTableView().isEditable() || !getTableColumn().isEditable()) {
					return;
				}
				if (tagBar == null) {
					tagBar = new TagBar<>();
					tagBar.setConverter(converter);
					tagBar.setCandidates(candidates);
					tagBar.onActionProperty()
							.set(e -> this.commitEdit(FXCollections.observableArrayList(tagBar.getTags())));
				}
				tagBar.setTags(FXCollections.observableArrayList(getItem()));

				super.startEdit();
				setText(null);
				setGraphic(tagBar);
			}

			/** {@inheritDoc} */
			@Override
			public void cancelEdit() {
				super.cancelEdit();

				setText(getItem().stream().map(t -> converter.toString(t)).collect(Collectors.joining(" ")));
				setGraphic(null);
			}

			/** {@inheritDoc} */
			@Override
			public void updateItem(ObservableList<T> item, boolean empty) {
				super.updateItem(item, empty);
				if (empty) {
					this.setText(null);
					this.setGraphic(null);
				} else {
					if (this.isEditing()) {
						if (tagBar != null) {
							tagBar.setTags(FXCollections.observableArrayList(getItem()));
						}
						this.setText(null);
						if (getGraphic() == null) {
							this.setGraphic(tagBar);
						}

					} else {
						this.setText(
								getItem().stream().map(t -> converter.toString(t)).collect(Collectors.joining(" ")));
						this.setGraphic(null);
					}
				}
			}

			@Override
			public void commitEdit(ObservableList<T> newValue) {
				super.commitEdit(newValue);
			}

		};

		return cell;
	}

}
