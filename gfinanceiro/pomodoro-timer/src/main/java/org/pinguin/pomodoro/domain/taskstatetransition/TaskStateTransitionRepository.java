package org.pinguin.pomodoro.domain.taskstatetransition;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.google.inject.persist.Transactional;

public class TaskStateTransitionRepository {
	@Inject
	private EntityManager em;

	@Transactional
	public void add(final TaskStateTransition taskStateTransition) {
		em.persist(taskStateTransition);
	}

}
