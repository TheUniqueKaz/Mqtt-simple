package com.mqtt.Repository;

import com.mqtt.Entity.TelemetryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryData,Long> {
}
