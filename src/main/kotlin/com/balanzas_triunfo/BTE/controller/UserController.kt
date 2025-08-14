package com.balanzastriunfo.bte.controller

import com.balanzastriunfo.bte.model.Role
import com.balanzastriunfo.bte.service.UserService
import com.balanzastriunfo.bte.service.JwtService
import com.balanzastriunfo.bte.service.ActivityLogService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails

data class LoginRequest(val username: String, val password: String)
data class LoginResponse(val token: String)
data class RegisterRequest(val username: String, val password: String, val role: Role)
data class UpdateRoleRequest(val role: Role)

@RestController
@RequestMapping("/api/auth")
class UserController(
    private val userService: UserService,
    private val jwtService: JwtService,
    private val authenticationManager: AuthenticationManager,
    private val activityLogService: ActivityLogService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest, @AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<String> {
        val newUser = userService.registerUser(request.username, request.password, request.role)
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(userId, userDetails.username, "USER_REGISTER", "Nuevo usuario ${newUser.username} registrado con rol: ${newUser.role}")
        return ResponseEntity.ok("Usuario registrado exitosamente: ${newUser.username}")
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<LoginResponse> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val userDetails = authentication.principal as UserDetails
        val token = jwtService.generateToken(userDetails)
        activityLogService.logActivity(null, userDetails.username, "LOGIN", "Inicio de sesión exitoso para: ${userDetails.username}")
        return ResponseEntity.ok(LoginResponse(token))
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    fun getAllUsers(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<List<com.balanzastriunfo.bte.model.User>> {
        val users = userService.getAllUsers()
        val userId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(userId, userDetails.username, "USER_LIST", "Listado de todos los usuarios")
        return ResponseEntity.ok(users)
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/role")
    fun updateUserRole(
        @PathVariable id: Long,
        @RequestBody request: UpdateRoleRequest,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<com.balanzastriunfo.bte.model.User> {
        val updatedUser = userService.updateUserRole(id, request.role)
        val adminUserId = (userDetails as com.balanzastriunfo.bte.model.User).id
        return if (updatedUser != null) {
            activityLogService.logActivity(adminUserId, userDetails.username, "USER_ROLE_UPDATED", "Rol de usuario ${updatedUser.username} (ID: $id) actualizado a: ${updatedUser.role}")
            ResponseEntity.ok(updatedUser)
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    fun deleteUser(
        @PathVariable id: Long,
        @AuthenticationPrincipal userDetails: UserDetails
    ): ResponseEntity<Void> {
        userService.deleteUser(id)
        val adminUserId = (userDetails as com.balanzastriunfo.bte.model.User).id
        activityLogService.logActivity(adminUserId, userDetails.username, "USER_DELETED", "Usuario con ID: $id eliminado")
        return ResponseEntity.noContent().build()
    }
}
