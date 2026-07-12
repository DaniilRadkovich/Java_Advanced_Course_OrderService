package com.innowise.orderservice.messaging;

import com.innowise.orderservice.model.entity.OrderStatus;
import com.innowise.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentKafkaConsumer {

  private final OrderService orderService;

  @KafkaListener(topics = "payment-events", groupId = "order-group")
  public void consumePaymentFromKafka(PaymentEvent paymentEvent) {
    log.info("****KAFKA**** Payment event received: {} ****KAFKA****", paymentEvent);

    if (paymentEvent.getStatus() == PaymentStatus.SUCCESS) {
      orderService.updateOrderStatusFromKafka(paymentEvent.getOrderId(), OrderStatus.PAID);
    } else if (paymentEvent.getStatus() == PaymentStatus.FAILED) {
      orderService.updateOrderStatusFromKafka(paymentEvent.getOrderId(), OrderStatus.CANCELLED);
    } else {
      log.error(
          "****KAFKA**** Failed to process payment event via Kafka in OrderService: {} ****KAFKA****",
          paymentEvent);
      throw new IllegalArgumentException("Unknown payment status: " + paymentEvent.getStatus());
    }
  }
}
