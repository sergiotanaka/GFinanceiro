package org.pinguin.gf.gui.planning;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;

import org.pinguin.gf.service.api.account.AccountService;
import org.pinguin.gf.service.api.account.AccountTO;
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

	private Function<Void, Void> addPlanningCommand;
	private Function<AccountPlanningTO, Void> addAccPlanCommand;
	private Function<AccountPlanningTO, Void> editAccPlanCommand;

	private final ObservableList<PlanningTO> plannings = FXCollections.observableArrayList();
	private final Property<PlanningTO> selectedPlanningProp = new SimpleObjectProperty<>();
	private ObservableList<PlanningTO> accPlannings = FXCollections.observableArrayList();

	private final Map<Long, AccountTO> accCache = new HashMap<>();

	public PlanningFormPresenter() {
		selectedPlanningProp.addListener(new ChangeListener<PlanningTO>() {

			@Override
			public void changed(ObservableValue<? extends PlanningTO> observable, PlanningTO oldValue,
					PlanningTO newValue) {
				if (oldValue != null) {
					oldValue.getAccountPlannings().clear();
//					oldValue.getAccountPlannings().addAll(accPlannings);
				}
				if (newValue != null) {
					accPlannings.clear();
//					accPlannings.addAll(newValue.getAccountPlannings());
				}
			}
		});
	}

	public Function<Void, Void> getAddPlanningCommand() {
		return addPlanningCommand;
	}

	public void setAddPlanningCommand(Function<Void, Void> addPlanningCommand) {
		this.addPlanningCommand = addPlanningCommand;
	}

	public Function<AccountPlanningTO, Void> getAddAccPlanCommand() {
		return addAccPlanCommand;
	}

	public void setAddAccPlanCommand(Function<AccountPlanningTO, Void> addAccPlanCommand) {
		this.addAccPlanCommand = addAccPlanCommand;
	}

	public ObservableList<PlanningTO> getPlannings() {
		return plannings;
	}

	public Property<PlanningTO> selectedPlanningProp() {
		return selectedPlanningProp;
	}

	public ObservableList<PlanningTO> getAccPlannings() {
		return accPlannings;
	}

	public Function<AccountPlanningTO, Void> getEditAccPlanCommand() {
		return editAccPlanCommand;
	}

	public void setEditAccPlanCommand(Function<AccountPlanningTO, Void> editAccPlanCommand) {
		this.editAccPlanCommand = editAccPlanCommand;
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
//			selected.getAccountPlannings().addAll(accPlannings);
		}

		for (PlanningTO plan : plannings) {
//			service.updatePlanning(plan.getPlanningId(), plan);
		}
	}

	public void deleteAccPlan(AccountPlanningTO selectedItem) {
		accPlannings.remove(selectedItem);
	}

	public void editAccPlan(AccountPlanningTO selectedItem) {
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
