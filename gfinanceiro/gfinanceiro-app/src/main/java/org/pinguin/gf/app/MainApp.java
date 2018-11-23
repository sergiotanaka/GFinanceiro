package org.pinguin.gf.app;

import static java.util.Optional.empty;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

import org.pinguin.gf.gui.account.AccountList;
import org.pinguin.gf.gui.accstatement.OpenAccStatementCommand;
import org.pinguin.gf.gui.accstatement.OpenAccStatementParam;
import org.pinguin.gf.gui.balance.BalanceReport;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryCommand;
import org.pinguin.gf.gui.journalentry.OpenJournalEntryParam;
import org.pinguin.gf.gui.main.MainPane;
import org.pinguin.gf.gui.planning.AccPlanningForm;
import org.pinguin.gf.gui.planning.AddPlanningForm;
import org.pinguin.gf.gui.planning.PlanningForm;
import org.pinguin.gf.service.api.account.AccountNatureTO;
import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.JournalEntryTO;
import org.pinguin.gf.service.api.planning.AccountPlanningTO;
import org.pinguin.gf.service.api.planning.MonthTO;
import org.pinguin.gf.service.api.planning.PlanningService;
import org.pinguin.gf.service.api.planning.PlanningTO;
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

		updateDB();

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

				Calendar calendar = Calendar.getInstance();

				final Stage addFormStage = new Stage();
				addFormStage.setTitle("Adicionar planejamento");
				AddPlanningForm addForm = injector.getInstance(AddPlanningForm.class);
				addForm.getPresenter().getMonths().addAll(MonthTO.values());
				PlanningTO newTO = new PlanningTO();
				// newTO.setMonthYear(new MonthYearTO(null, calendar.get(Calendar.YEAR)));
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
					// accPlanForm.getPresenter().setTo(new AccountPlanningTO());
					accPlanForm.getPresenter().setEditMode(EditMode.CREATE);
				} else {
					// accPlanForm.getPresenter().setTo(to);
					accPlanForm.getPresenter().setEditMode(EditMode.UPDATE);
				}
				accPlanForm.getPresenter().setOnSaveCommand((param) -> {
					List<AccountPlanningTO> aux = new ArrayList<>();
					// aux.addAll(form.getPresenter().getAccPlannings());
					form.getPresenter().getAccPlannings().clear();
					// form.getPresenter().getAccPlannings().addAll(aux);
					if (accPlanForm.getPresenter().getEditMode().equals(EditMode.CREATE)) {
						form.getPresenter().getAccPlannings().add(accPlanForm.getPresenter().getTo());
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
			// form.getPresenter().setAddAccPlanCommand(editAccPlanCommand);
			// form.getPresenter().setEditAccPlanCommand(editAccPlanCommand);
			// form.getPresenter().getPlannings().addAll(service.retrievePlannings());

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

		// Ativo e despesa tem natureza devedora
		AccountService accService = injector.getInstance(AccountService.class);

		AccountTO created = accService.createAccount(new AccountTO("Teste2", AccountNatureTO.DEBIT));
		accService.updateAccount(created.getAccountId(), created);
		accService.deleteAccount(created.getAccountId());

		if (accService.retrieveAll(empty(), empty(), empty(), empty(), empty()).isEmpty()) {
			accService.createAccount(new AccountTO("Caixa", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("C/C Santander", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Poupança Santander", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Investimento - GP", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Investimento - CDB", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Cartão de crédito", AccountNatureTO.CREDIT));
			// Despesas
			accService.createAccount(new AccountTO("Contas residenciais", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Mercado", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Moradia", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Saúde", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Transporte", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Bares / Restaurantes", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Compras", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Cuidados pessoais", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Impostos / Taxas", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Lazer", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("Presentes / Doações", AccountNatureTO.DEBIT));
			accService.createAccount(new AccountTO("TV / Internet / Telefonia", AccountNatureTO.DEBIT));
			// Receitas
			accService.createAccount(new AccountTO("Salário", AccountNatureTO.CREDIT));
			// Capital
			accService.createAccount(new AccountTO("Capital", AccountNatureTO.CREDIT));
		}
	}

	private void updateDB() {

		// EntityManager em = injector.getInstance(EntityManager.class);
		// em.getTransaction().begin();
		//
		// Query query = em.createNativeQuery("select max(j.id) from JournalEntry j");
		// BigInteger result = (BigInteger) query.getSingleResult();
		// if (result == null) {
		// result = BigInteger.ZERO;
		// }
		// System.out.println(result);
		//
		// Query query2 = em.createNativeQuery("select HIBERNATE_SEQUENCE.currval from
		// dual");
		// Object result2 = query2.getSingleResult();
		// System.out.println(result2);
		//
		// Query query3 = em
		// .createNativeQuery("alter sequence HIBERNATE_SEQUENCE restart with " +
		// (result.intValue() + 2));
		// query3.executeUpdate();
		//
		// // Query query = em.createNativeQuery("update Appointment set status =
		// // 'TODO' where status is null");
		// // query.executeUpdate();
		//
		// em.getTransaction().commit();
		//
		// // EntityManager em = injector.getInstance(EntityManager.class);
		// // em.getTransaction().begin();
		// //
		// // Query query = em.createNativeQuery("alter table Appointment add
		// // column if not exists aux varchar(255)");
		// // query.executeUpdate();
		// //
		// // query = em.createNativeQuery("update Appointment set aux =
		// // description");
		// // query.executeUpdate();
		// //
		// // query = em.createNativeQuery("alter table Appointment drop column
		// // description");
		// // query.executeUpdate();
		// //
		// // query = em.createNativeQuery("alter table Appointment add column
		// // description clob");
		// // query.executeUpdate();
		// //
		// // query = em.createNativeQuery("update Appointment set description =
		// // aux");
		// // query.executeUpdate();
		// //
		// // query = em.createNativeQuery("alter table Appointment drop column
		// // aux");
		// // query.executeUpdate();
		// //
		// // em.getTransaction().commit();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
