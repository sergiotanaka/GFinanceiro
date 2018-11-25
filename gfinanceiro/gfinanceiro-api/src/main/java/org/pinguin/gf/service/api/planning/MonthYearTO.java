package org.pinguin.gf.service.api.planning;

import java.time.Month;

public class MonthYearTO {

	private Month month;
	private int year;

	public MonthYearTO() {
	}

	public MonthYearTO(Month month, int year) {
		this.month = month;
		this.year = year;
	}

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

	@Override
	public String toString() {
		return month.name() + "/" + year;
	}

}
