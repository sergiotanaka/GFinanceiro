package org.pinguin.gf.gui.main;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

public class MainPane extends BorderPane {

	private EventHandler<ActionEvent> onJournalEntryHandler;
	private EventHandler<ActionEvent> onListJournalEntryHandler;
	private EventHandler<ActionEvent> onListBalanceHandler;
	private EventHandler<ActionEvent> onPlanningHandler;
	private EventHandler<ActionEvent> onMaintAccount;
	private EventHandler<ActionEvent> onJournalEntryListHandler;
	private EventHandler<ActionEvent> onClearEntriesHandler;
	private EventHandler<ActionEvent> onCashFlowHandler;
	private EventHandler<ActionEvent> onCalendarHandler;

	public MainPane() {
		loadFxml();
	}

	public EventHandler<ActionEvent> getOnJournalEntryHandler() {
		return onJournalEntryHandler;
	}

	public void setOnJournalEntryHandler(EventHandler<ActionEvent> handler) {
		this.onJournalEntryHandler = handler;
	}

	public EventHandler<ActionEvent> getOnListJournalEntryHandler() {
		return onListJournalEntryHandler;
	}

	public void setOnListJournalEntryHandler(EventHandler<ActionEvent> onListJournalEntryHandler) {
		this.onListJournalEntryHandler = onListJournalEntryHandler;
	}

	public EventHandler<ActionEvent> getOnListBalance() {
		return onListBalanceHandler;
	}

	public void setOnListBalance(EventHandler<ActionEvent> onListBalance) {
		this.onListBalanceHandler = onListBalance;
	}

	public EventHandler<ActionEvent> getOnPlanningHandler() {
		return onPlanningHandler;
	}

	public void setOnPlanningHandler(EventHandler<ActionEvent> onPlanningHandler) {
		this.onPlanningHandler = onPlanningHandler;
	}

	public EventHandler<ActionEvent> getOnMaintAccount() {
		return onMaintAccount;
	}

	public void setOnMaintAccount(EventHandler<ActionEvent> onMaintAccount) {
		this.onMaintAccount = onMaintAccount;
	}

	public EventHandler<ActionEvent> getOnJournalEntryListHandler() {
		return onJournalEntryListHandler;
	}

	public void setOnJournalEntryListHandler(EventHandler<ActionEvent> onJournalEntryListHandler) {
		this.onJournalEntryListHandler = onJournalEntryListHandler;
	}

	public EventHandler<ActionEvent> getOnClearEntriesHandler() {
		return onClearEntriesHandler;
	}

	public void setOnClearEntriesHandler(EventHandler<ActionEvent> onClearEntriesHandler) {
		this.onClearEntriesHandler = onClearEntriesHandler;
	}

	public EventHandler<ActionEvent> getOnCashFlowHandler() {
		return onCashFlowHandler;
	}

	public void setOnCashFlowHandler(EventHandler<ActionEvent> onCashFlowHandler) {
		this.onCashFlowHandler = onCashFlowHandler;
	}

	public EventHandler<ActionEvent> getOnCalendarHandler() {
		return onCalendarHandler;
	}

	public void setOnCalendarHandler(EventHandler<ActionEvent> onCalendarHandler) {
		this.onCalendarHandler = onCalendarHandler;
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/MainPane.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@FXML
	public void onJournalEntry(ActionEvent event) {
		if (onJournalEntryHandler != null) {
			onJournalEntryHandler.handle(event);
		}
	}

	@FXML
	public void onListJournalEntry(ActionEvent event) {
		if (onListJournalEntryHandler != null) {
			onListJournalEntryHandler.handle(event);
		}
	}

	@FXML
	public void onListBalance(ActionEvent event) {
		if (onListBalanceHandler != null) {
			onListBalanceHandler.handle(event);
		}
	}

	@FXML
	public void onPlanning(ActionEvent event) {
		if (onPlanningHandler != null) {
			onPlanningHandler.handle(event);
		}
	}

	@FXML
	public void onMaintAccount(ActionEvent event) {
		if (onMaintAccount != null) {
			onMaintAccount.handle(event);
		}
	}

	@FXML
	public void onJournalEntryList(ActionEvent event) {
		if (onJournalEntryListHandler != null) {
			onJournalEntryListHandler.handle(event);
		}
	}

	@FXML
	public void onClearEntries(ActionEvent event) {
		if (onClearEntriesHandler != null) {
			onClearEntriesHandler.handle(event);
		}
	}

	@FXML
	public void onCashFlow(ActionEvent event) {
		if (onCashFlowHandler != null) {
			onCashFlowHandler.handle(event);
		}
	}

	@FXML
	public void onCalendar(ActionEvent event) {
		if (onCalendarHandler != null) {
			onCalendarHandler.handle(event);
		}
	}
}
