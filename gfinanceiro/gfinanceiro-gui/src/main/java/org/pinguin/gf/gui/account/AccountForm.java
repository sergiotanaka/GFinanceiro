package org.pinguin.gf.gui.account;

import java.io.IOException;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountNatureTO;
import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AccountForm extends AnchorPane {

	@FXML
	private TextField nameText;
	@FXML
	private AutoCompleteComboBox<AccountNatureTO> natureCombo;
	@FXML
	private AutoCompleteComboBox<AccountTO> parentCombo;

	@Inject
	private AccountFormPresenter presenter;

	public AccountForm() {
		loadFxml();
		parentCombo.setConverter(new AccountStringConverter());
	}

	@Inject
	public void init() {
		nameText.textProperty().bindBidirectional(presenter.nameProperty());

		natureCombo.setOriginalItems(presenter.getNatures());
		parentCombo.setOriginalItems(presenter.getParentAccounts());
		natureCombo.valueProperty().bindBidirectional(presenter.natureProperty());
		parentCombo.valueProperty().bindBidirectional(presenter.parentProperty());

	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/AccountForm.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	public AccountFormPresenter getPresenter() {
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
