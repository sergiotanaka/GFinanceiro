package org.pinguin.gf.gui.account;

import java.io.IOException;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.gui.accstatement.AccStatementReport.SimpleCellValueFactory;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gui.util.Dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class AccountList extends AnchorPane {
	
	@FXML
	private TextField nameText;
	@FXML
	private TableView<AccountTO> accountTView;
	@FXML
	private TableColumn<AccountTO, String> parentColumn;

	@Inject
	private AccountListPresenter presenter;

	public AccountList() {
		loadFxml();
		
		parentColumn.setCellValueFactory(
				new SimpleCellValueFactory<AccountTO, AccountTO>("parent", new AccountStringConverter()));

		accountTView.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent evt) {
				if (evt.getCode().equals(KeyCode.DELETE)) {
					int result = Dialog.showQuestionDialog(null, "Confirma a exclusão do item?");
					if (result >= 0) {
						presenter.deleteAccount(accountTView.getSelectionModel().getSelectedItem());
					}
				}
			}
		});
		accountTView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					presenter.editAccount(accountTView.getSelectionModel().getSelectedItem());
				}
			}
		});

	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/AccountList.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@Inject
	public void init() {
		nameText.textProperty().bindBidirectional(presenter.nameProperty());
		accountTView.setItems(presenter.getAccounts());
	}

	@FXML
	public void retrieve(ActionEvent evt) {
		presenter.retrive();
	}

	@FXML
	public void add(ActionEvent evt) {
		presenter.add();
	}

}
