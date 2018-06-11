package org.pinguin.pomodoro.domain.pomodoro;

import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.FINISH;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.PAUSE;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.START;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.EXECUTING;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.PAUSED;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.RESTING;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.STOPPED;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class Pomodoro {

	private Long id;
	private Long execDuration = 25L * 60L * 1000L;
	private Long restDuration = 5L * 60L * 1000L;
	private Long remaining = 0L;
	private PomodoroState state = STOPPED;

	private long lastUpdate;

	private final List<BiConsumer<PomodoroState, PomodoroState>> changeListeners = new ArrayList<>();

	public void onEvent(final PomodoroEvent evt) {
		final PomodoroState oldState = this.state;
		final PomodoroState newState = this.state.after(evt);
		if (newState == null) {
			throw new IllegalArgumentException("Evento nao esparado para o estado atual: " + evt + " " + this.state);
		}
		this.state = newState;

		notifiyTransition(oldState, newState);

		// Acoes das mudancas de estado
		if (evt.equals(START) && (oldState.equals(STOPPED) || oldState.equals(RESTING))) {
			// reiniciando o remaining
			remaining = execDuration;
			lastUpdate = System.currentTimeMillis();
		} else if (evt.equals(PAUSE) && oldState.equals(EXECUTING)) {
			remaining -= System.currentTimeMillis() - lastUpdate;
		} else if (evt.equals(START) && oldState.equals(PAUSED)) {
			lastUpdate = System.currentTimeMillis();
		} else if (evt.equals(FINISH)) {
			remaining = restDuration;
			lastUpdate = System.currentTimeMillis();
		} else {
			System.out.println("Evento sem acao: " + evt);
		}
	}

	private void notifiyTransition(final PomodoroState oldState, final PomodoroState newState) {
		for (BiConsumer<PomodoroState, PomodoroState> listener : changeListeners) {
			listener.accept(oldState, newState);
		}
	}

	public void updateRemaining() {
		remaining -= System.currentTimeMillis() - lastUpdate;
		lastUpdate = System.currentTimeMillis();

		if (remaining <= 0) {
			remaining = 0L;
			onEvent(FINISH);
		}
	}

	public Long getRemaining() {
		return remaining;
	}

	public PomodoroState getState() {
		return state;
	}
	
	public void addListener(final BiConsumer<PomodoroState, PomodoroState> listener) {
		this.changeListeners.add(listener);
	}

}
