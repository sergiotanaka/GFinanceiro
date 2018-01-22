package org.pinguin.gf.gui.account;

import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.facade.account.AccountNatureTO;
import org.pinguin.gf.facade.account.AccountService;

import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenAccountCommand implements Function<OpenAccountCommandParam, Void> {

	@Inject
	private Injector injector;

	@Override
	public Void apply(OpenAccountCommandParam param) {

		final AccountService accService = injector.getInstance(AccountService.class);

		final Stage stage = new Stage();
		stage.setTitle("Conta");
		final AccountForm form = injector.getInstance(AccountForm.class);

		form.getPresenter().getNatures().addAll(AccountNatureTO.values());
		form.getPresenter().getParentAccounts().addAll(accService.retrieveAccounts());

		form.getPresenter().setEditMode(param.getEditMode());
		form.getPresenter().setTo(param.getTo());
		form.getPresenter().setCloseWindowCommand((none) -> {
			stage.close();
			return null;
		});
		stage.setScene(new Scene(form));
		stage.sizeToScene();
		stage.initOwner(param.getOwner());
		stage.centerOnScreen();
		stage.show();
		return null;
	}

}
