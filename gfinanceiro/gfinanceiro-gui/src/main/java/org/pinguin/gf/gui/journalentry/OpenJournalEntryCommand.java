package org.pinguin.gf.gui.journalentry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.journalentry.TagService;
import org.pinguin.gf.service.api.journalentry.TagTO;

import com.google.inject.Injector;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class OpenJournalEntryCommand implements Function<OpenJournalEntryParam, Void> {

	@Inject
	private Injector injector;

	@Override
	public Void apply(OpenJournalEntryParam param) {

		final Stage formStage = new Stage();
		formStage.setTitle("Lançamento");
		final JournalEntryForm form = injector.getInstance(JournalEntryForm.class);
		final AccountService accService = injector.getInstance(AccountService.class);
		final TagService tagService = injector.getInstance(TagService.class);
		final List<AccountTO> accs = accService.retrieveAnalytical();
		final List<TagTO> allTags = tagService.retrieveAll();
		Collections.sort(accs, new Comparator<AccountTO>() {
			@Override
			public int compare(AccountTO o1, AccountTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		form.getPresenter().getDebitAccounts().addAll(accs);
		form.getPresenter().getCreditAccounts().addAll(accs);
		form.getPresenter().getCandidateTags().addAll(allTags);
		form.getPresenter().setTo(param.getTo());
		form.getPresenter().setEditMode(param.getEditMode());
		form.getPresenter().setCloseWindowCommand((param2) -> {
			formStage.close();
			return null;
		});
		formStage.setScene(new Scene(form));
		formStage.sizeToScene();
		formStage.initOwner(param.getOwner());
		formStage.centerOnScreen();
		formStage.show();

		return null;
	}

}
