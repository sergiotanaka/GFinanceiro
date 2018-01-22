package org.pinguin.gf.facade.planning;

public class MonthYearTO {

	private MonthTO month;
	private int year;

	public MonthYearTO() {
		super();
	}

	public MonthYearTO(MonthTO month, int year) {
		super();
		this.month = month;
		this.year = year;
	}

	public MonthTO getMonth() {
		return month;
	}

	public void setMonth(MonthTO month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}
}
