package org.pinguin.gtarefa.domain.project;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

@Entity
public class DayOfWeekSchedule {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotNull
	private int dayOfWeek;
	@NotNull
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<TimeRange> timeRanges = new HashSet<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getDayOfWeek() {
		return dayOfWeek;
	}

	public void setDayOfWeek(int dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public Set<TimeRange> getTimeRanges() {
		return timeRanges;
	}

	public void setTimeRanges(Set<TimeRange> timeRanges) {
		this.timeRanges = timeRanges;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DayOfWeekSchedule [id=");
		builder.append(id);
		builder.append(", dayOfWeek=");
		builder.append(dayOfWeek);
		builder.append(", timeRanges=");
		builder.append(timeRanges);
		builder.append("]");
		return builder.toString();
	}

}
