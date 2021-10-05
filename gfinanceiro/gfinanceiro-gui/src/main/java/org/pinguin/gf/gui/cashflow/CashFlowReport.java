package org.pinguin.gf.gui.cashflow;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;

import javax.inject.Inject;

import org.pinguin.gf.gui.accstatement.OpenAccStatementCommand;
import org.pinguin.gf.gui.accstatement.OpenAccStatementParam;
import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.account.DayResultTO;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTextField;

public class CashFlowReport extends AnchorPane {

	@FXML
	private AutoCompleteComboBox<AccountTO> accountCombo;
	@FXML
	private CalendarTextField startDateText;
	@FXML
	private CalendarTextField endDateText;
	@SuppressWarnings("rawtypes")
	@FXML
	private BarChart barChart;
	@SuppressWarnings("rawtypes")
	@FXML
	private LineChart lineChart;
	@FXML
	private RadioButton balanceRadio;

	final XYChart.Series<String, BigDecimal> series1 = new XYChart.Series<>();
	final XYChart.Series<String, BigDecimal> series2 = new XYChart.Series<>();

	@Inject
	private CashFlowPresenter presenter;
	@Inject
	private OpenAccStatementCommand openAccStatement;

	@SuppressWarnings("unchecked")
	public CashFlowReport() {
		loadFxml();
		final StringConverter<AccountTO> stringConverter = new AccountStringConverter();
		accountCombo.setConverter(stringConverter);
		barChart.getData().add(series1);
		barChart.setLegendVisible(false);

		lineChart.getData().add(series2);
		lineChart.setLegendVisible(false);
		lineChart.setAnimated(false);
		lineChart.setCreateSymbols(true);
		lineChart.setAlternativeRowFillVisible(false);
		lineChart.setAlternativeColumnFillVisible(false);
		lineChart.setHorizontalGridLinesVisible(false);
		lineChart.setVerticalGridLinesVisible(false);
		lineChart.getXAxis().setVisible(false);
		lineChart.getYAxis().setVisible(false);
		((NumberAxis) lineChart.getYAxis()).setAutoRanging(false);
		((NumberAxis) lineChart.getYAxis()).setLowerBound(0.0);
		((NumberAxis) lineChart.getYAxis()).setUpperBound(10000.0);
		((NumberAxis) lineChart.getYAxis()).setTickUnit(1000.0);
		((NumberAxis) barChart.getYAxis()).setAutoRanging(false);
		((NumberAxis) barChart.getYAxis()).setLowerBound(0.0);
		((NumberAxis) barChart.getYAxis()).setUpperBound(10000.0);
		((NumberAxis) barChart.getYAxis()).setTickUnit(1000.0);
		lineChart.getStylesheets().addAll(getClass().getResource("/META-INF/css/chart.css").toExternalForm());

		// lineChart.setMouseTransparent(true);
//		lineChart.setPickOnBounds(false);

		lineChart.mouseTransparentProperty().bind(balanceRadio.selectedProperty().not());
	}

	@Inject
	private void init() {
		startDateText.calendarProperty().bindBidirectional(presenter.startDateProperty());
		endDateText.calendarProperty().bindBidirectional(presenter.endDateProperty());
		accountCombo.setOriginalItems(presenter.getAccounts());
		accountCombo.valueProperty().bindBidirectional(presenter.accountProperty());
	}

	@FXML
	public void retrieve(ActionEvent evt) {
		presenter.retrieve();

		series1.getData().clear();
		series2.getData().clear();
		BigDecimal lower = null;
		BigDecimal upper = null;
		for (final DayResultTO item : presenter.getCashFlow()) {
			if (lower == null) {
				lower = item.getBalance();
				upper = item.getBalance();
			}
			if (item.getResult().compareTo(lower) < 0) {
				lower = item.getResult();
			}
			if (item.getBalance().compareTo(lower) < 0) {
				lower = item.getBalance();
			}
			if (item.getResult().compareTo(upper) > 0) {
				upper = item.getResult();
			}
			if (item.getBalance().compareTo(upper) > 0) {
				upper = item.getBalance();
			}
			series1.getData()
					.add(new Data<String, BigDecimal>(item.getDate().toString(), item.getResult(), item.getDate()));
			series2.getData()
					.add(new Data<String, BigDecimal>(item.getDate().toString(), item.getBalance(), item.getDate()));
		}
		series1.getData().forEach(d -> {
			Tooltip tip = new Tooltip();
			tip.setText(d.getYValue() + "");
			Tooltip.install(d.getNode(), tip);
			d.getNode().setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					openAccStatement.apply(new OpenAccStatementParam(null, accountCombo.getValue(),
							map((LocalDate) d.getExtraValue()), map((LocalDate) d.getExtraValue()), ""));
				}
			});
		});
		series2.getData().forEach(d -> {
			Tooltip tip = new Tooltip();
			tip.setText(d.getYValue() + "");
			Tooltip.install(d.getNode(), tip);
			d.getNode().setOnMouseClicked(e -> {
				if (e.getClickCount() == 2) {
					openAccStatement.apply(new OpenAccStatementParam(null, accountCombo.getValue(),
							map((LocalDate) d.getExtraValue()), map((LocalDate) d.getExtraValue()), ""));
				}
			});
		});

		((NumberAxis) lineChart.getYAxis()).setLowerBound(lower.doubleValue() - 500.0);
		((NumberAxis) lineChart.getYAxis()).setUpperBound(upper.doubleValue() + 500.0);
		((NumberAxis) barChart.getYAxis()).setLowerBound(lower.doubleValue() - 500.0);
		((NumberAxis) barChart.getYAxis()).setUpperBound(upper.doubleValue() + 500.0);
	}

	private Calendar map(final LocalDate localDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth(), 0, 0, 0);
		return calendar;
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/CashFlowReport.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}
}
