package org.pinguin.pomodoro.domain.task;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Task {

	@Id
	@GeneratedValue
	private Long id;
	private Long index;
	private String name;
	private TaskState state = TaskState.STOPPED;
	private boolean done = false;

	public Task(String name, boolean done) {
		this.name = name;
		this.done = done;
	}

	public Task(String name) {
		this(name, false);
	}

	public Task() {
		this(null, false);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getIndex() {
		return index;
	}

	public void setIndex(Long index) {
		this.index = index;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskState getState() {
		return state;
	}

	public void setState(TaskState state) {
		this.state = state;
	}

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", index=" + index + ", name=" + name + ", state=" + state + ", done=" + done + "]";
	}
}
