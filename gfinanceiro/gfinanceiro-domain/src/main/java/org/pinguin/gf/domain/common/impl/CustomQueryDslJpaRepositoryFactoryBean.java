package org.pinguin.gf.domain.common.impl;

import java.io.Serializable;

import javax.persistence.EntityManager;

import org.pinguin.gf.domain.common.api.CustomQueryDslJpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

public class CustomQueryDslJpaRepositoryFactoryBean<R extends JpaRepository<T, I>, T, I extends Serializable>
		extends JpaRepositoryFactoryBean<R, T, I> {

	public CustomQueryDslJpaRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	@Override
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		return new CustomQueryDslJpaRepositoryFactory<T, I>(entityManager);
	}

	private static class CustomQueryDslJpaRepositoryFactory<T, I extends Serializable> extends JpaRepositoryFactory {

		private EntityManager entityManager;

		public CustomQueryDslJpaRepositoryFactory(EntityManager entityManager) {
			super(entityManager);
			this.entityManager = entityManager;
		}

		@SuppressWarnings("unused")
		protected Object getTargetRepository(RepositoryMetadata metadata) {
			return new CustomQueryDslJpaRepositoryImpl<>(getEntityInformation(metadata.getDomainType()), entityManager);
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			return CustomQueryDslJpaRepository.class;
		}
	}
}