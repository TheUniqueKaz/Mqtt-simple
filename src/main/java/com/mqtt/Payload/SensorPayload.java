package com.mqtt.Payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SensorPayload extends BasePayload {

    private String name;

    private Double value;

    private String unit;
}