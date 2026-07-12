package com.innowise.orderservice.messaging;

import com.innowise.orderservice.model.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderKafkaEventListener {

  private final OrderKafkaProducer orderKafkaProducer;

  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void handle(Order order) {

    log.info(
        "****KAFKA**** Transaction committed. Publishing CREATE_ORDER event for order {} ****KAFKA****",
        order.getId()
    );

    orderKafkaProducer.sendCreateOrderEvent(order);
  }
}