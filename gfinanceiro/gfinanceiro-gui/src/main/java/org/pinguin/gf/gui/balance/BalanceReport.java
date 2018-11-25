package org.pinguin.gf.gui.balance;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.pinguin.gf.gui.accstatement.OpenAccStatementCommand;
import org.pinguin.gf.gui.accstatement.OpenAccStatementParam;
import org.pinguin.gf.gui.util.AccountStringConverter;
import org.pinguin.gf.service.api.account.AccountTO;
import org.pinguin.gf.service.api.balance.BalanceTO;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import jfxtras.scene.control.CalendarTextField;

public class BalanceReport extends AnchorPane {

	@Inject
	private BalancePresenter presenter;
	@Inject
	private OpenAccStatementCommand openAccStatement;

	@FXML
	private CalendarTextField startDateText;
	@FXML
	private CalendarTextField endDateText;
	@FXML
	private TreeTableView<BalanceTO> balanceTree;
	@FXML
	private TreeTableColumn<BalanceTO, String> balanceTColumn;

	public BalanceReport() {
		loadFxml();
		balanceTree.setShowRoot(false);
	}

	@Inject
	public void init() {
		startDateText.calendarProperty().bindBidirectional(presenter.startDateProperty());
		endDateText.calendarProperty().bindBidirectional(presenter.endDateProperty());

		presenter.getBalanceList().addListener(new ListChangeListener<BalanceTO>() {

			@Override
			public void onChanged(Change<? extends BalanceTO> c) {
				ObservableList<? extends BalanceTO> list = c.getList();
				// Transformar e preencher
				TreeItem<BalanceTO> root = new TreeItem<BalanceTO>(null);
				transform(list, root);
				balanceTree.setRoot(root);
			}

			private void transform(ObservableList<? extends BalanceTO> list, TreeItem<BalanceTO> root) {
				// 1. Criar TreeItem e guardar no map, por ID da Conta
				Map<Long, TreeItem<BalanceTO>> map = new HashMap<>();
				for (BalanceTO item : list) {
					TreeItem<BalanceTO> treeItem = new TreeItem<BalanceTO>(item);
					map.put(item.getAccount().getAccountId(), treeItem);
				}
				// 2. Montar a hierarquia e guardar os roots
				for (TreeItem<BalanceTO> item : map.values()) {
					if (item.getValue().getAccount().getParent() != null) {
						@SuppressWarnings("unlikely-arg-type")
						TreeItem<BalanceTO> parent = map.get(item.getValue().getAccount().getParent().getId());
						parent.getChildren().add(item);
					} else {
						root.getChildren().add(item);
					}
				}
			}
		});

		final StringConverter<AccountTO> accStrConverter = new AccountStringConverter();
		balanceTColumn
				.setCellValueFactory(new SimpleTVCellValueFactory<BalanceTO, AccountTO>("account", accStrConverter));

		balanceTree.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.getClickCount() == 3) {
					BalanceTO selected = balanceTree.getSelectionModel().getSelectedItem().getValue();
					openAccStatement.apply(new OpenAccStatementParam(null, selected.getAccount(),
							presenter.startDateProperty().getValue(), presenter.endDateProperty().getValue()));
				}
			}
		});

		final ContextMenu contextMenu = new ContextMenu();
		final MenuItem copy = new MenuItem("Copiar para área de transferência.");
		copy.setOnAction(e -> {
			StringBuilder sb = new StringBuilder();
			appendItems(balanceTree.getRoot(), "", sb);
			final Clipboard clipboard = Clipboard.getSystemClipboard();
			final ClipboardContent content = new ClipboardContent();
			content.putString(sb.toString());
			clipboard.setContent(content);
		});
		contextMenu.getItems().add(copy);
		balanceTree.setContextMenu(contextMenu);
	}

	private void appendItems(final TreeItem<BalanceTO> parent, final String prefix, final StringBuilder sb) {
		for (final TreeItem<BalanceTO> item : parent.getChildren()) {
			sb.append(prefix + item.getValue().getAccount().getName()).append("\t");
			sb.append(format(item.getValue().getCredits())).append("\t");
			sb.append(format(item.getValue().getDebits())).append("\t");
			sb.append(format(item.getValue().getBalance())).append(System.lineSeparator());
			if (!item.getChildren().isEmpty()) {
				appendItems(item, prefix + "  ", sb);
			}
		}
	}

	private String format(final BigDecimal number) {
		Format format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
		return format.format(number);
	}

	private void loadFxml() {
		final FXMLLoader loader = new FXMLLoader(this.getClass().getResource("/META-INF/fxml/BalanceReport.xml"));
		loader.setRoot(this);
		loader.setController(this);
		try {
			loader.load();
		} catch (final IOException exception) {
			throw new IllegalStateException(exception);
		}
	}

	@FXML
	public void retrieve(ActionEvent evt) {
		presenter.retrieve();
	}

	public static class SimpleTVCellValueFactory<T, O>
			implements Callback<CellDataFeatures<T, String>, ObservableValue<String>> {

		private final String fieldName;
		private final StringConverter<O> strConverter;

		public SimpleTVCellValueFactory(String fieldName, StringConverter<O> strConverter) {
			this.fieldName = fieldName;
			this.strConverter = strConverter;
		}

		@Override
		public ObservableValue<String> call(CellDataFeatures<T, String> cellDataFeatures) {

			T bean = cellDataFeatures.getValue().getValue();

			if (bean == null) {
				return null;
			}

			try {
				Field field = bean.getClass().getDeclaredField(fieldName);
				field.setAccessible(true);
				@SuppressWarnings("unchecked")
				O fieldValue = (O) field.get(bean);
				return new ReadOnlyObjectWrapper<String>(strConverter.toString(fieldValue));
			} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
				return new ReadOnlyObjectWrapper<String>("Error");
			}
		}
	}

}
