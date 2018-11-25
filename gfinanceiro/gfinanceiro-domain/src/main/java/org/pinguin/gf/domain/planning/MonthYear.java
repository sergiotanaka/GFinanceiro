package org.pinguin.gf.domain.planning;

import java.time.Month;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class MonthYear {
	@Enumerated(EnumType.STRING)
	private Month month;
	private int year;

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
