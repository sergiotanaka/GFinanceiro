package org.pinguin.pomodoro.gui.report;

import java.time.LocalDateTime;

import org.pinguin.pomodoro.domain.task.TaskState;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ReportRow {

	private final ObjectProperty<LocalDateTime> timestampProp = new SimpleObjectProperty<>();
	private final StringProperty nameProp = new SimpleStringProperty();
	private final ObjectProperty<TaskState> beforeProp = new SimpleObjectProperty<>();
	private final ObjectProperty<TaskState> afterProp = new SimpleObjectProperty<>();

	public ReportRow() {
	}

	public ReportRow(final LocalDateTime timestamp, final String name, final TaskState before, final TaskState after) {
		timestampProp.set(timestamp);
		nameProp.set(name);
		beforeProp.set(before);
		afterProp.set(after);
	}

	public ObjectProperty<LocalDateTime> timestampProperty() {
		return timestampProp;
	}

	public StringProperty nameProperty() {
		return nameProp;
	}

	public ObjectProperty<TaskState> beforeProperty() {
		return beforeProp;
	}

	public ObjectProperty<TaskState> afterProperty() {
		return afterProp;
	}
}
