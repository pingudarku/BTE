package com.balanzastriunfo.bte.controller

import com.balanzastriunfo.bte.model.Product
import com.balanzastriunfo.bte.service.ProductService
import com.balanzastriunfo.bte.service.ActivityLogService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/products")
class ProductController(
    private val productService: ProductService,
    private val activityLogService: ActivityLogService
) {

    @GetMapping("/search")
    fun searchProductsByModel(
        @RequestParam model: String,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<List<Product>> {
        val products = productService.findProductsByModel(model)
        val username = userDetails?.username
        activityLogService.logActivity(null, username, "PRODUCT_SEARCH", "Búsqueda de producto por modelo: $model")
        return ResponseEntity.ok(products)
    }

    @GetMapping("/{id}")
    fun getProductById(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<Product> {
        val product = productService.findProductById(id)
        val username = userDetails?.username
        val userId = (userDetails as? com.balanzastriunfo.bte.model.User)?.id
        activityLogService.logActivity(userId, username, "PRODUCT_VIEW", "Visualización de producto con ID: $id")
        return if (product != null) {
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @GetMapping
    fun getAllProducts(
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<List<Product>> {
        val products = productService.getAllProducts()
        val username = userDetails?.username
        val userId = (userDetails as? com.balanzastriunfo.bte.model.User)?.id
        activityLogService.logActivity(userId, username, "PRODUCT_LIST", "Listado de todos los productos")
        return ResponseEntity.ok(products)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun createProduct(
        @RequestBody product: Product,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Product> {
        val createdProduct = productService.saveProduct(product)
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(userId, userDetails.username, "PRODUCT_ADDED", "Producto ${createdProduct.model} agregado con ID: ${createdProduct.id}")
        return ResponseEntity.ok(createdProduct)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestBody updatedProduct: Product,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Product> {
        val product = productService.updateProduct(id, updatedProduct)
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        return if (product != null) {
            activityLogService.logActivity(userId, userDetails.username, "PRODUCT_UPDATED", "Producto ${product.model} con ID: $id actualizado")
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    fun deleteProduct(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        productService.deleteProduct(id)
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(userId, userDetails.username, "PRODUCT_DELETED", "Producto con ID: $id eliminado")
        return ResponseEntity.noContent().build()
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/stock")
    fun updateProductStock(
        @PathVariable id: Long,
        @RequestParam newStock: Int,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Product> {
        val product = productService.updateProductStock(id, newStock)
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        return if (product != null) {
            activityLogService.logActivity(userId, userDetails.username, "STOCK_UPDATED", "Stock del producto ${product.model} (ID: $id) actualizado a: $newStock")
            ResponseEntity.ok(product)
        } else {
            ResponseEntity.notFound().build()
        }
    }
}
