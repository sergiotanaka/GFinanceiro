package org.pinguin.pomodoro.domain.taskstatetransition;

import java.time.LocalDateTime;

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
	private LocalDateTime timeStamp;
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
		this.timeStamp = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTaskId() {
		return taskId;
	}

	public void setTaskId(Long taskId) {
		this.taskId = taskId;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
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
		return "TaskStateTransition [id=" + id + ", taskId=" + taskId + ", timeStamp=" + timeStamp + ", before="
				+ before + ", after=" + after + "]";
	}
}
