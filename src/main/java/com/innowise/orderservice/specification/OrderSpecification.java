package com.innowise.orderservice.specification;

import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.model.entity.OrderStatus;
import java.time.Instant;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderSpecification {

  public static Specification<Order> hasStatuses(List<OrderStatus> statuses) {
    return (root, query, criteriaBuilder) -> {
      if (statuses == null || statuses.isEmpty()) {
        return criteriaBuilder.conjunction();
      }
      return root.get("status").in(statuses);
    };
  }

  public static Specification<Order> createdFromTo(Instant from, Instant to) {
    return (root, query, criteriaBuilder) -> {
      if (from != null && to != null) {
        return criteriaBuilder.between(root.get("createdAt"), from, to);
      }
      if (from != null) {
        return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
      }
      if (to != null) {
        return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
      }
      return criteriaBuilder.conjunction();
    };
  }
}
