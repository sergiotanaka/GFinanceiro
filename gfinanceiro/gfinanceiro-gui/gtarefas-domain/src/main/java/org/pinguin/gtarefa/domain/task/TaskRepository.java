package org.pinguin.gtarefa.domain.task;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.pinguin.core.domain.RepositoryBase;

public class TaskRepository extends RepositoryBase<Task, Long> {

	public long nextIndex() {
		EntityManager em = getEntityManager();
		TypedQuery<Long> query = em.createQuery("select max(t.index) from Task t where t.status <> 'DONE'", Long.class);
		Long maxIndex = query.getSingleResult();
		return maxIndex == null ? 0L : maxIndex + 1L;
	}

	@Override
	protected Class<Task> getEntityType() {
		return Task.class;
	}

}
