package com.balanzastriunfo.bte.service

import com.balanzastriunfo.bte.model.ActivityLog
import com.balanzastriunfo.bte.repository.ActivityLogRepository
import jakarta.transaction.Transactional
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime

@Service
class ActivityLogService(private val activityLogRepository: ActivityLogRepository) {

    @Transactional
    fun logActivity(userId: Long?, username: String?, action: String, details: String?) {
        val log = ActivityLog(userId = userId, username = username, action = action, details = details)
        activityLogRepository.save(log)
    }

    fun getAllActivityLogs(): List<ActivityLog> {
        return activityLogRepository.findAll()
    }

    fun generateActivityReport(): ByteArray {
        val logs = activityLogRepository.findAll()
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Registro de Actividad")

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("ID")
        headerRow.createCell(1).setCellValue("ID Usuario")
        headerRow.createCell(2).setCellValue("Nombre de Usuario")
        headerRow.createCell(3).setCellValue("Acción")
        headerRow.createCell(4).setCellValue("Fecha y Hora")
        headerRow.createCell(5).setCellValue("Detalles")

        logs.forEachIndexed { index, log ->
            val row = sheet.createRow(index + 1)
            row.createCell(0).setCellValue(log.id.toDouble())
            row.createCell(1).setCellValue(log.userId?.toDouble() ?: 0.0)
            row.createCell(2).setCellValue(log.username ?: "N/A")
            row.createCell(3).setCellValue(log.action)
            row.createCell(4).setCellValue(log.timestamp.toString())
            row.createCell(5).setCellValue(log.details ?: "")
        }

        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.close()
        return outputStream.toByteArray()
    }
}
