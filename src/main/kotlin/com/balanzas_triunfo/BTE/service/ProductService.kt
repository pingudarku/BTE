package com.balanzastriunfo.bte.service

import com.balanzastriunfo.bte.model.Product
import com.balanzastriunfo.bte.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.util.*

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }

    fun findProductById(id: Long): Product? {
        return productRepository.findById(id).orElse(null)
    }

    fun findProductsByModel(model: String): List<Product> {
        return productRepository.findByModelContainingIgnoreCase(model)
    }

    @Transactional
    fun saveProduct(product: Product): Product {
        return productRepository.save(product)
    }

    @Transactional
    fun updateProduct(id: Long, updatedProduct: Product): Product? {
        return productRepository.findById(id).map { existingProduct ->
            existingProduct.model = updatedProduct.model
            existingProduct.serialNumber = updatedProduct.serialNumber
            existingProduct.batteryInfo = updatedProduct.batteryInfo
            existingProduct.additionalDetails = updatedProduct.additionalDetails
            existingProduct.imageUrl = updatedProduct.imageUrl
            existingProduct.stock = updatedProduct.stock
            existingProduct.manualUrl = updatedProduct.manualUrl
            existingProduct.calibrationDocUrl = updatedProduct.calibrationDocUrl
            productRepository.save(existingProduct)
        }.orElse(null)
    }

    @Transactional
    fun deleteProduct(id: Long) {
        productRepository.deleteById(id)
    }

    @Transactional
    fun updateProductStock(productId: Long, newStock: Int): Product? {
        return productRepository.findById(productId).map { product ->
            product.stock = newStock
            productRepository.save(product)
        }.orElse(null)
    }
}
