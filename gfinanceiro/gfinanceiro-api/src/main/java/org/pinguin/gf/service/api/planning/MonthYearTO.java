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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((month == null) ? 0 : month.hashCode());
		result = prime * result + year;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MonthYearTO other = (MonthYearTO) obj;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}

}
