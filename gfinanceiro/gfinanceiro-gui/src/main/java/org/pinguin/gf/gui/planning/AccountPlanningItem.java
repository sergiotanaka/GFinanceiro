package org.pinguin.gf.gui.planning;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.pinguin.gf.service.api.account.AccountTO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 * Objeto que representa uma conta planejada
 */
public class AccountPlanningItem {

	private final ObjectProperty<Long> accPlanIdProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<String> commentProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<BigDecimal> valueProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	private final ObjectProperty<BigDecimal> accomplishedProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	private final ObjectProperty<BigDecimal> balanceProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	private final ObjectProperty<Double> percentProperty = new SimpleObjectProperty<>(0d);

	public AccountPlanningItem() {
		final ChangeListener<? super BigDecimal> listener = (r, o, n) -> {
			if (!valueProperty.get().setScale(2).equals(BigDecimal.ZERO.setScale(2))) {
				percentProperty.set(
						accomplishedProperty.get().divide(valueProperty.get(), 2, RoundingMode.HALF_UP).doubleValue());
			}
			if (accomplishedProperty.get().doubleValue() < 0.0) {
				percentProperty.set(Double.valueOf(0.0));
			}
			balanceProperty.set(valueProperty.get().subtract(accomplishedProperty.get()));
		};
		valueProperty.addListener(listener);
		accomplishedProperty.addListener(listener);
	}

	public Property<Long> accPlanIdProperty() {
		return accPlanIdProperty;
	}

	public Property<AccountTO> accountProperty() {
		return accountProperty;
	}

	public Property<BigDecimal> valueProperty() {
		return valueProperty;
	}

	public Property<String> commentProperty() {
		return commentProperty;
	}

	public Property<BigDecimal> accomplishedProperty() {
		return accomplishedProperty;
	}

	public Property<BigDecimal> balanceProperty() {
		return balanceProperty;
	}

	public Property<Double> percentProperty() {
		return percentProperty;
	}

	@Override
	public String toString() {
		return "AccountPlanningItem [accPlanIdProperty=" + accPlanIdProperty.getValue() + ", accountProperty="
				+ accountProperty.getValue() + ", commentProperty=" + commentProperty.getValue() + ", valueProperty="
				+ valueProperty.getValue() + ", accomplishedProperty=" + accomplishedProperty.getValue()
				+ ", balanceProperty=" + balanceProperty.getValue() + ", percentProperty=" + percentProperty.getValue()
				+ "]";
	}

}
