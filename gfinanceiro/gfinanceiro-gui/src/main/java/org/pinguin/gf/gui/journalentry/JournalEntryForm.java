package org.pinguin.gf.gui.journalentry;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;

import javax.inject.Inject;

import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.control.TagBar;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.TagTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTextField;

/**
 * Formulario para preenchimento de um Lancamento.
 */
public class JournalEntryForm extends AnchorPane {

	@FXML
	private AutoCompleteComboBox<AccountTO> debitCombo;
	@FXML
	private AutoCompleteComboBox<AccountTO> creditCombo;
	@FXML
	private TextField valueText;
	@FXML
	private CalendarTextField dateText;
	@FXML
	private TextField descriptionText;
	@FXML
	private TextField attachmentText;
	@FXML
	private TagBar<TagTO> tagBar;
	@FXML
	private CheckBox futureCheck;

	@Inject
	private JournalEntryPresenter presenter;

	public JournalEntryForm() {
		loadFxml();
		final StringConverter<AccountTO> stringConverter = new AccountStringConverter();
		debitCombo.setConverter(stringConverter);
		creditCombo.setConverter(stringConverter);
		dateText.setDateFormat(new SimpleDateFormat("dd/MM/yy HH:mm:ss"));
		dateText.setCalendar(Calendar.getInstance());
		dateText.setShowTime(true);
		StringConverter<BigDecimal> bigDecimalConverter = new StringConverter<>() {

			@Override
			public String toString(BigDecimal object) {
				if (object == null) {
					return "";
				}
				return object.toString();
			}

			@Override
			public BigDecimal fromString(String string) {
				if (string == null || string.isEmpty()) {
					return BigDecimal.ZERO.setScale(2);
				}
				return BigDecimal.valueOf(Double.parseDouble(string)).setScale(2);
			}
		};
		valueText.setTextFormatter(new TextFormatter<>(bigDecimalConverter));
		tagBar.setConverter(new StringConverter<TagTO>() {

			@Override
			public String toString(final TagTO object) {
				return object.getName();
			}

			@Override
			public TagTO fromString(final String string) {
				final Optional<TagTO> found = tagBar.getCandidates().stream().filter(t -> t.getName().equals(string))
						.findFirst();
				if (!found.isEmpty()) {
					return found.get();
				}
				return new TagTO(string);
			}
		});
	}

	@Inject
	public void init() {
		debitCombo.setOriginalItems(presenter.getDebitAccounts());
		creditCombo.setOriginalItems(presenter.getCreditAccounts());
		debitCombo.valueProperty().bindBidirectional(presenter.debitProperty());
		creditCombo.valueProperty().bindBidirectional(presenter.creditProperty());
		valueText.textProperty().bindBidirectional(presenter.valueProperty());
		dateText.calendarProperty().bindBidirectional(presenter.dateProperty());
		descriptionText.textProperty().bindBidirectional(presenter.descriptionProperty());
		attachmentText.textProperty().bindBidirectional(presenter.attachmentProperty());
		tagBar.setTags(presenter.getTags());
		tagBar.setCandidates(presenter.getCandidateTags());
		futureCheck.selectedProperty().bindBidirectional(presenter.futureProperty());

	}

	public JournalEntryPresenter getPresenter() {
		return presenter;
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/JournalEntryForm.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
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
	public void attach(ActionEvent evt) {
		final FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select attachment");
		final File selected = fileChooser.showOpenDialog(this.getScene().getWindow());
		presenter.attachmentProperty().setValue(selected.getAbsolutePath());
	}

}
