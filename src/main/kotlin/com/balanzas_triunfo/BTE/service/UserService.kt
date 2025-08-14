package com.balanzastriunfo.bte.service

import com.balanzastriunfo.bte.model.Role
import com.balanzastriunfo.bte.model.User
import com.balanzastriunfo.bte.repository.UserRepository
import jakarta.annotation.PostConstruct
import jakarta.transaction.Transactional
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Usuario no encontrado: $username")
    }

    @Transactional
    fun registerUser(username: String, passwordRaw: String, role: Role): User {
        val passwordHash = passwordEncoder.encode(passwordRaw)
        val newUser = User(username = username, passwordHash = passwordHash, role = role)
        return userRepository.save(newUser)
    }

    @Transactional
    fun updateUserRole(userId: Long, newRole: Role): User? {
        return userRepository.findById(userId).map { existingUser ->
            val updatedUser = existingUser.copy(role = newRole)
            userRepository.save(updatedUser)
        }.orElse(null)
    }

    @Transactional
    fun deleteUser(userId: Long) {
        userRepository.deleteById(userId)
    }

    fun getAllUsers(): List<User> {
        return userRepository.findAll()
    }

    @PostConstruct
    fun init() {
        if (userRepository.count() == 0L) {
            registerUser("admin", "adminpass", Role.ADMIN)
            registerUser("user", "userpass", Role.USER)
        }
    }
}
