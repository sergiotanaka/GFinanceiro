package org.pinguin.gf.gui.control;

import org.pinguin.gui.util.PropertyAdapter;
import org.pinguin.gui.util.ValueConverter;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;

/*
 * Copyright (c) 2012, 2017, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * A class containing a {@link TreeTableCell} implementation that draws a
 * {@link ProgressBar} node inside the cell.
 *
 * @param <S> The type of the elements contained within the TableView.
 * @since JavaFX 8.0
 */
public class ProgressBarTreeTableCell<S> extends TreeTableCell<S, Double> {

	private static final String RED_BAR = "red-bar";
	private static final String YELLOW_BAR = "yellow-bar";
	private static final String ORANGE_BAR = "orange-bar";
	private static final String GREEN_BAR = "green-bar";
	private static final String[] BAR_COLOR_STYLE_CLASSES = { RED_BAR, ORANGE_BAR, YELLOW_BAR, GREEN_BAR };

	/***************************************************************************
	 * * Static cell factories * *
	 **************************************************************************/

	/**
	 * Provides a {@link ProgressBar} that allows easy visualisation of a Number
	 * value as it proceeds from 0.0 to 1.0. If the value is -1, the progress bar
	 * will appear indeterminate.
	 *
	 * @param <S> The type of the TreeTableView generic type
	 * @return A {@link Callback} that can be inserted into the
	 *         {@link TreeTableColumn#cellFactoryProperty() cell factory property}
	 *         of a TreeTableColumn, that enables visualisation of a Number as it
	 *         progresses from 0.0 to 1.0.
	 */
	public static <S> Callback<TreeTableColumn<S, Double>, TreeTableCell<S, Double>> forTreeTableColumn() {
		return param -> new ProgressBarTreeTableCell<S>();
	}

	/***************************************************************************
	 * * Fields * *
	 **************************************************************************/

	private final ProgressBar progressBar;
	private final StackPane graphic;
	private final Label label;

	private ObservableValue<Double> observable;

	/***************************************************************************
	 * * Constructors * *
	 **************************************************************************/

	/**
	 * Creates a default {@link ProgressBarTreeTableCell} instance
	 */
	public ProgressBarTreeTableCell() {
		this.getStyleClass().add("progress-bar-tree-table-cell");

		this.progressBar = new ProgressBar();
		this.progressBar.getStylesheets().add(getClass().getResource("/META-INF/css/progress.css").toExternalForm());
		this.progressBar.progressProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				double progress = newValue == null ? 0 : newValue.doubleValue();
				if (progress > 1.0) {
					setBarStyleClass(progressBar, RED_BAR);
				} else if (progress > 0.9) {
					setBarStyleClass(progressBar, YELLOW_BAR);
				} else {
					setBarStyleClass(progressBar, GREEN_BAR);
				}
			}

			private void setBarStyleClass(ProgressBar bar, String barStyleClass) {
				bar.getStyleClass().removeAll(BAR_COLOR_STYLE_CLASSES);
				bar.getStyleClass().add(barStyleClass);
			}

		});
		this.label = new Label("50.00%");
		this.graphic = new StackPane(progressBar, label);
		this.progressBar.setMaxWidth(Double.MAX_VALUE);
	}

	/***************************************************************************
	 * * Public API * *
	 **************************************************************************/

	/** {@inheritDoc} */
	@Override
	public void updateItem(Double item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setGraphic(null);
		} else {
			progressBar.progressProperty().unbind();

			final TreeTableColumn<S, Double> column = getTableColumn();
			observable = column == null ? null : column.getCellObservableValue(getIndex());

			if (observable != null) {
				progressBar.progressProperty().bind(observable);

				final ValueConverter<String, Double> converter = new ValueConverter<String, Double>() {

					@Override
					public Double convert2(String value) {
						return Double.parseDouble(value);
					}

					@Override
					public String convert1(Double value) {
						return Double.toString(value);
					}
				};
				final PropertyAdapter<String, Double> percent = new PropertyAdapter<String, Double>(
						(Property<Double>) observable, converter);
				label.textProperty().bind(percent);
			} else if (item != null) {
				progressBar.setProgress(item);
			}

			setGraphic(graphic);
		}
	}
}
