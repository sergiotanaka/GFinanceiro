package org.pinguin.pomodoro.gui.mini;

import org.pinguin.pomodoro.gui.timer.Timer;
import org.pinguin.pomodoro.gui.timer.TimerBuilder;

import javafx.geometry.Insets;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class MiniPane extends BorderPane {

	private Timer timer;
	private Label remainingLabel = new Label();
	private Tooltip tooltip = new Tooltip();
	private Runnable closeWindow;

	/**
	 * Construtor.
	 */
	public MiniPane() {
		init();
		timer.stop();

		final BorderPane center = new BorderPane();
		center.setPadding(new Insets(0.0, 10.0, 0.0, 10.0));
		center.setCenter(timer);
		this.setCenter(center);

		final BorderPane top = new BorderPane();
		final Hyperlink closeLink = new Hyperlink("x");
		closeLink.setOnAction(e -> {
			if (closeWindow != null) {
				closeWindow.run();
			}
		});
		top.setRight(closeLink);
		this.setTop(top);

		final BorderPane bottom = new BorderPane();
		bottom.setPadding(new Insets(2.0));
		bottom.setCenter(remainingLabel);
		this.setBottom(bottom);

		Tooltip.install(this, tooltip);
	}

	public void setCloseWindow(Runnable closeWindow) {
		this.closeWindow = closeWindow;
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
