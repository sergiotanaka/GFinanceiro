package org.pinguin.core.domain;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.google.inject.persist.Transactional;

@Transactional(rollbackOn = { Exception.class, RuntimeException.class })
public abstract class RepositoryBase<T, I> {

	@Inject
	private EntityManager entityManager;

	protected EntityManager getEntityManager() {
		return entityManager;
	}

	public T create(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	public T update(T entity) {
		return entityManager.merge(entity);
	}

	public T delete(T entity) {
		final T merged = entityManager.merge(entity);
		entityManager.remove(merged);
		return merged;
	}

	public T retrieveById(I id) {
		return entityManager.find(getEntityType(), id);
	}

	public List<T> retrieveAll() {
		final TypedQuery<T> query = entityManager
				.createQuery(String.format("select e from %s e", getEntityType().getName()), getEntityType());
		return query.getResultList();
	}

	public List<T> retrieveByQuery(String jpql, Parameter<?>... params) {
		final TypedQuery<T> query = entityManager.createQuery(jpql, getEntityType());
		for (Parameter<?> param : params) {
			query.setParameter(param.getName(), param.getValue());
		}

		return query.getResultList();
	}

	protected abstract Class<T> getEntityType();

}
