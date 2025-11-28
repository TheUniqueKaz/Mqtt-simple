package com.mqtt.Handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqtt.Entity.TelemetryData;
import com.mqtt.Repository.TelemetryRepository;
import com.mqtt.Payload.BasePayload;
import com.mqtt.Payload.MessagePayload;
import com.mqtt.Payload.SensorPayload;
import com.mqtt.mapper.TelemetryMapper;
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

import java.util.Map;

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
    private ObjectMapper objectMapper;

    @Autowired
    private TelemetryMapper telemetryMapper;


    @Bean
    public IntegrationFlow mqttInboundFlow(MqttPahoClientFactory clientFactory) {
        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_listener",
                clientFactory,
                topicToSubscribe
        );
        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);

        return IntegrationFlow.from(adapter).channel(MQTT_INPUT_CHANNEL).get();
    }


    @Bean
    public MessageHandler mqttMessageHandler() {
        return (Message<?> message) -> {
            // 1. Lấy thông tin Header và Payload
            String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
            String rawJson = (String) message.getPayload();

            try {
                // 2. Phân tích Topic: u/{deviceId}/{type}
                String[] parts = topic.split("/");

                if (parts.length < 3) {
                    logger.warn("Topic sai định dạng: {}", topic);
                    return;
                }

                String deviceId = parts[1];
                String typeCode = parts[2]; // 's', 'm', hoặc 'control'

                BasePayload payloadObj = null;
                String fullType = "UNKNOWN";

                // 3. Phân loại và Parse JSON
                if ("s".equals(typeCode)) {
                    // Mapping JSON {"n":"t", "v":30} vào SensorPayload
                    payloadObj = objectMapper.readValue(rawJson, SensorPayload.class);
                    fullType = "sensor";
                }
                else if ("m".equals(typeCode)) {
                    payloadObj = objectMapper.readValue(rawJson, MessagePayload.class);
                    fullType = "message";
                }
                else if ("control".equals(typeCode)) {
                    // Nếu là tin nhắn điều khiển (Backend gửi đi), thường ta bỏ qua
                    // hoặc nếu muốn lưu log thì xử lý ở đây.
                    logger.debug("Bỏ qua tin nhắn điều khiển: {}", rawJson);
                    return;
                }
                else {
                    logger.warn("Loại dữ liệu lạ (typeCode={}): {}", typeCode, topic);
                    return;
                }

                // 4. Chuyển sang Entity Database (Dùng Mapper bạn đã viết)
                TelemetryData dbEntry = telemetryMapper.toEntity(topic, payloadObj);

                // 5. LƯU VÀO POSTGRESQL
                telemetryRepository.save(dbEntry);

                logger.info(">> ĐÃ LƯU DB: Dev={} | Type={} | ID={}", deviceId, fullType, dbEntry.getId());

                processBusinessLogic(dbEntry);

            } catch (Exception e) {
                logger.error("Lỗi xử lý tin nhắn MQTT: {} | Data: {}", e.getMessage(), rawJson);

            }
        };
    }


    private void processBusinessLogic(TelemetryData dbEntry) {
        Map<String, Object> data = dbEntry.getPayload();
        String type = dbEntry.getDataType();

        if ("sensor".equals(type)) {

            String name = (String) data.get("name");
            Double value = ((Number) data.get("value")).doubleValue();

            logger.info("🌡 Dữ liệu đo: {} = {}", name, value);


            if ("temperature".equals(name) && value > 50) {
                logger.error("🔥 CẢNH BÁO: QUÁ NHIỆT ({})!", value);
            }
        }
        else if ("message".equals(type)) {
            String category = (String) data.get("category");
            String content = (String) data.get("content");

            logger.info("📩 Tin nhắn [{}]: {}", category, content);


            if ("QR_CODE".equals(category)) {
                logger.info(">> Đang kiểm tra kho hàng mã: {}", content);
            }
        }
    }

    @Bean
    public IntegrationFlow messageHandlingFlow() {
        return IntegrationFlow.from(MQTT_INPUT_CHANNEL).handle(mqttMessageHandler()).get();
    }
}
