package org.pinguin.gf.gui.planning;

import java.io.IOException;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

/**
 * IHM "Planejamento da conta"
 */
public class AccPlanningForm extends AnchorPane {

	@Inject
	private AccPlanningFormPresenter presenter;

	@FXML
	private AutoCompleteComboBox<AccountTO> accountCombo;
	@FXML
	private TextField valueText;
	@FXML
	private TextArea commentText;

	public AccPlanningForm() {
		loadFxml();
		accountCombo.setConverter(new AccountStringConverter());
	}

	@Inject
	public void init() {
		accountCombo.setOriginalItems(presenter.getAccounts());
		accountCombo.valueProperty().bindBidirectional(presenter.accountProperty());
		valueText.textProperty().bindBidirectional(presenter.valueProperty());
		commentText.textProperty().bindBidirectional(presenter.commentProperty());
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
