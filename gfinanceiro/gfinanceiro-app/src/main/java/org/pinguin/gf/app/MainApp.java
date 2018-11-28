package org.pinguin.gf.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.pinguin.gf.gui.account.AccountList;
import org.pinguin.gf.gui.accstatement.OpenAccStatementCommand;
import org.pinguin.gf.gui.accstatement.OpenAccStatementParam;
import org.pinguin.gf.gui.balance.BalanceReport;
import org.pinguin.gf.gui.cashflow.CashFlowReport;
import org.pinguin.gf.gui.journalentry.JournalEntryListForm;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryCommand;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryParam;
import org.pinguin.gf.gui.main.MainPane;
import org.pinguin.gf.gui.planning.AccPlanningForm;
import org.pinguin.gf.gui.planning.AddPlanningForm;
import org.pinguin.gf.gui.planning.PlanningForm;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.journalentry.JournalEntryService;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.pinguin.gf.service.api.planning.AccountPlanningTO;
import org.pinguin.gf.service.api.planning.MonthYearTO;
import org.pinguin.gf.service.api.planning.PlanningService;
import org.pinguin.gf.service.api.planning.PlanningTO;
import org.pinguin.gui.util.Dialog;
import org.pinguin.gui.util.EditMode;

import com.google.inject.Guice;
import com.google.inject.Injector;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Injector injector;

	@Override
	public void start(final Stage stage) throws Exception {

		initResources();

		MainPane mainPane = injector.getInstance(MainPane.class);

		stage.setTitle("Controle financeiro");

		mainPane.setOnJournalEntryHandler((evt) -> {
			JournalEntryTO newTo = new JournalEntryTO();
			final LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
			newTo.setDate(today);
			newTo.setValue(BigDecimal.valueOf(0.0).setScale(2));
			injector.getInstance(OpenJournalEntryCommand.class)
					.apply(new OpenJournalEntryParam(newTo, stage, EditMode.CREATE));
		});

		mainPane.setOnListJournalEntryHandler(
				(evt) -> injector.getInstance(OpenAccStatementCommand.class).apply(new OpenAccStatementParam(stage)));

		mainPane.setOnJournalEntryListHandler((evt) -> {
			JournalEntryListForm listForm = injector.getInstance(JournalEntryListForm.class);
			Stage listStage = new Stage();
			listStage.setTitle("Lançamentos");
			listStage.setScene(new Scene(listForm));
			listStage.sizeToScene();
			listStage.initOwner(stage);
			listStage.centerOnScreen();
			listStage.show();
		});

		mainPane.setOnListBalance((evt) -> {
			BalanceReport report = injector.getInstance(BalanceReport.class);
			Stage listStage = new Stage();
			listStage.setTitle("Resumo");
			listStage.setScene(new Scene(report));
			listStage.sizeToScene();
			listStage.initOwner(stage);
			listStage.centerOnScreen();
			listStage.show();
		});

		mainPane.setOnPlanningHandler((evt) -> {

			PlanningService service = injector.getInstance(PlanningService.class);

			final Stage formStage = new Stage();
			formStage.setTitle("Planejamento");

			final PlanningForm form = injector.getInstance(PlanningForm.class);

			form.getPresenter().setAddPlanningCommand((param) -> {

				final LocalDate now = LocalDate.now();

				final Stage addFormStage = new Stage();
				addFormStage.setTitle("Adicionar planejamento");
				AddPlanningForm addForm = injector.getInstance(AddPlanningForm.class);
				addForm.getPresenter().getMonths().addAll(Month.values());
				PlanningTO newTO = new PlanningTO();
				newTO.setMonthYear(new MonthYearTO(now.getMonth(), now.getYear()));
				addForm.getPresenter().setTo(newTO);
				addFormStage.setScene(new Scene(addForm));
				addFormStage.sizeToScene();
				addFormStage.initOwner(formStage);
				addFormStage.centerOnScreen();
				addFormStage.show();

				return null;
			});
			Function<AccountPlanningTO, Void> editAccPlanCommand = (to) -> {

				AccountService accService = injector.getInstance(AccountService.class);

				final Stage accPlanStage = new Stage();
				accPlanStage.setTitle("Planejamento da conta");
				AccPlanningForm accPlanForm = injector.getInstance(AccPlanningForm.class);
				accPlanForm.getPresenter().getAccounts().addAll(accService.retrieveIncomeAccounts());
				accPlanForm.getPresenter().getAccounts().addAll(accService.retrieveExpenseAccounts());
				if (to == null) {
					accPlanForm.getPresenter().setTo(new AccountPlanningTO());
					accPlanForm.getPresenter().setEditMode(EditMode.CREATE);
				} else {
					accPlanForm.getPresenter().setTo(to);
					accPlanForm.getPresenter().setEditMode(EditMode.UPDATE);
				}
				accPlanForm.getPresenter().setOnSaveCommand((param) -> {
					List<AccountPlanningTO> aux = new ArrayList<>();
					// aux.addAll(form.getPresenter().getAccPlannings());
					form.getPresenter().getAccPlannings().clear();
					// form.getPresenter().getAccPlannings().addAll(aux);
					if (accPlanForm.getPresenter().getEditMode().equals(EditMode.CREATE)) {
						// form.getPresenter().getAccPlannings().add(accPlanForm.getPresenter().getTo());
					}
					accPlanStage.close();
					return null;
				});
				accPlanStage.setScene(new Scene(accPlanForm));
				accPlanStage.sizeToScene();
				accPlanStage.initOwner(formStage);
				accPlanStage.centerOnScreen();
				accPlanStage.showAndWait();

				return null;
			};
			form.getPresenter().setAddAccPlanCommand(editAccPlanCommand);
			form.getPresenter().setEditAccPlanCommand(editAccPlanCommand);
			form.getPresenter().getPlannings().addAll(service.retrieveAll());

			formStage.setScene(new Scene(form));
			formStage.sizeToScene();
			formStage.initOwner(stage);
			formStage.centerOnScreen();
			formStage.show();
		});

		mainPane.setOnMaintAccount((evt) -> {
			AccountList list = injector.getInstance(AccountList.class);
			Stage formStage = new Stage();
			formStage.setTitle("Cadastro de contas");
			formStage.setScene(new Scene(list));
			formStage.sizeToScene();
			formStage.initOwner(stage);
			formStage.centerOnScreen();
			formStage.show();
		});

		mainPane.setOnClearEntriesHandler(e -> {
			// Confirmacao
			int result = Dialog.showQuestionDialog(null, "Confirma a limpeza dos lançamentos?");
			if (result >= 0) {
				final JournalEntryService service = injector.getInstance(JournalEntryService.class);
				final List<JournalEntryTO> entries = service.retrieveAll();

				for (JournalEntryTO entry : entries) {
					service.deleteEntry(entry.getEntryId());
				}
			}
		});

		mainPane.setOnCashFlowHandler(e -> {
			final CashFlowReport cashFlowReport = injector.getInstance(CashFlowReport.class);
			Stage repStage = new Stage();
			repStage.setTitle("Fluxo de Caixa");
			repStage.setScene(new Scene(cashFlowReport));
			repStage.sizeToScene();
			repStage.initOwner(stage);
			repStage.centerOnScreen();
			repStage.show();
		});

		stage.setScene(new Scene(mainPane));
		stage.sizeToScene();
		stage.centerOnScreen();
		stage.show();

	}

	@Override
	public void stop() throws Exception {
	}

	private void initResources() {
		injector = Guice.createInjector(new MainModule());
	}

	public static void main(String[] args) {
		launch(args);
	}

}
