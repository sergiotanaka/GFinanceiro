package org.pinguin.pomodoro.domain.pomodoro;

import java.time.LocalDateTime;
import java.util.List;

public class Pomodoro {

	private Long id;
	private Long duration;
	private LocalDateTime start;
	private LocalDateTime end;
	private List<Long> taskIds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getDuration() {
		return duration;
	}

	public void setDuration(Long duration) {
		this.duration = duration;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getEnd() {
		return end;
	}

	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

	public List<Long> getTaskIds() {
		return taskIds;
	}

	public void setTaskIds(List<Long> taskIds) {
		this.taskIds = taskIds;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Pomodoro [id=");
		builder.append(id);
		builder.append(", duration=");
		builder.append(duration);
		builder.append(", start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append(", taskIds=");
		builder.append(taskIds);
		builder.append("]");
		return builder.toString();
	}

}
