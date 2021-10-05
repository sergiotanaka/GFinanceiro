package org.pinguin.gf.gui.accstatement;

import java.util.Calendar;

import org.pinguin.gf.service.api.account.AccountTO;

import javafx.stage.Window;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OpenAccStatementParam {
	private Window owner;
	private AccountTO account;
	private Calendar startDate;
	private Calendar endDate;
	private String tagFilter;

	public OpenAccStatementParam(Window owner) {
		this.owner = owner;
	}

}
