package org.pinguin.gf.gui.planning;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.pinguin.gf.gui.balance.BalanceReport.SimpleTVCellValueFactory;
import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.planning.PlanningTO;
import org.pinguin.gui.util.Dialog;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;

public class PlanningForm extends AnchorPane {

	@Inject
	private PlanningFormPresenter presenter;

	@FXML
	private AutoCompleteComboBox<PlanningTO> monthYearCombo;
	@FXML
	private TreeTableView<PlanningTO> accPlanTree;
	@FXML
	private TreeTableColumn<PlanningTO, String> accountTColumn;

	public PlanningForm() {
		loadFxml();
		accPlanTree.setShowRoot(false);
		monthYearCombo.setConverter(new StringConverter<PlanningTO>() {

			@Override
			public String toString(PlanningTO object) {
				if (object == null) {
					return "";
				}
				return String.format("%s/%s", object.getMonthYear().getMonth(), object.getMonthYear().getYear());
			}

			@Override
			public PlanningTO fromString(String string) {
				return new PlanningTO();
			}
		});
		final StringConverter<AccountTO> accStrConverter = new AccountStringConverter();

		// Tratamento a entrada de teclado
		accPlanTree.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent evt) {
				if (evt.getCode().equals(KeyCode.DELETE)) {
					int result = Dialog.showQuestionDialog(null, "Confirma a exclusao do item?");
					if (result >= 0) {
//						presenter.deleteAccPlan(accPlanTree.getSelectionModel().getSelectedItem().getValue());
					}
				}
			}
		});
		accPlanTree.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent evt) {
				if (evt.getClickCount() == 3) {
					if (accPlanTree.getSelectionModel().getSelectedItem().getChildren().isEmpty()) {
//						presenter.editAccPlan(accPlanTree.getSelectionModel().getSelectedItem().getValue());
					}
				}
			}
		});

		accountTColumn
				.setCellValueFactory(new SimpleTVCellValueFactory<PlanningTO, AccountTO>("account", accStrConverter));
	}

	@Inject
	public void init() {
		monthYearCombo.setOriginalItems(presenter.getPlannings());
		monthYearCombo.valueProperty().bindBidirectional(presenter.selectedPlanningProp());

		presenter.getAccPlannings().addListener(new ListChangeListener<PlanningTO>() {

			@Override
			public void onChanged(Change<? extends PlanningTO> c) {
				ObservableList<? extends PlanningTO> list = c.getList();
				// Transformar e preencher
				TreeItem<PlanningTO> root = new TreeItem<PlanningTO>(new PlanningTO());
				transform(list, root);
				accPlanTree.setRoot(root);
			}

			private void retrieveParents(Long id, Map<Long, TreeItem<PlanningTO>> map) {
				AccountTO retrieved = presenter.retrieveAccountById(id);
				PlanningTO parentPlan = new PlanningTO();
				// parentPlan.setAccount(retrieved);
				map.put(retrieved.getAccountId(), new TreeItem<PlanningTO>(parentPlan));
				if (retrieved.getParent() != null) {
					retrieveParents(retrieved.getParent().getAccountId(), map);
				}
			}

			private void transform(ObservableList<? extends PlanningTO> list, TreeItem<PlanningTO> root) {
				// 1. Criar TreeItem e guardar no map, por ID da Conta
				Map<Long, TreeItem<PlanningTO>> map = new HashMap<>();
				for (PlanningTO item : list) {
					TreeItem<PlanningTO> treeItem = new TreeItem<>(item);
					// map.put(item.getAccount().getAccountId(), treeItem);
					// // 2. Preencher os pais
					// if (item.getAccount().getParent() != null) {
					// retrieveParents(item.getAccount().getParent().getAccountId(), map);
					// }

				}
				// 2. Montar a hierarquia e guardar os roots
				for (TreeItem<PlanningTO> item : map.values()) {
					// if (item.getValue().getAccount().getParent() != null) {
					// TreeItem<PlanningTO> parent =
					// map.get(item.getValue().getAccount().getParent().getId());
					// parent.getChildren().add(item);
					// } else {
					// root.getChildren().add(item);
					// }
				}
				// 3. Totalizar
				totalize(root);
			}

			private BigDecimal totalize(TreeItem<PlanningTO> root) {
				if (root.getChildren().isEmpty()) {
					// Caso analitico
					// return root.getValue().getValue();
					return null;
				} else {
					// Caso sintetico
					BigDecimal total = BigDecimal.ZERO;
					for (TreeItem<PlanningTO> child : root.getChildren()) {
						total = total.add(totalize(child));
					}
					// root.getValue().setValue(total.setScale(2));
					return total;
				}
			}
		});

	}

	public PlanningFormPresenter getPresenter() {
		return presenter;
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/PlanningForm.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@FXML
	public void add(ActionEvent evt) {
		presenter.add();
	}

	@FXML
	public void addAccPlan(ActionEvent evt) {
		presenter.addAccPlan();
	}

	@FXML
	public void save(ActionEvent evt) {
		presenter.save();
	}
}
