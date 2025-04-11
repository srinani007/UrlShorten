package com.prashanth.url_shortener.repository;

import com.prashanth.url_shortener.entity.ClickEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEventEntity, Long> {

    List<ClickEventEntity> findByShortCode(String shortCode);

    List<ClickEventEntity> findByShortCodeAndTimestampBetween(
            String shortCode, LocalDateTime from, LocalDateTime to);

    Page<ClickEventEntity> findByShortCode(String shortCode, Pageable pageable);
}

