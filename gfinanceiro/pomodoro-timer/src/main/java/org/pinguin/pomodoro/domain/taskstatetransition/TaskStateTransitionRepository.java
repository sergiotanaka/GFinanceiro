package org.pinguin.pomodoro.domain.taskstatetransition;

import javax.inject.Inject;
import javax.persistence.EntityManager;

public class TaskStateTransitionRepository {

	@Inject
	private EntityManager em;

	public void add(final TaskStateTransition taskStateTransition) {
		em.persist(taskStateTransition);
	}

}
