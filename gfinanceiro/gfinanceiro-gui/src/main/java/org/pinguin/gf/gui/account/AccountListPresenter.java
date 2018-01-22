package org.pinguin.gf.gui.account;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountService;
import org.pinguin.gf.facade.account.AccountTO;
import org.pinguin.gf.facade.common.ParameterTO;
import org.pinguin.gui.util.EditMode;

import com.google.inject.Injector;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AccountListPresenter {

	@Inject
	private Injector injector;
	@Inject
	private AccountService service;

	private final Property<String> nameProperty = new SimpleStringProperty();
	private final ObservableList<AccountTO> accounts = FXCollections.observableArrayList();

	public Property<String> nameProperty() {
		return nameProperty;
	}

	public ObservableList<AccountTO> getAccounts() {
		return accounts;
	}

	public void retrive() {
		List<AccountTO> retrieved = new ArrayList<>();
		if (nameProperty.getValue() == null || nameProperty.getValue().isEmpty()) {
			retrieved.addAll(service.retrieveAccounts());
		} else {
			retrieved.addAll(
					service.retrieveAccountByParams("select a from Account a where upper(name) like upper(:name)",
							new ParameterTO<String>("name", "%" + nameProperty.getValue() + "%")));
		}
		this.accounts.clear();
		this.accounts.addAll(retrieved);
	}

	public void editAccount(AccountTO selectedItem) {
		AccountTO account = service.retrieveAccountById(selectedItem.getId());
		injector.getInstance(OpenAccountCommand.class)
				.apply(new OpenAccountCommandParam(account, EditMode.UPDATE, null));
	}

	public void add() {
		injector.getInstance(OpenAccountCommand.class)
				.apply(new OpenAccountCommandParam(new AccountTO(), EditMode.CREATE, null));
	}

	public void deleteAccount(AccountTO selectedItem) {
		service.deleteAccount(selectedItem);
	}

}
