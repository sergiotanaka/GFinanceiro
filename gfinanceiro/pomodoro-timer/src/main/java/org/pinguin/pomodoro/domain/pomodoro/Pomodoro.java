package org.pinguin.pomodoro.domain.pomodoro;

import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.FINISH;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.PAUSE;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroEvent.START;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.EXECUTING;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.PAUSED;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.RESTING;
import static org.pinguin.pomodoro.domain.pomodoro.PomodoroState.STOPPED;

import java.util.Timer;
import java.util.TimerTask;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Pomodoro {

	// private Long id;
	private Long execDuration = 25L * 60L * 1000L;
	private Long restDuration = 5L * 60L * 1000L;
	private Long remaining = execDuration;
	private final ObjectProperty<PomodoroState> stateProp = new SimpleObjectProperty<>(STOPPED);

	private Timer timer;

	private long lastUpdate;

	private Runnable onTimeout;

	public void setOnTimeout(final Runnable onTimeout) {
		this.onTimeout = onTimeout;
	}

	public synchronized void onEvent(final PomodoroEvent evt) {
		final PomodoroState oldState = stateProp.get();
		final PomodoroState newState = stateProp.get().after(evt);
		if (newState == null) {
			throw new IllegalArgumentException(
					"Evento nao esparado para o estado atual: " + evt + " " + this.stateProp.get());
		}
		stateProp.set(newState);

		// Acoes das mudancas de estado
		if (evt.equals(START) && (oldState.equals(STOPPED) || oldState.equals(RESTING))) {
			// reiniciando o remaining
			remaining = execDuration;
			lastUpdate = System.currentTimeMillis();
			onTimeout(FINISH, execDuration);
		} else if (evt.equals(START) && oldState.equals(PAUSED)) {
			lastUpdate = System.currentTimeMillis();
			onTimeout(FINISH, remaining);
		} else if (evt.equals(PAUSE) && oldState.equals(EXECUTING)) {
			remaining -= System.currentTimeMillis() - lastUpdate;
			timer.cancel();
			timer = null;
		} else if (evt.equals(FINISH) && (oldState.equals(EXECUTING) || oldState.equals(PAUSED))) {
			remaining = restDuration;
			lastUpdate = System.currentTimeMillis();
			onTimeout(FINISH, restDuration);
		} else if (evt.equals(FINISH) && oldState.equals(RESTING)) {
			remaining = execDuration;
			lastUpdate = System.currentTimeMillis();
		} else {
			System.out.println("Evento sem acao: " + evt);
		}
	}

	private void onTimeout(final PomodoroEvent evt, long timeout) {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer(true);

		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Pomodoro.this.onEvent(evt);
				if (Pomodoro.this.onTimeout != null) {
					Pomodoro.this.onTimeout.run();
				}
			}
		};
		timer.schedule(task, timeout);
	}

	public Long getRemaining() {
		return remaining;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public ObjectProperty<PomodoroState> stateProperty() {
		return stateProp;
	}
}
