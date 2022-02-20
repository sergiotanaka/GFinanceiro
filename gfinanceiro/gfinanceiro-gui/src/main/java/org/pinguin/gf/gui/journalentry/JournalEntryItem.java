package org.pinguin.gf.gui.journalentry;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.TagTO;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class JournalEntryItem {

	private final LongProperty entryIdProperty = new SimpleLongProperty();
	private final ObjectProperty<AccountTO> debitAccountProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<AccountTO> creditAccountProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<BigDecimal> valueProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDateTime> dateProperty = new SimpleObjectProperty<>();
	private final StringProperty descriptionProperty = new SimpleStringProperty();
	private final ObjectProperty<ObservableList<TagTO>> tagsProperty = new SimpleObjectProperty<>(
			FXCollections.observableArrayList());

	public LongProperty entryIdProperty() {
		return entryIdProperty;
	}

	public ObjectProperty<AccountTO> debitAccountProperty() {
		return debitAccountProperty;
	}

	public ObjectProperty<AccountTO> creditAccountProperty() {
		return creditAccountProperty;
	}

	public ObjectProperty<BigDecimal> valueProperty() {
		return valueProperty;
	}

	public ObjectProperty<LocalDateTime> dateProperty() {
		return dateProperty;
	}

	public StringProperty descriptionProperty() {
		return descriptionProperty;
	}

	public ObjectProperty<ObservableList<TagTO>> tagsProperty() {
		return tagsProperty;
	}

}
