package com.innowise.orderservice.messaging;

import com.innowise.orderservice.model.entity.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderKafkaProducer {

  private static final String ORDER_TOPIC = "order-events";

  private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

  public void sendCreateOrderEvent(Order order) {
    OrderEvent event = OrderEvent.builder()
        .orderId(order.getId())
        .userId(order.getUserId())
        .totalPrice(order.getTotalPrice())
        .build();

    kafkaTemplate.send(ORDER_TOPIC, String.valueOf(order.getId()), event)
        .whenComplete((result, ex) -> {
          if (ex != null) {
            log.error("****KAFKA**** Failed to publish CREATE_ORDER event ****KAFKA****", ex);
            return;
          }

          log.info(
              "****KAFKA**** CREATE_ORDER event successfully published. Topic={}, Partition={}, Offset={} ****KAFKA****",
              result.getRecordMetadata().topic(),
              result.getRecordMetadata().partition(),
              result.getRecordMetadata().offset()
          );
        });
  }
}