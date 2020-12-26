package com.bochkov.wicket.component.select2.data;

import com.google.common.collect.ImmutableList;
import org.danekja.java.util.function.serializable.SerializableBiFunction;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * The interface Maskable.
 */
public interface Maskable {


    /**
     * Mask specification specification.
     *
     * @param <T>               the type parameter
     * @param mask              the mask
     * @param maskedPopertyName the masked poperty name
     * @return the specification
     */

    static SerializableBiFunction<Root, String, Path> DEFAULT_PROPERTY_EXTRACTOR = (root, maskedPopertyName) -> {
        Path maskedProperty = fetchNestedPath(root, maskedPopertyName);

        return maskedProperty;
    };


    static Predicate stringMaskExpression(String mask, Expression maskedProperty, CriteriaBuilder cb) {
        mask = Optional.ofNullable(mask).orElse("%");
        return cb.like(cb.lower(maskedProperty.as(String.class)), Optional.of(mask).map(String::trim).map(String::toLowerCase).map(s -> "%" + s + "%").orElse(null));
    }

    static public <T> Specification<T> maskSpecification(final String mask, final String maskedPopertyName, final SerializableBiFunction<Root, String, Path> propertyExtractor) {
        return (root, query, cb) -> {
            Predicate result;
            Path maskedProperty = propertyExtractor.apply(root, maskedPopertyName);
            result = stringMaskExpression(mask, maskedProperty, cb);
            if (result != null) {
                List<Order> orders;
                if (query.getOrderList() == null) {
                    orders = ImmutableList.of();
                } else {
                    orders = ImmutableList.copyOf(query.getOrderList());
                }
                Expression locate = cb.locate(maskedProperty.as(String.class), Optional.ofNullable(mask).orElse(""));
                orders = ImmutableList.<Order>builder().add(cb.asc(locate), cb.asc(cb.length(maskedProperty.as(String.class))), cb.asc(maskedProperty)).addAll(orders).build();
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
                result = toPath(root, field);
            } else {
                result = toPath(result, field);
            }
        }
        return result;
    }

    static Path toPath(Path root, String fieldName) {
        if (root instanceof From && Collection.class.isAssignableFrom(root.get(fieldName).getJavaType())) {
            return ((From) root).join(fieldName, JoinType.LEFT);
        } else {
            return root.get(fieldName);
        }
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
        return maskSpecification(mask, maskedPoperties, DEFAULT_PROPERTY_EXTRACTOR);
    }

    static <T> Specification<T> maskSpecification(final String mask, final Iterable<String> maskedPoperties, final SerializableBiFunction<Root, String, Path> propertyExtractor) {
        Specification<T> where = null;
        for (String p : maskedPoperties) {
            Specification<T> spec = maskSpecification(mask, p, propertyExtractor);
            if (where == null) {
                where = Specification.where(spec);
            } else {
                where = where.or(spec);
            }
        }
        return where;
    }
}
