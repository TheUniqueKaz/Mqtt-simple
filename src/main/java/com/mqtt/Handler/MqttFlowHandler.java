package com.mqtt.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqtt.Entity.TelemetryData;
import com.mqtt.Repository.TelemetryRepository;
import com.mqtt.mapper.TelemetryMapper;
import com.mqtt.Payload.BasePayload;
import com.mqtt.Payload.MessagePayload;
import com.mqtt.Payload.SensorPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttFlowHandler {

    private static final Logger logger = LoggerFactory.getLogger(MqttFlowHandler.class);
    private static final String MQTT_INPUT_CHANNEL = "mqttInputChannel";

    @Value("${mqtt.client.id}")
    private String clientId;

    @Value("${mqtt.topic.subscribe}")
    private String topicToSubscribe;

    @Autowired
    private TelemetryRepository telemetryRepository;

    @Autowired
    private TelemetryMapper telemetryMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Bean
    public IntegrationFlow mqttInboundFlow(MqttPahoClientFactory clientFactory) {

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_listener",
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
            String rawJson = (String) message.getPayload();

            logger.info(">>> Nhận tin nhắn từ Topic: {}", topic);

            try {

                BasePayload payloadObj = objectMapper.readValue(rawJson, BasePayload.class);


                TelemetryData dbEntry = telemetryMapper.toEntity(topic, payloadObj);


                telemetryRepository.save(dbEntry);
                logger.info("✔ Đã lưu DB thành công (ID thiết bị: {})", payloadObj.getDeviceId());


                processBusinessLogic(payloadObj);

            } catch (Exception e) {
                logger.error("Lỗi xử lý Message: {}", e.getMessage());
                // e.printStackTrace();
            }
        };
    }


    @Bean
    public IntegrationFlow messageHandlingFlow() {
        return IntegrationFlow.from(MQTT_INPUT_CHANNEL)
                .handle(mqttMessageHandler())
                .get();
    }


    private void processBusinessLogic(BasePayload payload) {

        if (payload instanceof SensorPayload) {
            SensorPayload p = (SensorPayload) payload;
            if ("temperature".equalsIgnoreCase(p.getName())) {
                logger.info("🌡 Nhiệt độ đo được: {} {}", p.getValue(), p.getUnit());


                if (p.getValue() > 50) logger.warn("CẢNH BÁO: NHIỆT ĐỘ QUÁ CAO!");
            } else {
                logger.info("💧 Cảm biến {}: {}", p.getName(), p.getValue());
            }
        }

        else if (payload instanceof MessagePayload) {
            MessagePayload p = (MessagePayload) payload;

            switch (p.getCategory()) {
                case "QR_CODE":
                    logger.info("📷 Quét mã vạch: {}", p.getContent());
                    break;
                case "KEYBOARD":
                    logger.info("⌨ Người dùng nhập: {}", p.getContent());
                    break;
                case "SYSTEM_LOG":
                    if ("ERROR".equals(p.getLevel())) {
                        logger.error("🚨 LỖI HỆ THỐNG: {}", p.getContent());
                    } else {
                        logger.info("ℹ Log hệ thống: {}", p.getContent());
                    }
                    break;
                default:
                    logger.info("📩 Tin nhắn khác: {}", p.getContent());
            }
        }
    }
}