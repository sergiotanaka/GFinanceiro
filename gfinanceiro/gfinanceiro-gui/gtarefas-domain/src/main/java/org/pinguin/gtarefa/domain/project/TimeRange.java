package org.pinguin.gtarefa.domain.project;

import java.time.LocalTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class TimeRange {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotNull
	private LocalTime start;
	@NotNull
	private LocalTime end;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public LocalTime getStart() {
		return start;
	}

	public void setStart(LocalTime start) {
		this.start = start;
	}

	public LocalTime getEnd() {
		return end;
	}

	public void setEnd(LocalTime end) {
		this.end = end;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TimeRange [id=");
		builder.append(id);
		builder.append(", start=");
		builder.append(start);
		builder.append(", end=");
		builder.append(end);
		builder.append("]");
		return builder.toString();
	}

}
