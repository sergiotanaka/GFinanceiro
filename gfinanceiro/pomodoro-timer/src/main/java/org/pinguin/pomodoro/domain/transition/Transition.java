package org.pinguin.pomodoro.domain.transition;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.pinguin.pomodoro.domain.pomodoro.PomodoroState;

@Entity
public class Transition {
	@Id
	@GeneratedValue
	private Long id;
	@Enumerated(EnumType.STRING)
	private PomodoroState before;
	@Enumerated(EnumType.STRING)
	private PomodoroState after;

	private LocalDateTime timestamp;

	public Transition(PomodoroState before, PomodoroState after) {
		this.before = before;
		this.after = after;
		this.timestamp = LocalDateTime.now();
	}

	public Transition() {
	}

	public PomodoroState getBefore() {
		return before;
	}

	public void setBefore(PomodoroState before) {
		this.before = before;
	}

	public PomodoroState getAfter() {
		return after;
	}

	public void setAfter(PomodoroState after) {
		this.after = after;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "Transition [before=" + before + ", after=" + after + ", timestamp=" + timestamp + "]";
	}

}
