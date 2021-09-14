package org.pinguin.pomodoro.domain.taskstatetransition;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class TaskStateTransitionRepository {

	@Inject
	private EntityManager em;

	public void add(final TaskStateTransition taskStateTransition) {
		em.persist(taskStateTransition);
	}

	public List<TaskStateTransition> retrieveByTaskId(final Long taskId) {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<TaskStateTransition> cq = cb.createQuery(TaskStateTransition.class);
		final Root<TaskStateTransition> taskTransition = cq.from(TaskStateTransition.class);
		cq.select(taskTransition).where(cb.equal(taskTransition.get("taskId"), taskId))
				.orderBy(cb.asc(taskTransition.get("timeStamp")));
		return em.createQuery(cq).getResultList();
	}

}
