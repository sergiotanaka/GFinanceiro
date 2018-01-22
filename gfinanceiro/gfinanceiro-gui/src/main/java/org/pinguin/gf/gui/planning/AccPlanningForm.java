package org.pinguin.gf.gui.planning;

import java.io.IOException;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AccPlanningForm extends AnchorPane {

	@Inject
	private AccPlanningFormPresenter presenter;

	@FXML
	private AutoCompleteComboBox<AccountTO> accountCombo;
	@FXML
	private TextField valueText;

	public AccPlanningForm() {
		loadFxml();
		accountCombo.setConverter(new AccountStringConverter());
	}

	@Inject
	public void init() {
		accountCombo.setOriginalItems(presenter.getAccounts());
		accountCombo.valueProperty().bindBidirectional(presenter.accountProperty());
		valueText.textProperty().bindBidirectional(presenter.valueProperty());
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/AccPlanningForm.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	public AccPlanningFormPresenter getPresenter() {
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
