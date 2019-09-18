package com.bochkov.wicket.component.select2.data;

import com.google.common.collect.ImmutableList;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

/**
 * The interface Maskable.
 */
public interface Maskable {


    /**
     * String mask expression predicate.
     *
     * @param mask           the mask
     * @param maskedProperty the masked property
     * @param query          the query
     * @param cb             the cb
     * @return the predicate
     */
    static Predicate stringMaskExpression(String mask, Expression maskedProperty, CriteriaQuery<?> query, CriteriaBuilder cb) {
        mask = Optional.ofNullable(mask).orElse("%");
        return cb.like(cb.lower(maskedProperty.as(String.class)), Optional.of(mask).map(String::trim).map(String::toLowerCase).map(s -> "%" + s + "%").orElse(null));
    }

    /**
     * Mask specification specification.
     *
     * @param <T>               the type parameter
     * @param mask              the mask
     * @param maskedPopertyName the masked poperty name
     * @return the specification
     */
    static public <T> Specification<T> maskSpecification(final String mask, final String maskedPopertyName) {
        return (root, query, cb) -> {
            Predicate result;
            Path maskedProperty = fetchNestedPath(root, maskedPopertyName);
            result = stringMaskExpression(mask, maskedProperty, query, cb);
            if (result != null) {
                List<Order> orders;
                if (query.getOrderList() == null) {
                    orders = ImmutableList.of();
                } else {
                    orders = ImmutableList.copyOf(query.getOrderList());
                }
                Expression locate = cb.locate(maskedProperty, mask);
                orders = ImmutableList.<Order>builder().add(cb.asc(locate), cb.asc(cb.length(maskedProperty)), cb.asc(maskedProperty)).addAll(orders).build();
                query.orderBy(orders);
            }
            return result;
        };
    }

    /**
     * Fetch nested path path.
     *
     * @param <T>       the type parameter
     * @param root      the root
     * @param fieldname the fieldname
     * @return the path
     */
    public static <T> Path<T> fetchNestedPath(Path<T> root, String fieldname) {
        String[] fields = fieldname.split("\\.");
        Path<T> result = null;
        for (String field : fields) {
            if (result == null) {
                result = root.get(field);
            } else {
                result = result.get(field);
            }
        }
        return result;
    }

    /**
     * Mask specification specification.
     *
     * @param <T>             the type parameter
     * @param mask            the mask
     * @param maskedPoperties the masked poperties
     * @return the specification
     */
    static <T> Specification<T> maskSpecification(final String mask, final Iterable<String> maskedPoperties) {
        Specification<T> where = null;
        for (String p : maskedPoperties) {
            Specification<T> spec = maskSpecification(mask, p);
            if (where == null) {
                where = Specification.where(spec);
            } else {
                where = where.or(spec);
            }
        }
        return where;
    }
}
