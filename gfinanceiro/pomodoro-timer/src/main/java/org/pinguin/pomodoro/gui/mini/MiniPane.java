package org.pinguin.pomodoro.gui.mini;

import org.pinguin.pomodoro.gui.timer.Timer;
import org.pinguin.pomodoro.gui.timer.TimerBuilder;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class MiniPane extends BorderPane {

	private Timer timer;
	private Label remainingLabel = new Label();

	/**
	 * Construtor.
	 */
	public MiniPane() {
		init();
		this.setPadding(new Insets(10.0));
		this.setCenter(timer);
		timer.stop();

		final BorderPane bottom = new BorderPane();
		bottom.setPadding(new Insets(5.0));
		bottom.setCenter(remainingLabel);
		this.setBottom(bottom);
	}

	public Timer getTimer() {
		return timer;
	}

	public Label getRemainingLabel() {
		return remainingLabel;
	}

	private void init() {
		timer = TimerBuilder.create().playButtonVisible(false).waitingColor(Color.GRAY).prefSize(70.0, 70.0).build();
		timer.setOnTimerEvent(event -> {
			switch (event.getType()) {
			case STARTED:
				break;
			case CONTINUED:
				break;
			case STOPPED:
				timer.setPlayButtonVisible(true);
				break;
			case FINISHED:
				break;
			case WAITING:
				break;
			}
		});
	}
}
