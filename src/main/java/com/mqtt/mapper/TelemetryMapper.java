package com.mqtt.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqtt.Entity.TelemetryData;
import com.mqtt.Payload.BasePayload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TelemetryMapper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TelemetryData toEntity(String topic, BasePayload payloadObj) {
        TelemetryData dbEntry = new TelemetryData();


        dbEntry.setDeviceId(payloadObj.getDeviceId());
        dbEntry.setDataType(payloadObj.getType());
        dbEntry.setTopic(topic);

        Map<String, Object> payloadAsMap = objectMapper.convertValue(payloadObj, Map.class);
        dbEntry.setPayload(payloadAsMap);

        return dbEntry;
    }
}