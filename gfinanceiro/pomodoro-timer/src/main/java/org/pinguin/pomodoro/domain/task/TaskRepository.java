package org.pinguin.pomodoro.domain.task;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public class TaskRepository {

	@Inject
	private EntityManager em;

	public void updIndex() {
		getAllTasks().forEach(t -> t.setIndex(t.getId()));
	}

	public List<Task> getAllTasks() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> from = cq.from(Task.class);
		cq.select(from).orderBy(cb.asc(from.get("index")));
		return em.createQuery(cq).getResultList();
	}

	public List<Task> getAllUndone() {
		final CriteriaBuilder cb = em.getCriteriaBuilder();
		final CriteriaQuery<Task> cq = cb.createQuery(Task.class);
		Root<Task> t = cq.from(Task.class);
		cq.select(t).where(cb.equal(t.get("done"), false)).orderBy(cb.asc(t.get("index")));
		return em.createQuery(cq).getResultList();
	}

	public long getNextIndex() {
		final Query qry = em.createQuery("select max(t.index) from Task t");
		final Object result = qry.getSingleResult();
		if (result == null) {
			return 0;
		} else {
			return ((Long) qry.getSingleResult()) + 1L;
		}
	}

}
