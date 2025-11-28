package com.mqtt.Payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SensorPayload extends BasePayload {

    @JsonProperty("n")
    private String name;
    @JsonProperty("v")
    private Double value;
}

