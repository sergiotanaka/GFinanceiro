package org.pinguin.gf.gui.planning;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.pinguin.gf.gui.accstatement.OpenAccStatementCommand;
import org.pinguin.gf.gui.accstatement.OpenAccStatementParam;
import org.pinguin.gf.gui.control.AutoCompleteComboBox;
import org.pinguin.gf.gui.control.ProgressBarTreeTableCell;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.balance.BalanceTO;
import org.pinguin.gf.service.api.planning.MonthYearTO;
import org.pinguin.gf.service.api.planning.PlanningTO;
import org.pinguin.gui.util.Dialog;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * IHM "Planejamento"
 */
public class PlanningForm extends AnchorPane {

	@Inject
	private PlanningFormPresenter presenter;
	@Inject
	private OpenAccStatementCommand openAccStatement;

	@FXML
	private AutoCompleteComboBox<PlanningTO> monthYearCombo;
	@FXML
	private TreeTableView<AccountPlanningItem> accPlanTree;
	@FXML
	private TreeTableColumn<AccountPlanningItem, AccountTO> accountTColumn;
	@FXML
	private TreeTableColumn<AccountPlanningItem, Double> percentTColumn;

	/**
	 * Construtor.
	 */
	public PlanningForm() {
		loadFxml();
		accPlanTree.setShowRoot(false);
		monthYearCombo.setConverter(buildPlanStrConverter());
		// Tratamento a entrada de teclado
		accPlanTree.setOnKeyPressed(buildKeyHandler());
		accPlanTree.setOnMouseClicked(buildMouseHandler());
		accountTColumn.setCellFactory(buildAccCellFactory());
		percentTColumn.setCellFactory(ProgressBarTreeTableCell.forTreeTableColumn());
	}

