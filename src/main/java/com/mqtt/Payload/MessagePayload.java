package com.mqtt.Payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessagePayload extends BasePayload {

    @JsonProperty("c")
    private String content;
    @JsonProperty("k")
    private String category;
    @JsonProperty("l")
    private String level;
}

