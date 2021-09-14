package org.pinguin.gf.service.api.planning;

import java.time.Month;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthYearTO {
	private Month month;
	private int year;
}
