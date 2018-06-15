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
	private Long parentId;

	public Task(String name, TaskState state) {
		this.name = name;
		this.state = state;
	}

	public Task(String name) {
		this(name, TaskState.STOPPED);
	}

	public Task() {
		this(null);
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

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", index=" + index + ", name=" + name + ", state=" + state + "]";
	}

}
