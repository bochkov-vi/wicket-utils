package com.bochkov;

import com.google.common.collect.Lists;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PageUtil {

    public static <T> Page<T> concat(int pageNumber, Page<T>... pages) {
        List<T> content = Stream.of(pages).map(Page::stream).reduce(Stream::concat).orElseGet(Stream::empty).collect(Collectors.toList());
        Pageable p = Stream.of(pages).map(Page::getPageable).reduce((p1, p2) -> PageRequest.of(pageNumber, p1.getPageSize() + p2.getPageSize())).get();
        long total = Stream.of(pages).mapToLong(Page::getTotalElements).sum();
        return new PageImpl<T>(content, p, total);
    }

    public static <T> Page<T> concat(int pageNumber, T entity, Page<T>... page) {
        return PageUtil.concat(pageNumber, PageUtil.of(pageNumber, entity), PageUtil.concat(pageNumber, page));
    }

    public static <T> Page<T> of(int pageNumber, T... entity) {
        return new PageImpl<>(Lists.newArrayList(entity), PageRequest.of(pageNumber, 1), entity.length);
    }
}
