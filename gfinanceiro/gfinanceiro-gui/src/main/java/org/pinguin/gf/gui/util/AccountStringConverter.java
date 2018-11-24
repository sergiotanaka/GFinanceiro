package org.pinguin.gf.gui.util;

import org.pinguin.gf.service.api.account.AccountNatureTO;
import org.pinguin.gf.service.api.account.AccountTO;

import javafx.util.StringConverter;

/**
 * Conversor de {@link Account} para {@link String}.
 */
public class AccountStringConverter extends StringConverter<AccountTO> {

	@Override
	public String toString(AccountTO acc) {
		if (acc == null) {
			return null;
		}
		return acc.getName();
	}

	@Override
	public AccountTO fromString(String str) {
		return new AccountTO(str, AccountNatureTO.CREDIT);
	}

	static public AccountStringConverter instance() {
		return new AccountStringConverter();
	}
}
