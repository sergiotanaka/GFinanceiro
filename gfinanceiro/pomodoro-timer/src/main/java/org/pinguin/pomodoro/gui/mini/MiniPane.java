package org.pinguin.pomodoro.gui.mini;

import org.pinguin.pomodoro.gui.timer.Timer;
import org.pinguin.pomodoro.gui.timer.TimerBuilder;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MiniPane extends BorderPane {

	private Timer timer;

	/**
	 * Construtor.
	 */
	public MiniPane() {
		init();
		this.setPadding(new Insets(30.0));
		this.setCenter(timer);
		timer.waiting();
		final Label remaining = new Label();
		this.setBottom(remaining);
		timer.progressProperty().addListener((r, o, n) -> remaining.setText(Double.toString((double) n)));
	}

	public void init() {
		timer = TimerBuilder.create().playButtonVisible(false).waitingColor(Color.GRAY).prefSize(38, 38).build();
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
			System.out.println(event.getType());
		});
	}

	public ObjectProperty<Duration> durationProperty() {
		return timer.durationProperty();
	}

}
