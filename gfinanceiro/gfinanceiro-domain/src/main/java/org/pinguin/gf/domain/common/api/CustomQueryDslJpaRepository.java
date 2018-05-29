package org.pinguin.gf.domain.common.api;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Predicate;

@NoRepositoryBean
public interface CustomQueryDslJpaRepository<T, ID extends Serializable>
		extends JpaRepository<T, ID>, QuerydslPredicateExecutor<T> {
	/**
	 * Returns a {@link org.springframework.data.domain.Page} of entities matching
	 * the given {@link com.mysema.query.types.Predicate}. This also uses provided
	 * projections ( can be JavaBean or constructor or anything supported by
	 * QueryDSL
	 * 
	 * @param constructorExpression
	 *            this constructor expression will be used for transforming query
	 *            results
	 * @param predicate
	 * @param pageable
	 * @return
	 */
	Page<T> findAll(FactoryExpression<T> factoryExpression, Predicate predicate, Pageable pageable);
}