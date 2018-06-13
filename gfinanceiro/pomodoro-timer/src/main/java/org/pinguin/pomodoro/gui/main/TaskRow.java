package org.pinguin.pomodoro.gui.main;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskState;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskRow {

	private final SimpleStringProperty nameProp = new SimpleStringProperty();
	private final SimpleObjectProperty<TaskState> stateProp = new SimpleObjectProperty<>();
	private final Task task;

	public TaskRow(final Task task) {

		nameProp.set(task.getName());
		stateProp.set(task.getState());

		nameProp.addListener((obs, oldV, newV) -> task.setName(newV));
		stateProp.addListener((obs, oldV, newV) -> task.setState(newV));

		this.task = task;
	}

	public StringProperty nameProperty() {
		return nameProp;
	}

	public ObjectProperty<TaskState> stateProperty() {
		return stateProp;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public String toString() {
		return "TaskRow [nameProp=" + nameProp + ", stateProp=" + stateProp + ", task=" + task + "]";
	}

}
