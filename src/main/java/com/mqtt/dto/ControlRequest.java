package com.mqtt.dto;

import lombok.Data;

@Data
public class ControlRequest {
    private String deviceId;
    private String command;
    private boolean status;
}