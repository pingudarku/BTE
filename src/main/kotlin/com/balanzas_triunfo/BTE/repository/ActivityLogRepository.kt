package com.balanzastriunfo.bte.repository

import com.balanzastriunfo.bte.model.ActivityLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ActivityLogRepository : JpaRepository<ActivityLog, Long> {
    fun findByTimestampBetween(start: LocalDateTime, end: LocalDateTime): List<ActivityLog>
}
