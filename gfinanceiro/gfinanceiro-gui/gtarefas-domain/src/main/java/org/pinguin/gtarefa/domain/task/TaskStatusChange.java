package org.pinguin.gtarefa.domain.task;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

@Entity
public class TaskStatusChange {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotNull
	private Status newStatus;
	@NotNull
	private LocalDateTime dateTime;
	@NotNull
	@OneToOne(fetch = FetchType.EAGER)
	private Task task;

	public TaskStatusChange() {
	}

	public TaskStatusChange(Status newStatus, Task task) {
		this.newStatus = newStatus;
		this.task = task;
		this.dateTime = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Status getNewStatus() {
		return newStatus;
	}

	public void setNewStatus(Status newStatus) {
		this.newStatus = newStatus;
	}

	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Override
	public String toString() {
		return "TaskStatusChange [id=" + id + ", newStatus=" + newStatus + ", dateTime=" + dateTime + "]";
	}

}
