package org.pinguin.pomodoro.gui.main;

import org.pinguin.pomodoro.domain.task.Task;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

public class TaskRow {

	private final SimpleStringProperty nameProp = new SimpleStringProperty();
	private final SimpleBooleanProperty doneProp = new SimpleBooleanProperty();

	public TaskRow(final Task task) {

		nameProp.set(task.getName());
		doneProp.set(task.isDone());

		nameProp.addListener((obs, oldV, newV) -> task.setName(newV));
		doneProp.addListener((obs, oldV, newV) -> task.setDone(newV));
	}

	public SimpleStringProperty nameProperty() {
		return nameProp;
	}

	public SimpleBooleanProperty doneProperty() {
		return doneProp;
	}

	@Override
	public String toString() {
		return "TaskRow [nameProp=" + nameProp.get() + ", doneProp=" + doneProp.get() + "]";
	}

}
