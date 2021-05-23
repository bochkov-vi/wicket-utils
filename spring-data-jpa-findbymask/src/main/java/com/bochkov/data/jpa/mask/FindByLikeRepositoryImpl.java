package com.bochkov.data.jpa.mask;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;

import static com.bochkov.data.jpa.mask.Maskable.maskSpecification;

public class FindByLikeRepositoryImpl<T, ID> extends SimpleJpaRepository<T, ID> implements FindByLikeRepository<T> {

    public FindByLikeRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    public FindByLikeRepositoryImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
    }

    @Override
    public Page<T> findByLike(String mask, Pageable pageable, String... properties) {
        return findAll(maskSpecification(mask, Lists.newArrayList(properties)), pageable);
    }

    @Override
    public Page<T> findByLike(String mask, Pageable pageable, Iterable<String> properties) {
        return findAll(maskSpecification(mask, properties), pageable);
    }
}
