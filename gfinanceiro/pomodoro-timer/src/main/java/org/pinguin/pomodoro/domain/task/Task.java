package org.pinguin.pomodoro.domain.task;

public class Task {

	private Long id;
	private String name;
	private boolean done = false;

	public Task(String name, boolean done) {
		this.name = name;
		this.done = done;
	}

	public Task(String name) {
		this(name, false);
	}

	public Task() {
		this(null, false);
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

	public boolean isDone() {
		return done;
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Task [id=");
		builder.append(id);
		builder.append(", name=");
		builder.append(name);
		builder.append(", done=");
		builder.append(done);
		builder.append("]");
		return builder.toString();
	}

}
