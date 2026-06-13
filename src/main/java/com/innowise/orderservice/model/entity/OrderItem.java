package com.innowise.orderservice.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "order_items")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", updatable = false, nullable = false)
  private Long id;

  @JoinColumn(name = "order_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Order order;

  @JoinColumn(name = "item_id", nullable = false)
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Item item;

  @Column(name = "quantity", nullable = false)
  private Integer quantity;
}
