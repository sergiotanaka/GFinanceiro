package org.pinguin.pomodoro.gui.report;

import java.time.LocalDate;
import java.time.LocalTime;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DailyReportRow {
	private final ObjectProperty<LocalDate> dateProp = new SimpleObjectProperty<>();
	private final StringProperty taskNameProp = new SimpleStringProperty();
	private final ObjectProperty<LocalTime> startProp = new SimpleObjectProperty<>();
	private final ObjectProperty<LocalTime> endProp = new SimpleObjectProperty<>();

	public DailyReportRow(final LocalDate date, final String taskName, final LocalTime start,
			final LocalTime end) {
		this.dateProp.set(date);
		this.taskNameProp.set(taskName);
		this.startProp.set(start);
		this.endProp.set(end);
	}

	public DailyReportRow() {
	}

	public ObjectProperty<LocalDate> dateProperty() {
		return dateProp;
	}

	public StringProperty taskNameProperty() {
		return taskNameProp;
	}

	public ObjectProperty<LocalTime> startProperty() {
		return startProp;
	}

	public ObjectProperty<LocalTime> endProperty() {
		return endProp;
	}

}
