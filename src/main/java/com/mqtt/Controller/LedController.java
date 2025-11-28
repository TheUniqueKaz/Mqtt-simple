package com.mqtt.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mqtt.Config.MqttGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/control")
@CrossOrigin(origins = "*")
public class LedController {

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/led")
    public String controlLed(@RequestBody Map<String, Object> request) {
        try {

            String deviceId = (String) request.get("deviceId");
            Boolean status = (Boolean) request.get("status");


            String topic = "u/" + deviceId + "/control";


            String commandStr = status ? "ON" : "OFF";

            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("cmd", commandStr);

            String jsonPayload = objectMapper.writeValueAsString(payloadMap);


            mqttGateway.sendToMqtt(jsonPayload, topic);
            System.out.println(">> Backend gửi lệnh: " + jsonPayload + " vào topic: " + topic);

            return "Đã gửi lệnh: " + jsonPayload;

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi: " + e.getMessage();
        }
    }
}