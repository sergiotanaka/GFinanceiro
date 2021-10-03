package org.pinguin.gf.gui.control;

import static org.apache.commons.lang3.StringUtils.stripAccents;

import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class AutoCompleteComboBox<T> extends ComboBox<T> {

	private final ObservableList<T> originalItems = FXCollections.observableArrayList();

	/** Filtro digitado pelo usuario. */
	private final StringProperty filter = new SimpleStringProperty("");

	public AutoCompleteComboBox() {
		super();
		this.setTooltip(new Tooltip());
		this.getTooltip().textProperty().bind(filter);
		this.setOnKeyPressed(this::handleOnKeyPressed);
		this.setOnHidden(this::handleOnHiding);
		this.filter.addListener((observable, oldValue, newValue) -> handleFilterChanged(newValue));
		this.originalItems.addListener(new ListChangeListener<T>() {
			@Override
			public void onChanged(Change<? extends T> c) {
				List<T> filtered = filter(filter.get(), originalItems);
				getItems().setAll(filtered);
			}
		});
	}

	public AutoCompleteComboBox(ObservableList<T> originalItems) {
		this();
		setOriginalItems(originalItems);
	}

	public void setOriginalItems(ObservableList<T> originalItems) {
		Bindings.bindContentBidirectional(this.originalItems, originalItems);
		getItems().setAll(originalItems);
	}

	/**
	 * Captura a tecla pressionada. Atualiza o filtro com o valor inserido.
	 * 
	 * @param e
	 */
	public void handleOnKeyPressed(KeyEvent e) {
		// Capturando a tecla
		final KeyCode code = e.getCode();
		String filterValue = filter.get();
		if (code.isLetterKey()) {
			filterValue += e.getText();
		}
		if (code == KeyCode.BACK_SPACE && filterValue.length() > 0) {
			filterValue = filterValue.substring(0, filterValue.length() - 1);
			getItems().setAll(originalItems);
		}
		if (code == KeyCode.ESCAPE) {
			filterValue = "";
		}
		filter.setValue(filterValue);
	}

	public void handleOnHiding(Event e) {
		filter.setValue("");
		getTooltip().hide();
		restoreOriginalItems();
	}

	private void handleFilterChanged(final String newValue) {
		if (!newValue.isEmpty()) {
			show();
			if (newValue.isEmpty()) {
				restoreOriginalItems();
			} else {
				showTooltip();
				getItems().setAll(filter(newValue, originalItems));
			}
		} else {
			getTooltip().hide();
		}
	}

	private void showTooltip() {
		if (!getTooltip().isShowing()) {
			Window stage = getScene().getWindow();
			double posX = stage.getX() + localToScene(getBoundsInLocal()).getMinX() + 4;
			double posY = stage.getY() + localToScene(getBoundsInLocal()).getMinY() - 29;
			getTooltip().show(stage, posX, posY);
		}
	}

	private List<T> filter(final String filter, final List<T> original) {

		if (filter.length() == 0) {
			return original;
		}

		final String filterLower = filter.toString().toLowerCase();

		return original.stream().filter(element -> {
			final StringConverter<T> converter = getConverter();

			if (converter == null) {
				return stripAccents(element.toString().toLowerCase()).contains(filterLower);
			} else {
				return stripAccents(converter.toString(element).toLowerCase()).contains(filterLower);
			}
		}).collect(Collectors.toList());
	}

	private void restoreOriginalItems() {
		T s = getSelectionModel().getSelectedItem();
		getItems().setAll(originalItems);
		getSelectionModel().select(s);
	}
}
