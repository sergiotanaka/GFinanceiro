package org.pinguin.pomodoro.gui.main;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskState;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TaskRow {

	private final SimpleStringProperty nameProp = new SimpleStringProperty();
	private final SimpleObjectProperty<TaskState> stateProp = new SimpleObjectProperty<>();
	private final SimpleIntegerProperty estimatedProp = new SimpleIntegerProperty();
	private final SimpleIntegerProperty spentProp = new SimpleIntegerProperty();
	
	
	private final Task task;

	public TaskRow(final Task task) {
		
		if (task.getEstimated() == null) {
			task.setEstimated(0);
		}
		nameProp.set(task.getName());
		stateProp.set(task.getState());
		estimatedProp.set(task.getEstimated());
		spentProp.set(0);

		nameProp.addListener((obs, oldV, newV) -> task.setName(newV));
		stateProp.addListener((obs, oldV, newV) -> task.setState(newV));
		estimatedProp.addListener((obs, oldV, newV) -> task.setEstimated((Integer) newV));

		this.task = task;
	}

	public StringProperty nameProperty() {
		return nameProp;
	}

	public ObjectProperty<TaskState> stateProperty() {
		return stateProp;
	}
	
	public IntegerProperty estimatedProperty() {
		return estimatedProp;
	}
	
	public IntegerProperty spentProperty() {
		return spentProp;
	}

	public Task getTask() {
		return task;
	}

	@Override
	public String toString() {
		return "TaskRow [nameProp=" + nameProp + ", stateProp=" + stateProp + ", task=" + task + "]";
	}

}
