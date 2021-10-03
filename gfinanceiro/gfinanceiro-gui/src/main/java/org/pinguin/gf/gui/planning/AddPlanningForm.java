package org.pinguin.gf.gui.planning;

import java.io.IOException;
import java.time.Month;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * IHM "Adicionar planejamento"
 */
public class AddPlanningForm extends AnchorPane {

	@FXML
	private AutoCompleteComboBox<Month> monthCombo;
	@FXML
	private TextField yearText;

	@Inject
	private AddPlanningFormPresenter presenter;

	/**
	 * Construtor.
	 */
	public AddPlanningForm() {
		loadFxml();
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/AddPlanningForm.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@Inject
	private void init() {
		monthCombo.setOriginalItems(presenter.getMonths());
		monthCombo.valueProperty().bindBidirectional(presenter.monthProperty());
		yearText.textProperty().bindBidirectional(presenter.yearProperty());
	}

	public AddPlanningFormPresenter getPresenter() {
		return presenter;
	}

	@FXML
	public void save(ActionEvent evt) {
		presenter.save();
	}

	@FXML
	public void cancel(ActionEvent evt) {
		presenter.cancel();
	}

}
