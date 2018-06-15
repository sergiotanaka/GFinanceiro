package org.pinguin.pomodoro.gui.mini;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;

public class MiniPane extends BorderPane {

	private final ProgressIndicator ind;

	/**
	 * Construtor.
	 */
	public MiniPane() {
		this.setPadding(new Insets(10.0));
		ind = new ProgressIndicator(0.0);
		this.setCenter(ind);
	}
	
	public DoubleProperty progressProperty() {
		return ind.progressProperty();
	}

}
