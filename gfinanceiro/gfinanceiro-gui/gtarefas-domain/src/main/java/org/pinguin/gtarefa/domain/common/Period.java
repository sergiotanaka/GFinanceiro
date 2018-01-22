package org.pinguin.gtarefa.domain.common;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public interface Period {

	LocalDateTime getStart();

	LocalDateTime getEnd();

	default boolean isWithin(LocalDateTime dateTime) {
		return (dateTime.compareTo(getStart()) >= 0) && (dateTime.compareTo(getEnd()) <= 0);
	}

	default boolean isBefore(LocalDateTime dateTime) {
		return getStart().isBefore(dateTime);
	}

	default boolean isAfter(LocalDateTime dateTime) {
		return getEnd().isAfter(dateTime);
	}

	default boolean hasIntersection(Period another) {
		return !(this.getStart().isAfter(another.getEnd()) || this.getEnd().isBefore(another.getStart()));
	}

	default boolean hasIntersectionExc(Period another) {
		return !((this.getStart().compareTo(another.getEnd()) >= 0)
				|| (this.getEnd().compareTo(another.getStart()) <= 0));
	}

	default int intervalInMinutes() {
		return (int) getStart().until(getEnd(), ChronoUnit.MINUTES);
	}
}
