package com.balanzastriunfo.bte.controller

import com.balanzastriunfo.bte.service.ActivityLogService
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
class AdminController(
    private val activityLogService: ActivityLogService
) {

    @GetMapping("/reports/activity-excel", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    fun getActivityReportExcel(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<ByteArrayResource> {
        val excelBytes = activityLogService.generateActivityReport()
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(userId, userDetails.username, "REPORT_GENERATED", "Informe de actividad Excel generado")

        val headers = HttpHeaders()
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=registro_actividad.xlsx")
        headers.add(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(excelBytes.size.toLong())
            .body(ByteArrayResource(excelBytes))
    }
}
