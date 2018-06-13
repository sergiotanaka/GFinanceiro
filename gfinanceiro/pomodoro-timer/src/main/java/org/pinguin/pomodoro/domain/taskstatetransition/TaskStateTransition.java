package org.pinguin.pomodoro.domain.taskstatetransition;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.pinguin.pomodoro.domain.task.TaskState;

@Entity
public class TaskStateTransition {
	@Id
	@GeneratedValue
	private Long id;
	private Long taskId;
	@Enumerated(EnumType.STRING)
	private TaskState before;
	@Enumerated(EnumType.STRING)
	private TaskState after;

	public TaskStateTransition() {
	}

	public TaskStateTransition(Long taskId, TaskState before, TaskState after) {
		this.taskId = taskId;
		this.before = before;
		this.after = after;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTask() {
		return taskId;
	}

	public void setTask(Long taskId) {
		this.taskId = taskId;
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
		return "TaskStateTransition [id=" + id + ", taskId=" + taskId + ", before=" + before + ", after=" + after + "]";
	}

}
