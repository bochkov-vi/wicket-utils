package com.bochkov.data.jpa.mask;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindByLikeRepository<T> {
    Page<T> findByLike(String mask, Pageable pageable, String... properties);

    Page<T> findByLike(String mask, Pageable pageable, Iterable<String> properties);
}
