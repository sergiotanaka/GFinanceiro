package org.pinguin.gf.gui.account;

import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gui.util.EditMode;

import javafx.stage.Window;

public class OpenAccountCommandParam {
	private final AccountTO to;
	private final EditMode editMode;
	private final Window owner;

	public OpenAccountCommandParam(AccountTO to, EditMode editMode, Window owner) {
		super();
		this.to = to;
		this.editMode = editMode;
		this.owner = owner;
	}

	public AccountTO getTo() {
		return to;
	}

	public EditMode getEditMode() {
		return editMode;
	}

	public Window getOwner() {
		return owner;
	}

}
