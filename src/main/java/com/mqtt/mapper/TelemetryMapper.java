package com.mqtt.mapper;

import com.mqtt.Entity.TelemetryData;
import com.mqtt.Payload.BasePayload;
import com.mqtt.Payload.MessagePayload;
import com.mqtt.Payload.SensorPayload;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TelemetryMapper {

    public TelemetryData toEntity(String topic, BasePayload payload) {
        TelemetryData dbEntry = new TelemetryData();

        String[] parts = topic.split("/");

        if (parts.length >= 3) {
            dbEntry.setDeviceId(parts[1]);
            String typeCode = parts[2];
            if ("s".equals(typeCode)) dbEntry.setDataType("sensor");
            else if ("m".equals(typeCode)) dbEntry.setDataType("message");
            else dbEntry.setDataType("unknown");
        } else {
            dbEntry.setDeviceId("unknown_device");
            dbEntry.setDataType("unknown");
        }
        dbEntry.setTopic(topic);
        Map<String, Object> map = new HashMap<>();

        if (payload instanceof SensorPayload) {
            SensorPayload p = (SensorPayload) payload;

            String longName = p.getName();
            if ("t".equals(longName)) longName = "temperature";
            else if ("h".equals(longName)) longName = "humidity";
            else if ("l".equals(longName)) longName = "light";

            map.put("name", longName);
            map.put("value", p.getValue());

        }
        else if (payload instanceof MessagePayload) {
            MessagePayload p = (MessagePayload) payload;

            map.put("category", p.getCategory());
            map.put("content", p.getContent());
            if (p.getLevel() != null) {
                map.put("level", p.getLevel());
            }
        }
        dbEntry.setPayload(map);

        return dbEntry;
    }
}