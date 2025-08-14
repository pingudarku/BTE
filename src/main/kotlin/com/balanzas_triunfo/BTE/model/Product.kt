package com.balanzastriunfo.bte.model

import jakarta.persistence.*

@Entity
@Table(name = "products")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    var model: String,
    var serialNumber: String,
    var batteryInfo: String,
    var additionalDetails: String?,
    var imageUrl: String?,
    var stock: Int,
    var manualUrl: String?,
    var calibrationDocUrl: String?
)
