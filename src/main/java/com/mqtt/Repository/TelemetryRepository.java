package com.mqtt.Repository;

import com.mqtt.Entity.TelemetryData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TelemetryRepository extends JpaRepository<TelemetryData,Long> {
    List<TelemetryData> findByDataTypeOrderByCreatedAtDesc(String dataType, Pageable pageable);
}
