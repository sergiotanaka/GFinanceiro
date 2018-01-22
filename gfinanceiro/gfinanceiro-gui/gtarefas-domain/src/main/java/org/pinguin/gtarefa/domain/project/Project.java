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
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotNull
	private String name;
	private String description;
	@NotNull
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private Set<DayOfWeekSchedule> schedules = new HashSet<>();

	public Project() {
		super();
	}

	public Project(Long id, String name, String description) {
		this();
		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<DayOfWeekSchedule> getSchedules() {
		return schedules;
	}

	public void setSchedules(Set<DayOfWeekSchedule> schedules) {
		this.schedules = schedules;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Project [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", description=");
		builder.append(description);
		builder.append(", schedules=");
		builder.append(schedules);
		builder.append("]");
		return builder.toString();
	}
}
