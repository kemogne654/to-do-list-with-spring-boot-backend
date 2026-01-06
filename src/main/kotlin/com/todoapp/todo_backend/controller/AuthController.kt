package com.todoapp.todo_backend.controller

import com.todoapp.todo_backend.dto.*
import com.todoapp.todo_backend.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class AuthController(private val authService: AuthService) {
    
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.login(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PostMapping("/register")
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        return try {
            val response = authService.register(request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        return ResponseEntity.ok(mapOf("message" to "Logged out successfully"))
    }
    
    @GetMapping("/me")
    fun getCurrentUser(@RequestHeader("Authorization") authorization: String): ResponseEntity<UserResponse> {
        return try {
            val token = authorization.removePrefix("Bearer ")
            val userId = authService.validateToken(token) ?: throw RuntimeException("Invalid token")
            val user = authService.getCurrentUser(userId)
            ResponseEntity.ok(user)
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }
    
    @GetMapping("/users")
    fun getAllUsers(@RequestHeader("Authorization") authorization: String): ResponseEntity<List<UserResponse>> {
        return try {
            val token = authorization.removePrefix("Bearer ")
            authService.validateToken(token) ?: throw RuntimeException("Invalid token")
            val users = authService.getAllUsers()
            ResponseEntity.ok(users)
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }
}