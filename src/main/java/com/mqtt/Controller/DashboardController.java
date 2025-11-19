package com.mqtt.Controller;

import com.mqtt.Entity.TelemetryData;
import com.mqtt.Repository.TelemetryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private TelemetryRepository repository;

    @GetMapping("/sensors")
    public List<TelemetryData> getLatestSensors() {
        return repository.findByDataTypeOrderByCreatedAtDesc("sensor", PageRequest.of(0, 20));
    }

    @GetMapping("/messages")
    public List<TelemetryData> getLatestMessages() {
        return repository.findByDataTypeOrderByCreatedAtDesc("message", PageRequest.of(0, 10));
    }
}