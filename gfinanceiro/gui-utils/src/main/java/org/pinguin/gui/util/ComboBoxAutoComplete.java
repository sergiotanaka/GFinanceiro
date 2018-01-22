package org.pinguin.gui.util;

import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Window;
import javafx.util.StringConverter;

public class ComboBoxAutoComplete<T> {

	private ComboBox<T> cmb;
	private String filter = "";
	private final ObservableList<T> originalItems = FXCollections.observableArrayList();

	public ComboBoxAutoComplete(ComboBox<T> cmb) {
		this.cmb = cmb;
		cmb.setTooltip(new Tooltip());
		cmb.setOnKeyPressed(this::handleOnKeyPressed);
		cmb.setOnHidden(this::handleOnHiding);
	}

	public ObservableList<T> getOriginalItems() {
		return originalItems;
	}

	public void handleOnKeyPressed(KeyEvent e) {
		ObservableList<T> filteredList = FXCollections.observableArrayList();
		KeyCode code = e.getCode();

		if (code.isLetterKey()) {
			filter += e.getText();
		}
		if (code == KeyCode.BACK_SPACE && filter.length() > 0) {
			filter = filter.substring(0, filter.length() - 1);
			cmb.getItems().setAll(originalItems);
		}
		if (code == KeyCode.ESCAPE) {
			filter = "";
		}
		if (filter.length() == 0) {
			filteredList.clear();
			filteredList.addAll(originalItems);
			cmb.getTooltip().hide();
		} else {
			Stream<T> itens = cmb.getItems().stream();
			String txtUsr = filter.toString().toLowerCase();
			itens.filter(el -> {
				final StringConverter<T> converter = cmb.getConverter();
				if (converter == null) {
					return el.toString().toLowerCase().contains(txtUsr);
				} else {
					return converter.toString(el).toLowerCase().contains(txtUsr);
				}
			}).forEach(filteredList::add);
			cmb.getTooltip().setText(txtUsr);
			Window stage = cmb.getScene().getWindow();
			double posX = stage.getX() + cmb.getBoundsInParent().getMinX();
			double posY = stage.getY() + cmb.getBoundsInParent().getMinY();
			cmb.getTooltip().show(stage, posX, posY);
			cmb.show();
		}
		cmb.getItems().setAll(filteredList);
	}

	public void handleOnHiding(Event e) {
		filter = "";
		cmb.getTooltip().hide();
		T s = cmb.getSelectionModel().getSelectedItem();
		cmb.getItems().setAll(originalItems);
		cmb.getSelectionModel().select(s);
	}

}