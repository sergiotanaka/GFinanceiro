package org.pinguin.pomodoro.domain.pomodoro;

import java.util.HashMap;
import java.util.Map;

/**
 * Estados do pomodoro.
 */
public enum PomodoroState {

	/** */
	STOPPED, EXECUTING, PAUSED, RESTING;

	private final Map<PomodoroEvent, PomodoroState> transitions = new HashMap<>();
	static {
		STOPPED.transitions.put(PomodoroEvent.START, EXECUTING);

		EXECUTING.transitions.put(PomodoroEvent.PAUSE, PAUSED);
		EXECUTING.transitions.put(PomodoroEvent.FINISH, RESTING);

		PAUSED.transitions.put(PomodoroEvent.START, EXECUTING);
		PAUSED.transitions.put(PomodoroEvent.FINISH, RESTING);

		RESTING.transitions.put(PomodoroEvent.START, EXECUTING);
		RESTING.transitions.put(PomodoroEvent.FINISH, STOPPED);
	}

	public PomodoroState after(PomodoroEvent evt) {
		return transitions.get(evt);
	}
}
