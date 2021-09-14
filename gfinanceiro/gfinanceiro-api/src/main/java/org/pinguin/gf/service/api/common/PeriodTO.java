package org.pinguin.gf.service.api.common;

import java.util.Calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PeriodTO {
	private Calendar start;
	private Calendar end;
}
