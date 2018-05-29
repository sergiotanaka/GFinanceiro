package org.pinguin.gf.domain.common.impl;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;

import org.pinguin.gf.domain.common.api.CustomQueryDslJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.Querydsl;
import org.springframework.data.jpa.repository.support.QuerydslJpaRepository;
import org.springframework.data.querydsl.EntityPathResolver;
import org.springframework.data.querydsl.SimpleEntityPathResolver;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;

public class CustomQueryDslJpaRepositoryImpl<T, ID extends Serializable> extends QuerydslJpaRepository<T, ID>
		implements CustomQueryDslJpaRepository<T, ID> {

	// All instance variables are available in super, but they are private
	private static final EntityPathResolver DEFAULT_ENTITY_PATH_RESOLVER = SimpleEntityPathResolver.INSTANCE;

	private final EntityPath<T> path;
	private final PathBuilder<T> builder;
	private final Querydsl querydsl;

	public CustomQueryDslJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
		this(entityInformation, entityManager, DEFAULT_ENTITY_PATH_RESOLVER);
	}

	public CustomQueryDslJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager,
			EntityPathResolver resolver) {
		super(entityInformation, entityManager);
		this.path = resolver.createPath(entityInformation.getJavaType());
		this.builder = new PathBuilder<T>(path.getType(), path.getMetadata());
		this.querydsl = new Querydsl(entityManager, builder);
	}

	@Override
	public Page<T> findAll(FactoryExpression<T> factoryExpression, Predicate predicate, Pageable pageable) {

		final JPQLQuery<?> countQuery = createCountQuery(predicate);
		final JPQLQuery<T> query = querydsl.applyPagination(pageable, createQuery(predicate).select(factoryExpression));

		final long total = countQuery.fetchCount();
		final List<T> content = pageable == null || total > pageable.getOffset() ? query.fetch()
				: Collections.<T>emptyList();

		return new PageImpl<T>(content, pageable, total);
	}
}