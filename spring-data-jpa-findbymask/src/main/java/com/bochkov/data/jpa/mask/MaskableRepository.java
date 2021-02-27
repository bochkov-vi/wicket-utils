package com.bochkov.data.jpa.mask;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import static com.bochkov.data.jpa.mask.Maskable.maskSpecification;

public interface MaskableRepository<T> extends  JpaSpecificationExecutor<T> {

    default Page<T> findByMask(String mask, Pageable pageable, String... properties) {
        return findAll(maskSpecification(mask, Lists.newArrayList(properties)), pageable);
    }

    default Page<T> findByMask(String mask, Pageable pageable, Iterable<String> properties) {
        return findAll(maskSpecification(mask, properties), pageable);
    }
}
