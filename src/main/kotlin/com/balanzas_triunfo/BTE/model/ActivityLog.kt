package com.balanzastriunfo.bte.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "activity_logs")
data class ActivityLog(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val userId: Long?,
    val username: String?,
    val action: String,
    val timestamp: LocalDateTime = LocalDateTime.now(),
    @Column(length = 1000)
    val details: String?
)
