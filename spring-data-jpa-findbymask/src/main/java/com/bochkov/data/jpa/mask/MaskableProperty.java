package com.bochkov.data.jpa.mask;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.mapping.PropertyPath;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.bochkov.data.jpa.mask.QueryUtils.toExpression;

@Data
@Accessors(chain = true)
public class MaskableProperty<T> {


    String property;

    public MaskableProperty(String property) {
        this.property = property;
    }

    public static <T> MaskableProperty<T> of(String name) {
        return new MaskableProperty<>(name);
    }

    static public <T> Specification<T> maskSpecification(final String mask, final String... maskedPoperties) {
        return maskSpecification(mask, Lists.newArrayList(maskedPoperties));
    }

    static public <T> Specification<T> maskSpecification(final String mask, final Collection<String> maskedPoperties) {
        Specification<T> result = null;
        for (Specification s : maskedPoperties.stream().map(MaskableProperty::of).map(sp -> sp.specification(mask)).collect(Collectors.toList())) {
            if (result == null) {
                result = s;
            } else {
                result = result.or(s);
            }
        }
        return result;
    }

    public Specification<T> specification(String like) {
        return new Specification<T>() {
            @Override
            public Predicate toPredicate(Root r, CriteriaQuery q, CriteriaBuilder b) {
                PropertyPath propertyPath = PropertyPath.from(property, r.getJavaType());
                Predicate predicate = null;
                if (!propertyPath.getLeafProperty().isCollection()) {
                    Expression property = toExpression(propertyPath, r, b);
                    appendOrders(q, b, property, like);
                    predicate = like(property.as(String.class), like, b);
                } else {
                    Subquery<?> sq = q.subquery(r.getJavaType());
                    Root sr = sq.from(r.getJavaType());
                    sq.select(sr.get("id"));
                    sq.where(b.and(like(toExpression(propertyPath, sr, b), like, b)), b.equal(sr.get("id"), r.get("id")));
                    predicate = b.exists(sq);
                    //appendOrders(q, b.desc(predicate));
                }
                return predicate;
            }
        };
    }

    public Predicate like(Expression<String> expression, String like, CriteriaBuilder cb) {
        return cb.like(expression, like(like));
    }

    public String like(String term) {
        return Optional.ofNullable(term).map(str -> String.format("%%%s%%", term)).orElse("%");
    }

    public void appendOrders(CriteriaQuery<?> query, Order... append) {
        appendOrders(query, Lists.newArrayList(append));
    }

    public void appendOrders(CriteriaQuery<?> query, List<Order> append) {
        if (append != null && !append.isEmpty()) {
            List<Order> orders = query.getOrderList();
            if (orders == null) {
                orders = ImmutableList.of();
            }
            List<Order> concated = Streams.concat(orders.stream(), append.stream()).collect(Collectors.toList());
            query.orderBy(concated);
        }
    }

    public void appendOrders(CriteriaQuery<?> query, CriteriaBuilder cb, Expression<?> property, String term) {
        if (!Strings.isNullOrEmpty(term)) {
            Expression<?> locate = cb.locate(property.as(String.class), Optional.ofNullable(term).orElse(""));
            Expression<?> length = cb.length(property.as(String.class));

            List<Expression<?>> expressionToOrder = Stream.of(locate, length, property).collect(Collectors.toList());
            List<Order> orders = expressionToOrder.stream().map(cb::asc).collect(Collectors.toList());
            appendOrders(query, orders);
        }
    }


}
