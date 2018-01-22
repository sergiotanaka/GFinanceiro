package org.pinguin.gtarefa.domain.task;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.pinguin.gtarefa.domain.project.Project;

@Entity
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	private Long id;
	@NotNull
	private String name;
	/** Utilizado para priorizacao. */
	private long index;
	@Lob
	private String description;
	private int duration;
	@NotNull
	private LocalDateTime dueDate;
	@NotNull
	@Enumerated(EnumType.STRING)
	private Category category;
	@NotNull
	@Enumerated(EnumType.STRING)
	private Status status;
	@OneToOne(fetch = FetchType.EAGER)
	private Project project;
	private boolean displayOnBoard;
	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<Task> subTasks = new HashSet<>();

	public Task() {
		super();
	}

	public Task(Long id) {
		this.id = id;
	}
	
	public Task(String name) {
		this.name = name;
	}

	public Task(Long id, String name, long index, int duration, LocalDateTime dueDate, Project project) {
		this();
		this.id = id;
		this.name = name;
		this.index = index;
		this.duration = duration;
		this.dueDate = dueDate;
		this.category = Category.CIRCUMSTANTIAL;
		this.status = Status.TODO;
		this.project = project;
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

	public long getIndex() {
		return index;
	}

	public void setIndex(long index) {
		this.index = index;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}

	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public boolean getDisplayOnBoard() {
		return displayOnBoard;
	}

	public void setDisplayOnBoard(boolean displayOnBoard) {
		this.displayOnBoard = displayOnBoard;
	}

	public Set<Task> getSubTasks() {
		return subTasks;
	}

	public void setSubTasks(Set<Task> subTasks) {
		this.subTasks = subTasks;
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", name=" + name + ", index=" + index + ", description=" + description + ", duration="
				+ duration + ", dueDate=" + dueDate + ", category=" + category + ", status=" + status + ", project="
				+ project + ", displayOnBoard=" + displayOnBoard + "]";
	}

	public Task copy() {
		Task copy = new Task();
		copy.setId(getId());
		copy.setName(getName());
		copy.setIndex(getIndex());
		copy.setDuration(getDuration());
		copy.setDueDate(getDueDate());
		copy.setCategory(getCategory());
		copy.setStatus(getStatus());
		copy.setProject(getProject());
		return copy;
	}

}
