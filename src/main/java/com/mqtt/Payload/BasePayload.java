package com.mqtt.Payload;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SensorPayload.class, name = "sensor"),

        @JsonSubTypes.Type(value = MessagePayload.class, name = "message")
})
public abstract class BasePayload {

    private String deviceId;

    private String type;

    private String timestamp;
}