package org.pinguin.pomodoro.gui.mini;

import org.pinguin.pomodoro.gui.timer.Timer;
import org.pinguin.pomodoro.gui.timer.TimerBuilder;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class MiniPane extends BorderPane {

	private Timer timer;
	private Label remainingLabel = new Label();
	private Tooltip tooltip = new Tooltip();

	/**
	 * Construtor.
	 */
	public MiniPane() {
		init();
		this.setPadding(new Insets(10.0));
		this.setCenter(timer);
		timer.stop();

		final BorderPane bottom = new BorderPane();
		bottom.setPadding(new Insets(2.0));
		bottom.setCenter(remainingLabel);
		this.setBottom(bottom);

		Tooltip.install(this, tooltip);
	}

	public Timer getTimer() {
		return timer;
	}

	public Label getRemainingLabel() {
		return remainingLabel;
	}

	public Tooltip getTooltip() {
		return tooltip;
	}

	private void init() {
		timer = TimerBuilder.create().playButtonVisible(false).waitingColor(Color.GRAY).prefSize(50.0, 50.0).build();
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
