package com.todoapp.todo_backend.service

import com.todoapp.todo_backend.dto.*
import com.todoapp.todo_backend.entity.User
import com.todoapp.todo_backend.entity.Role
import com.todoapp.todo_backend.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class AuthService(private val userRepository: UserRepository) {
    
    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw RuntimeException("Invalid credentials")
        
        if (user.password != request.password) {
            throw RuntimeException("Invalid credentials")
        }
        
        val token = generateToken(user.id)
        return AuthResponse(
            token = token,
            user = UserResponse(user.id, user.email, user.name, user.role)
        )
    }
    
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.findByEmail(request.email) != null) {
            throw RuntimeException("Email already exists")
        }
        
        val user = User(
            id = UUID.randomUUID().toString(),
            email = request.email,
            password = request.password,
            name = request.name,
            role = request.role
        )
        
        val savedUser = userRepository.save(user)
        val token = generateToken(savedUser.id)
        
        return AuthResponse(
            token = token,
            user = UserResponse(savedUser.id, savedUser.email, savedUser.name, savedUser.role)
        )
    }
    
    fun getCurrentUser(userId: String): UserResponse {
        val user = userRepository.findById(userId).orElseThrow { RuntimeException("User not found") }
        return UserResponse(user.id, user.email, user.name, user.role)
    }
    
    fun getAllUsers(): List<UserResponse> {
        val users = userRepository.findAll()
        return users.map { UserResponse(it.id, it.email, it.name, it.role) }
    }
    
    private fun generateToken(userId: String): String {
        return "jwt_token_$userId"
    }
    
    fun validateToken(token: String): String? {
        return if (token.startsWith("jwt_token_")) {
            token.removePrefix("jwt_token_")
        } else null
    }
}