	@Inject
	public void init() {
		monthYearCombo.setOriginalItems(presenter.getPlannings());
		monthYearCombo.valueProperty().bindBidirectional(presenter.selectedPlanningProp());

		// "Bind" entre o presenter#accPlannings e o TreeTableView
		presenter.getAccPlannings().addListener(new ListChangeListener<AccountPlanningItem>() {

			@Override
			public void onChanged(Change<? extends AccountPlanningItem> c) {
				ObservableList<? extends AccountPlanningItem> list = c.getList();
				// TODO estou reconstruindo a arvore toda vez. talvez pudesso so' inserir ou
				// alterar
				// Transformar e preencher
				TreeItem<AccountPlanningItem> root = new TreeItem<AccountPlanningItem>(new AccountPlanningItem());
				transform(list, root);
				accPlanTree.setRoot(root);
			}

			private void retrieveParents(Long id, Map<Long, TreeItem<AccountPlanningItem>> map) {
				AccountTO retrieved = presenter.retrieveAccountById(id);
				AccountPlanningItem parentPlan = new AccountPlanningItem();
				parentPlan.accountProperty().setValue(retrieved);
				map.put(retrieved.getAccountId(), new TreeItem<AccountPlanningItem>(parentPlan));
				if (retrieved.getParent() != null) {
					retrieveParents(retrieved.getParent().getAccountId(), map);
				}
			}

			private void transform(ObservableList<? extends AccountPlanningItem> list,
					TreeItem<AccountPlanningItem> root) {
				// 1. Criar TreeItem e guardar no map, por ID da Conta
				final Map<Long, TreeItem<AccountPlanningItem>> map = new HashMap<>();
				for (final AccountPlanningItem item : list) {
					final TreeItem<AccountPlanningItem> treeItem = new TreeItem<>(item);
					map.put(item.accountProperty().getValue().getAccountId(), treeItem);
					// 2. Preencher os pais
					if (item.accountProperty().getValue().getParent() != null) {
						retrieveParents(item.accountProperty().getValue().getParent().getAccountId(), map);
					}

				}
				// 2. Montar a hierarquia e guardar os roots
				for (final TreeItem<AccountPlanningItem> item : map.values()) {
					if (item.getValue().accountProperty().getValue().getParent() != null) {
						final TreeItem<AccountPlanningItem> parent = map
								.get(item.getValue().accountProperty().getValue().getParent().getAccountId());
						parent.getChildren().add(item);
					} else {
						root.getChildren().add(item);
					}
				}
				// 3. Preencher realizados
				final Map<Long, BigDecimal> balanceMap = retrieveBalanceMap();
				fillAccomplished(root, balanceMap);

				// 4. Totalizar planejados
				totalize(root);
			}

			private Map<Long, BigDecimal> retrieveBalanceMap() {
				MonthYearTO monthYear = presenter.selectedPlanningProp().getValue().getMonthYear();
				final LocalDate start = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), 1);
				final LocalDate end = LocalDate.of(monthYear.getYear(), monthYear.getMonth(), 1).plusMonths(1)
						.minusDays(1);
				final List<BalanceTO> balance = presenter.getBalService().retrieveBalance(start, end, "", false);
				final Map<Long, BigDecimal> balanceMap = balance.stream()
						.collect(Collectors.toMap(b -> b.getAccount().getAccountId(), BalanceTO::getBalance));
				return balanceMap;
			}

			private BigDecimal fillAccomplished(final TreeItem<AccountPlanningItem> item, Map<Long, BigDecimal> map) {
				if (item.getChildren().isEmpty()) {
					// Caso analitico
					if (item.getValue().accountProperty().getValue() == null
							|| !map.containsKey(item.getValue().accountProperty().getValue().getAccountId())) {
						return BigDecimal.ZERO;
					}
					final BigDecimal balance = map.get(item.getValue().accountProperty().getValue().getAccountId());
					item.getValue().accomplishedProperty().setValue(balance);
					return balance;
				} else {
					// Caso sintetico
					BigDecimal total = BigDecimal.ZERO;
					for (TreeItem<AccountPlanningItem> child : item.getChildren()) {
						total = total.add(fillAccomplished(child, map));
					}
					item.getValue().accomplishedProperty().setValue(total.setScale(2));
					return total;
				}
			}

			private BigDecimal totalize(TreeItem<AccountPlanningItem> root) {
				if (root.getChildren().isEmpty()) {
					// Caso analitico
					if (root.getValue().valueProperty().getValue() == null) {
						return BigDecimal.ZERO;
					}
					return root.getValue().valueProperty().getValue();
				} else {
					// Caso sintetico
					BigDecimal total = BigDecimal.ZERO;
					for (TreeItem<AccountPlanningItem> child : root.getChildren()) {
						total = total.add(totalize(child));
					}
					root.getValue().valueProperty().setValue(total.setScale(2));
					return total;
				}
			}
		});

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem copy = new MenuItem("Copiar para área de transferência");
		copy.setOnAction(e -> {
			StringBuilder sb = new StringBuilder();
			appendItems(accPlanTree.getRoot(), "\u2514", sb);
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(sb.toString());
			clipboard.setContent(content);
		});
		final MenuItem detail = new MenuItem("Abrir detalhes");
		detail.setOnAction(e -> {
			MonthYearTO monthYear = presenter.selectedPlanningProp().getValue().getMonthYear();
			final Calendar ini = GregorianCalendar.getInstance();
			ini.set(monthYear.getYear(), monthYear.getMonth().getValue() - 1, 1, 0, 0, 0);
			Calendar end = (Calendar) ini.clone();
			end.add(Calendar.MONTH, 1);
			end.add(Calendar.SECOND, -1);
			AccountPlanningItem selected = accPlanTree.getSelectionModel().getSelectedItem().getValue();
			openAccStatement
					.apply(new OpenAccStatementParam(null, selected.accountProperty().getValue(), ini, end, ""));
		});

		final MenuItem edit = new MenuItem("Editar");
		edit.setOnAction(e -> {
			if (accPlanTree.getSelectionModel().getSelectedItem().getChildren().isEmpty()) {
				presenter.editAccPlan(accPlanTree.getSelectionModel().getSelectedItem().getValue());
			}
		});

		contextMenu.getItems().addAll(edit, detail, copy);
		accPlanTree.setContextMenu(contextMenu);
	}

	private void appendItems(final TreeItem<AccountPlanningItem> treeItem, final String prefix,
			final StringBuilder sb) {
		for (final TreeItem<AccountPlanningItem> item : treeItem.getChildren()) {
			sb.append(prefix + item.getValue().accountProperty().getValue().getName()).append("\t");
			sb.append(format(item.getValue().valueProperty().getValue())).append("\t");
			sb.append(format(item.getValue().accomplishedProperty().getValue())).append("\t");
			sb.append(format(item.getValue().balanceProperty().getValue())).append("\t");
			sb.append(format(item.getValue().percentProperty().getValue())).append("\n");
			if (!item.getChildren().isEmpty()) {
				appendItems(item, prefix + "\u2500\u2500", sb);
			}
		}
	}

	private String format(final BigDecimal number) {
		Format format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		return format.format(number);
	}

	private String format(final Double number) {
		Format format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		return format.format(number);
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

	@FXML
	public void report(ActionEvent evt) {
		presenter.report();
	}

	private StringConverter<PlanningTO> buildPlanStrConverter() {
		return new StringConverter<PlanningTO>() {

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
		};
	}

	private EventHandler<KeyEvent> buildKeyHandler() {
		return new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent evt) {
				if (evt.getCode().equals(KeyCode.DELETE)) {
					int result = Dialog.showQuestionDialog(null, "Confirma a exclusao do item?");
					if (result >= 0) {
						presenter.deleteAccPlan(accPlanTree.getSelectionModel().getSelectedItem().getValue());
					}
				}
			}
		};
	}

	private EventHandler<MouseEvent> buildMouseHandler() {
		return new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent evt) {
				if (evt.getClickCount() == 3) {
					if (accPlanTree.getSelectionModel().getSelectedItem().getChildren().isEmpty()) {
						presenter.editAccPlan(accPlanTree.getSelectionModel().getSelectedItem().getValue());
					}
				}
			}
		};
	}

	private Callback<TreeTableColumn<AccountPlanningItem, AccountTO>, TreeTableCell<AccountPlanningItem, AccountTO>> buildAccCellFactory() {
		return new Callback<TreeTableColumn<AccountPlanningItem, AccountTO>, TreeTableCell<AccountPlanningItem, AccountTO>>() {

			@Override
			public TreeTableCell<AccountPlanningItem, AccountTO> call(
					TreeTableColumn<AccountPlanningItem, AccountTO> param) {
				return new TreeTableCell<>() {

					@Override
					protected void updateItem(AccountTO item, boolean empty) {
						if (item == getItem()) {
							return;
						}
						super.updateItem(item, empty);
						if (item == null) {
							super.setText(null);
							super.setGraphic(null);
						} else {
							super.setText(item.getName());
							super.setGraphic(null);
						}
					}

				};
			}
		};
	}

}
