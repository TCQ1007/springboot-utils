package io.github.tcq1007.springbootutils.querydsl;

import io.github.tcq1007.springbootutils.jpa.JpaBaseRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaQueryDslBaseRepository<E, K> extends JpaBaseRepository<E, K>, QuerydslPredicateExecutor<E> {
}
