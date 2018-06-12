package org.pinguin.pomodoro.domain.taskstatetransition;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.pinguin.pomodoro.domain.task.Task;
import org.pinguin.pomodoro.domain.task.TaskState;

@Entity
public class TaskStateTransition {
	@Id
	@GeneratedValue
	private Long id;
	@OneToOne(fetch = FetchType.EAGER)
	private Task task;
	@Enumerated(EnumType.STRING)
	private TaskState before;
	@Enumerated(EnumType.STRING)
	private TaskState after;

	public TaskStateTransition() {
	}

	public TaskStateTransition(Task task, TaskState before, TaskState after) {
		this.task = task;
		this.before = before;
		this.after = after;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	public TaskState getBefore() {
		return before;
	}

	public void setBefore(TaskState before) {
		this.before = before;
	}

	public TaskState getAfter() {
		return after;
	}

	public void setAfter(TaskState after) {
		this.after = after;
	}

	@Override
	public String toString() {
		return "TaskStateTransition [id=" + id + ", task=" + task + ", before=" + before + ", after=" + after + "]";
	}

}
