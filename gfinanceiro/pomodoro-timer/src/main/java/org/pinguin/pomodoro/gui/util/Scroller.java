package org.pinguin.pomodoro.gui.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeTableView;
import javafx.util.Duration;

public class Scroller {

	private final Timeline scrolltimeline = new Timeline();
	private double scrollDirection = 0;

	public void setup(final TreeTableView<?> treeTableView) {
		scrolltimeline.setCycleCount(Timeline.INDEFINITE);
		scrolltimeline.getKeyFrames().add(new KeyFrame(Duration.millis(20), "Scoll", (ActionEvent) -> {
			dragScroll(treeTableView);
		}));
		treeTableView.setOnDragExited(event -> {
			if (event.getY() > 0) {
				scrollDirection = 1.0 / treeTableView.getExpandedItemCount();
			} else {
				scrollDirection = -1.0 / treeTableView.getExpandedItemCount();
			}
			scrolltimeline.play();
		});
		treeTableView.setOnDragEntered(event -> {
			scrolltimeline.stop();
		});
		treeTableView.setOnDragDone(event -> {
			scrolltimeline.stop();
		});

	}

	private void dragScroll(final TreeTableView<?> treeTableView) {
		ScrollBar sb = getVerticalScrollbar(treeTableView);
		if (sb != null) {
			double newValue = sb.getValue() + scrollDirection;
			newValue = Math.min(newValue, 1.0);
			newValue = Math.max(newValue, 0.0);
			sb.setValue(newValue);
		}
	}

	private ScrollBar getVerticalScrollbar(final TreeTableView<?> treeTableView) {
		ScrollBar result = null;
		for (Node n : treeTableView.lookupAll(".scroll-bar")) {
			if (n instanceof ScrollBar) {
				ScrollBar bar = (ScrollBar) n;
				if (bar.getOrientation().equals(Orientation.VERTICAL)) {
					result = bar;
				}
			}
		}
		return result;
	}

}
