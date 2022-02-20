package org.pinguin.gf.gui.accstatement;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccStatementEntryTO;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gui.util.Dialog;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTextField;

/**
 * Form do "Historico".
 */
public class AccStatementReport extends AnchorPane {

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@FXML
	private AutoCompleteComboBox<AccountTO> accountCombo;
	@FXML
	private CalendarTextField startDateText;
	@FXML
	private CalendarTextField endDateText;
	@FXML
	private TextField tagFilterText;
	@FXML
	private TableView<AccStatementEntryTO> accStatementTView;
	@FXML
	private CheckBox periodBalanceChkBox;

	@FXML
	private TableColumn<AccStatementEntryTO, String> dateColumn;
	@FXML
	private TableColumn<AccStatementEntryTO, String> originColumn;
	@FXML
	private TableColumn<AccStatementEntryTO, String> accountColumn;

	@Inject
	private AccStatementPresenter presenter;

	public AccStatementReport() {
		loadFxml();
		final StringConverter<AccountTO> accStrConverter = new AccountStringConverter();
		accountCombo.setConverter(accStrConverter);
		dateColumn.setCellValueFactory(new SimpleCellValueFactory<AccStatementEntryTO, LocalDateTime>("date",
				new LocalDateTimeStrConverter()));
		accountColumn.setCellValueFactory(
				new SimpleCellValueFactory<AccStatementEntryTO, AccountTO>("account", accStrConverter));
		originColumn.setCellValueFactory(
				new SimpleCellValueFactory<AccStatementEntryTO, AccountTO>("origin", accStrConverter));

		accStatementTView.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent evt) {
				if (evt.getCode().equals(KeyCode.DELETE)) {
					int result = Dialog.showQuestionDialog(null, "Confirma a exclusao do item?");
					if (result >= 0) {
						presenter.deleteJournalEntry(accStatementTView.getSelectionModel().getSelectedItem());
					}
				}
			}
		});
		accStatementTView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					presenter.editJournalEntry(accStatementTView.getSelectionModel().getSelectedItem());
				}
			}
		});

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem copy = new MenuItem("Copiar para área de transferência");
		copy.setOnAction(e -> {
			StringBuilder sb = new StringBuilder();
			appendItems(accStatementTView, "", sb);
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(sb.toString());
			clipboard.setContent(content);
		});
		contextMenu.getItems().addAll(copy);
		accStatementTView.setContextMenu(contextMenu);
	}

	private void appendItems(final TableView<AccStatementEntryTO> tableView, final String prefix,
			final StringBuilder sb) {
		for (AccStatementEntryTO item : tableView.getItems()) {
			sb.append(format(item.getDate())).append("\t");
			sb.append(item.getOrigin().getName()).append("\t");
			sb.append(item.getAccount().getName()).append("\t");
			sb.append(item.getDescription()).append("\t");
			sb.append(item.getTags()).append("\t");
			sb.append(format(item.getValue())).append("\t");
			sb.append(format(item.getBalance())).append("\n");
		}
	}

	private String format(final BigDecimal number) {
		Format format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		return format.format(number);
	}

	private String format(final LocalDateTime dateTime) {
		return formatter.format(dateTime);
	}

	public static class CalendarStrConverter extends StringConverter<Calendar> {

		private final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		@Override
		public Calendar fromString(String string) {
			return null;
		}

		@Override
		public String toString(Calendar cal) {
			if (cal == null) {
				return "";
			}
			return formatter.format(cal.getTime());
		}
	}

	public static class LocalDateTimeStrConverter extends StringConverter<LocalDateTime> {

		private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

		@Override
		public LocalDateTime fromString(String string) {
			return null;
		}

		@Override
		public String toString(LocalDateTime cal) {
			if (cal == null) {
				return "";
			}

			return cal.format(formatter);
		}
	}

	public static class SimpleCellValueFactory<T, O>
			implements Callback<CellDataFeatures<T, String>, ObservableValue<String>> {

		private final String fieldName;
		private final StringConverter<O> strConverter;

		public SimpleCellValueFactory(String fieldName, StringConverter<O> strConverter) {
			this.fieldName = fieldName;
			this.strConverter = strConverter;
		}

		@Override
		public ObservableValue<String> call(CellDataFeatures<T, String> cellDataFeatures) {

			T bean = cellDataFeatures.getValue();

			if (bean == null) {
				return null;
			}

			try {
				Field field = bean.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				@SuppressWarnings("unchecked")
				O fieldValue = (O) field.get(bean);
				return new ReadOnlyObjectWrapper<String>(strConverter.toString(fieldValue));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				return new ReadOnlyObjectWrapper<String>("Error");
			}
		}
	}

	@Inject
	public void init() {
		accountCombo.setOriginalItems(presenter.getAccounts());
		accountCombo.valueProperty().bindBidirectional(presenter.accountProperty());
		startDateText.calendarProperty().bindBidirectional(presenter.startDateProperty());
		endDateText.calendarProperty().bindBidirectional(presenter.endDateProperty());
		tagFilterText.textProperty().bindBidirectional(presenter.tagFilterProperty());
		periodBalanceChkBox.selectedProperty().bindBidirectional(presenter.periodBalanceProperty());
		accStatementTView.setItems(presenter.accStatementEntries());
	}

	public AccStatementPresenter getPresenter() {
		return presenter;
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/AccStatementReport.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@FXML
	public void retrieve(ActionEvent evt) {
		presenter.retrieve();
	}

}
