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
    log.info("Payment event received: {}", paymentEvent);

    try {
      if (paymentEvent.getStatus().equals(PaymentStatus.SUCCESS)) {
        orderService.updateOrderStatusFromKafka(paymentEvent.getOrderId(), OrderStatus.PAID);
      } else if (paymentEvent.getStatus().equals(PaymentStatus.FAILED)) {
        orderService.updateOrderStatusFromKafka(paymentEvent.getOrderId(), OrderStatus.CANCELLED);
      } else {
        throw new IllegalArgumentException("Invalid payment status: " + paymentEvent.getStatus());
      }
    } catch (Exception e) {
      log.error("Failed to process payment event via Kafka in OrderService: {}", paymentEvent, e);
    }
  }
}
