package org.pinguin.gf.gui.account;

import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountNatureTO;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gui.util.BindHelper;
import org.pinguin.gui.util.EditMode;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 */
public class AccountFormPresenter {

	@Inject
	private AccountService service;

	private BindHelper<AccountTO> bindHelper = new BindHelper<AccountTO>();

	private final Property<String> nameProperty = new SimpleObjectProperty<>("");
	private final Property<String> tagProperty = new SimpleObjectProperty<>("");
	private final ObservableList<AccountNatureTO> natures = FXCollections.observableArrayList();
	private final ObservableList<AccountTO> parentAccounts = FXCollections.observableArrayList();
	private final ObservableList<String> tags = FXCollections.observableArrayList();
	private Property<AccountNatureTO> nature = new SimpleObjectProperty<>();
	private Property<AccountTO> parent = new SimpleObjectProperty<>();
	private Property<String> selectedProperty = new SimpleObjectProperty<>("");

	private boolean selected = false;

	private EditMode editMode = EditMode.CREATE;
	private AccountTO to;

	private Function<Void, Void> closeWindowCommand;

	AccountFormPresenter() {
		selectedProperty.addListener((o, oldV, newV) -> {
			selected = (newV != null && !newV.isEmpty());
			if (selected) {
				tagProperty.setValue(newV);
			}
		});
	}

	public Property<String> nameProperty() {
		return nameProperty;
	}

	public ObservableList<AccountNatureTO> getNatures() {
		return natures;
	}

	public ObservableList<AccountTO> getParentAccounts() {
		return parentAccounts;
	}

	public Property<AccountNatureTO> natureProperty() {
		return nature;
	}

	public Property<AccountTO> parentProperty() {
		return parent;
	}

	public Property<String> tagProperty() {
		return tagProperty;
	}

	public Property<String> selectedProperty() {
		return selectedProperty;
	}

	public ObservableList<String> getTags() {
		return tags;
	}

	public AccountTO getTo() {
		return to;
	}

	public void setTo(AccountTO to) {
		this.to = to;

		if (to != null) {
			mapToPresenter(to);
		}
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public void setEditMode(EditMode editMode) {
		this.editMode = editMode;
	}

	public Function<Void, Void> getCloseWindowCommand() {
		return closeWindowCommand;
	}

	public void setCloseWindowCommand(Function<Void, Void> closeWindowCommand) {
		this.closeWindowCommand = closeWindowCommand;
	}

	private void mapToPresenter(AccountTO to) {
		bindHelper.setTo(to);
		bindHelper.bind("name", nameProperty);
		bindHelper.bind("nature", nature);
		bindHelper.bind("parent", parent);
		tags.addAll(to.getTags());
		to.setTags(tags);
	}

	public void save() {
		if (editMode.equals(EditMode.CREATE)) {
			service.createAccount(to);
		} else if (editMode.equals(EditMode.UPDATE)) {
			service.updateAccount(to.getAccountId(), to);
		}
		if (closeWindowCommand != null) {
			closeWindowCommand.apply(null);
		}
	}

	public void cancel() {
		if (closeWindowCommand != null) {
			closeWindowCommand.apply(null);
		}
	}

	public void newTag() {
		tagProperty.setValue("");
		selected = false;
	}

	public void delTag() {
		if (selected) {
			tags.remove(selectedProperty.getValue());
		}
	}

	public void updTag() {
		if (selected) {
			tags.remove(selectedProperty.getValue());
		}
		tags.add(tagProperty.getValue());
	}

}
