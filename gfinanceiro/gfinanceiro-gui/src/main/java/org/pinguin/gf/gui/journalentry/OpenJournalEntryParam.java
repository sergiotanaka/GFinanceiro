package org.pinguin.gf.gui.journalentry;

import org.pinguin.gf.facade.journalentry.JournalEntryTO;
import org.pinguin.gui.util.EditMode;

import javafx.stage.Window;

public class OpenJournalEntryParam {

	private final JournalEntryTO to;
	private final EditMode editMode;
	private final Window owner;

	public OpenJournalEntryParam(JournalEntryTO to, Window owner, EditMode editMode) {
		this.to = to;
		this.owner = owner;
		this.editMode = editMode;
	}

	public JournalEntryTO getTo() {
		return to;
	}

	public Window getOwner() {
		return owner;
	}

	public EditMode getEditMode() {
		return editMode;
	}
}
