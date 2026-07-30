package com.innowise.orderservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.orderservice.messaging.OrderEvent;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

  @Value("${kafka.bootstrap-servers:localhost:9092}")
  private String bootstrapServers;

  @Bean
  public ProducerFactory<String, OrderEvent> producerFactory(ObjectMapper objectMapper) {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

    JsonSerializer<OrderEvent> serializer = new JsonSerializer<>(objectMapper);
    serializer.setAddTypeInfo(false);

    return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(), serializer);
  }

  @Bean
  public KafkaTemplate<String, OrderEvent> kafkaTemplate(
      ProducerFactory<String, OrderEvent> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}