package com.mqtt.Handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;

import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttFlowHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttFlowHandler.class);

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic.subscribe}")
    private String topicToSubscribe;

    private static final String MQTT_INPUT_CHANNEL = "mqttInputChannel";

    @Bean
    public IntegrationFlow mqttInboundFlow(MqttPahoClientFactory clientFactory) { // <-- Dùng Factory v3


        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_inbound_v3",
                clientFactory,
                topicToSubscribe
        );
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);

        return IntegrationFlow
                .from(adapter)
                .channel(MQTT_INPUT_CHANNEL)
                .get();
    }
    @Bean
    public MessageHandler mqttMessageHandler() {
        return (Message<?> message) -> {
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String payload = (String) message.getPayload();

            logger.info("--------------------");
            logger.info("Nhan duoc tin nhan! ");
            logger.info("Topic: {}", topic);
            logger.info("Payload: {}", payload);
        };
    }
    @Bean
    public IntegrationFlow messageHandlingFlow() {
        return IntegrationFlow.from(MQTT_INPUT_CHANNEL)
                .handle(mqttMessageHandler())
                .get();
    }
}