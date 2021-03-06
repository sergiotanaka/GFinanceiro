package org.pinguin.gf.gui.account;

import java.io.IOException;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountNatureTO;
import org.pinguin.gf.service.api.account.AccountTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class AccountForm extends AnchorPane {

	@FXML
	private TextField nameText;
	@FXML
	private AutoCompleteComboBox<AccountNatureTO> natureCombo;
	@FXML
	private AutoCompleteComboBox<AccountTO> parentCombo;
	@FXML
	private TextField tagText;
	@FXML
	private ListView<String> tagsLView;

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

		tagText.textProperty().bindBidirectional(presenter.tagProperty());
		tagsLView.setItems(presenter.getTags());
		presenter.selectedProperty().bind(tagsLView.getSelectionModel().selectedItemProperty());
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

	@FXML
	public void updTag(ActionEvent evt) {
		presenter.updTag();
	}

	@FXML
	public void newTag(ActionEvent evt) {
		presenter.newTag();
	}

	@FXML
	public void delTag(ActionEvent evt) {
		presenter.delTag();
	}
}
