package com.mqtt.Payload;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessagePayload extends BasePayload {

    private String content;

    private String category;

    private String level;
}