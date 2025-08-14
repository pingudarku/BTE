package com.balanzastriunfo.bte.repository

import com.balanzastriunfo.bte.model.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : JpaRepository<Product, Long> {
    fun findByModelContainingIgnoreCase(model: String): List<Product>
}
