package org.pinguin.gf.gui.planning;

import java.math.BigDecimal;

import org.pinguin.gf.service.api.account.AccountTO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 */
public class AccountPlanningItem {

	private final ObjectProperty<Long> accPlanIdProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<AccountTO> accountProperty = new SimpleObjectProperty<>();
	private final ObjectProperty<BigDecimal> valueProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);
	private final ObjectProperty<BigDecimal> accomplishedProperty = new SimpleObjectProperty<>(BigDecimal.ZERO);

	public Property<Long> accPlanIdProperty() {
		return accPlanIdProperty;
	}

	public Property<AccountTO> accountProperty() {
		return accountProperty;
	}

	public Property<BigDecimal> valueProperty() {
		return valueProperty;
	}

	public ObjectProperty<BigDecimal> accomplishedProperty() {
		return accomplishedProperty;
	}

}
