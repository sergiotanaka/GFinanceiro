package org.pinguin.gf.gui.accstatement;

import static java.util.Optional.empty;

import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;

import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenAccStatementCommand implements Function<OpenAccStatementParam, Void> {

	@Inject
	private Injector injector;

	@Override
	public Void apply(OpenAccStatementParam param) {
		AccountService accService = injector.getInstance(AccountService.class);

		AccStatementReport report = injector.getInstance(AccStatementReport.class);
		report.getPresenter().getAccounts().addAll(accService.retrieveAll(empty(), empty(), empty(), empty(), empty()));

		if (param.getAccount() != null) {
			report.getPresenter().accountProperty().setValue(param.getAccount());
		}
		if (param.getStartDate() != null) {
			report.getPresenter().startDateProperty().setValue(param.getStartDate());
		}
		if (param.getEndDate() != null) {
			report.getPresenter().endDateProperty().setValue(param.getEndDate());
		}

		Stage listStage = new Stage();
		listStage.setTitle("Historico");
		listStage.setScene(new Scene(report));
		listStage.sizeToScene();
		listStage.initOwner(param.getOwner());
		listStage.centerOnScreen();
		listStage.show();
		return null;
	}

}
