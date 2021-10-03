package org.pinguin.gf.gui.journalentry;

import static org.pinguin.gf.gui.control.AutoCompleteCBCellFactory.forTableColumn;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTextField;

public class JournalEntryListForm extends AnchorPane {

	@FXML
	private TextArea textArea;
	@FXML
	private AutoCompleteComboBox<AccountTO> accountCombo;
	@FXML
	private CalendarTextField startDateText;
	@FXML
	private CalendarTextField endDateText;

	@FXML
	private TableView<JournalEntryItem> journalEntryTView;
	@FXML
	private TableColumn<JournalEntryItem, LocalDateTime> dateColumn;
	@FXML
	private TableColumn<JournalEntryItem, AccountTO> originColumn;
	@FXML
	private TableColumn<JournalEntryItem, AccountTO> accountColumn;
	@FXML
	private TableColumn<JournalEntryItem, String> descriptionColumn;

	@Inject
	private JournalEntryListPresenter presenter;

	public JournalEntryListForm() {
		loadFxml();
		final StringConverter<AccountTO> accStrConverter = new AccountStringConverter();
		accountCombo.setConverter(accStrConverter);

		startDateText.setDateFormat(new SimpleDateFormat("dd/MM/yy"));
		startDateText.setCalendar(Calendar.getInstance());

		endDateText.setDateFormat(new SimpleDateFormat("dd/MM/yy"));
		endDateText.setCalendar(Calendar.getInstance());

		journalEntryTView.setEditable(true);
	}

	@Inject
	public void init() {
		textArea.textProperty().bindBidirectional(presenter.textAreaProperty());

		journalEntryTView.setItems(presenter.getEntries());

		accountCombo.setOriginalItems(presenter.getAccounts());
		accountCombo.valueProperty().bindBidirectional(presenter.accountProperty());

		startDateText.calendarProperty().bindBidirectional(presenter.startDateProperty());
		endDateText.calendarProperty().bindBidirectional(presenter.endDateProperty());

		originColumn.setCellFactory(forTableColumn(AccountStringConverter.instance(), presenter.getAccounts()));
		accountColumn.setCellFactory(forTableColumn(AccountStringConverter.instance(), presenter.getAccounts()));
		descriptionColumn.setCellFactory(TextFieldTableCell.forTableColumn());
	}

	@FXML
	public void process(ActionEvent evt) {
		presenter.process();
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
	public void clean(ActionEvent evt) {
		presenter.clean();
	}

	@FXML
	public void reloadAccounts(ActionEvent evt) {
		presenter.reloadAccounts();
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(
				this.getClass().getResource("/META-INF/fxml/JournalEntryListForm.xml"));
		System.out.println("passou aqui");
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

}
