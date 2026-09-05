package io.github.tcq1007.springbootutils.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JpaBaseRepository<E, K> extends JpaRepository<E, K>, JpaSpecificationExecutor<E> {
}
