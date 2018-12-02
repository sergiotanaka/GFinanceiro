package org.pinguin.gf.gui.planning;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.balance.BalanceService;
import org.pinguin.gf.service.api.planning.AccountPlanningTO;
import org.pinguin.gf.service.api.planning.PlanningService;
import org.pinguin.gf.service.api.planning.PlanningTO;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlanningFormPresenter {

	@Inject
	private PlanningService service;
	@Inject
	private AccountService accService;
	@Inject
	private BalanceService balService;

	private Function<Void, Void> addPlanningCommand;
	private Function<AccountPlanningItem, Void> addAccPlanCommand;
	private Function<AccountPlanningItem, Void> editAccPlanCommand;

	private final ObservableList<PlanningTO> plannings = FXCollections.observableArrayList();
	private final Property<PlanningTO> selectedPlanningProp = new SimpleObjectProperty<>();
	private ObservableList<AccountPlanningItem> accPlannings = FXCollections.observableArrayList();

	private final Map<Long, AccountTO> accCache = new HashMap<>();

	public PlanningFormPresenter() {
		selectedPlanningProp.addListener(new ChangeListener<PlanningTO>() {

			@Override
			public void changed(ObservableValue<? extends PlanningTO> observable, PlanningTO oldValue,
					PlanningTO newValue) {
				if (oldValue != null) {
					oldValue.getAccountPlannings().clear();
					oldValue.getAccountPlannings().addAll(map2(accPlannings));
				}
				if (newValue != null) {
					accPlannings.clear();
					accPlannings.addAll(map(newValue.getAccountPlannings()));
				}
			}
		});
	}

	protected List<AccountPlanningItem> map(Collection<AccountPlanningTO> plannings) {

		final List<AccountPlanningItem> result = new ArrayList<>();

		for (final AccountPlanningTO plan : plannings) {
			final AccountPlanningItem item = new AccountPlanningItem();
			item.accPlanIdProperty().setValue(plan.getAccPlanId());
			item.accountProperty().setValue(plan.getAccount());
			item.valueProperty().setValue(plan.getValue());
			result.add(item);
		}

		return result;
	}

	private Set<AccountPlanningTO> map2(Collection<AccountPlanningItem> plannings) {
		final Set<AccountPlanningTO> result = new HashSet<>();
		for (final AccountPlanningItem item : plannings) {
			final AccountPlanningTO plan = new AccountPlanningTO();
			plan.setAccPlanId(item.accPlanIdProperty().getValue());
			plan.setAccount(item.accountProperty().getValue());
			plan.setValue(item.valueProperty().getValue());
			result.add(plan);
		}
		return result;
	}

	public Function<Void, Void> getAddPlanningCommand() {
		return addPlanningCommand;
	}

	public void setAddPlanningCommand(Function<Void, Void> addPlanningCommand) {
		this.addPlanningCommand = addPlanningCommand;
	}

	public Function<AccountPlanningItem, Void> getAddAccPlanCommand() {
		return addAccPlanCommand;
	}

	public void setAddAccPlanCommand(Function<AccountPlanningItem, Void> addAccPlanCommand) {
		this.addAccPlanCommand = addAccPlanCommand;
	}

	public ObservableList<PlanningTO> getPlannings() {
		return plannings;
	}

	public Property<PlanningTO> selectedPlanningProp() {
		return selectedPlanningProp;
	}

	public ObservableList<AccountPlanningItem> getAccPlannings() {
		return accPlannings;
	}

	public Function<AccountPlanningItem, Void> getEditAccPlanCommand() {
		return editAccPlanCommand;
	}

	public void setEditAccPlanCommand(Function<AccountPlanningItem, Void> editAccPlanCommand) {
		this.editAccPlanCommand = editAccPlanCommand;
	}

	public BalanceService getBalService() {
		return balService;
	}

	public void add() {
		if (addPlanningCommand != null) {
			addPlanningCommand.apply(null);
		}
	}

	public void addAccPlan() {
		if (addAccPlanCommand != null) {
			addAccPlanCommand.apply(null);
		}
	}

	public void save() {
		final PlanningTO selected = selectedPlanningProp.getValue();
		if (selected != null) {
			selected.getAccountPlannings().clear();
			selected.getAccountPlannings().addAll(map2(accPlannings));
		}

		for (PlanningTO plan : plannings) {
			service.updatePlanning(plan.getPlanId(), plan);
		}
	}

	public void deleteAccPlan(AccountPlanningItem selectedItem) {
		accPlannings.remove(selectedItem);
	}

	public void editAccPlan(AccountPlanningItem selectedItem) {
		if (editAccPlanCommand != null) {
			editAccPlanCommand.apply(selectedItem);
		}
	}

	public AccountTO retrieveAccountById(Long id) {
		if (!accCache.containsKey(id)) {
			accCache.put(id, accService.retrieveById(id));
		}
		return accCache.get(id);
	}

}
