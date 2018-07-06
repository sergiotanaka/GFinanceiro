package org.pinguin.pomodoro.gui.report;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DailyReportRow {
	private final ObjectProperty<LocalDate> dateProp = new SimpleObjectProperty<>();
	private final StringProperty taskNameProp = new SimpleStringProperty();
	private final ObjectProperty<LocalDateTime> startProp = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalDateTime> endProp = new SimpleObjectProperty<>();
	private final ObjectProperty<Long> durationProp = new SimpleObjectProperty<>();

	public DailyReportRow(final LocalDate date, final String taskName, final LocalDateTime start,
			final LocalDateTime end, Long duration) {
		this.dateProp.set(date);
		this.taskNameProp.set(taskName);
		this.startProp.set(start);
		this.endProp.set(end);
		this.durationProp.set(duration);
	}

	public DailyReportRow() {
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return dateProp;
	}

	public StringProperty taskNameProperty() {
		return taskNameProp;
	}

	public ObjectProperty<LocalDateTime> startProperty() {
		return startProp;
	}

	public ObjectProperty<LocalDateTime> endProperty() {
		return endProp;
	}

	public ObjectProperty<Long> durationProperty() {
		return durationProp;
	}

}
