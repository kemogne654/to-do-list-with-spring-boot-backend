package com.todoapp.todo_backend.controller

import com.todoapp.todo_backend.dto.*
import com.todoapp.todo_backend.entity.*
import com.todoapp.todo_backend.service.AuthService
import com.todoapp.todo_backend.service.TodoService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = ["http://localhost:3000", "http://127.0.0.1:3000"])
class TodoController(
    private val todoService: TodoService,
    private val authService: AuthService
) {
    
    @GetMapping
    fun getTodos(
        @RequestHeader("Authorization") authorization: String,
        @RequestParam(required = false) status: Status?,
        @RequestParam(required = false) category: Category?,
        @RequestParam(required = false) priority: Priority?,
        @RequestParam(required = false) overdue: Boolean?
    ): ResponseEntity<TodosResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val user = authService.getCurrentUser(userId)
            val response = todoService.getTodos(userId, user.email, status, category, priority, overdue)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }
    
    @PostMapping
    fun createTodo(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: CreateTodoRequest
    ): ResponseEntity<TodoResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val response = todoService.createTodo(userId, request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PutMapping("/{id}")
    fun updateTodo(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: String,
        @RequestBody request: UpdateTodoRequest
    ): ResponseEntity<TodoResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val user = authService.getCurrentUser(userId)
            val response = todoService.updateTodo(userId, user.email, id, request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/{id}")
    fun deleteTodo(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: String
    ): ResponseEntity<Map<String, String>> {
        return try {
            val userId = getUserIdFromToken(authorization)
            todoService.deleteTodo(userId, id)
            ResponseEntity.ok(mapOf("message" to "Todo deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @DeleteMapping("/bulk")
    fun bulkDelete(
        @RequestHeader("Authorization") authorization: String,
        @RequestBody request: BulkDeleteRequest
    ): ResponseEntity<BulkDeleteResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val response = todoService.bulkDelete(userId, request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @PatchMapping("/{id}/complete")
    fun completeTodo(
        @RequestHeader("Authorization") authorization: String,
        @PathVariable id: String,
        @RequestBody request: CompleteTodoRequest
    ): ResponseEntity<TodoResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val user = authService.getCurrentUser(userId)
            val response = todoService.completeTodo(userId, user.email, id, request)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.badRequest().build()
        }
    }
    
    @GetMapping("/stats")
    fun getStats(
        @RequestHeader("Authorization") authorization: String
    ): ResponseEntity<StatsResponse> {
        return try {
            val userId = getUserIdFromToken(authorization)
            val user = authService.getCurrentUser(userId)
            val response = todoService.getStats(userId, user.email)
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(401).build()
        }
    }
    
    private fun getUserIdFromToken(authorization: String): String {
        val token = authorization.removePrefix("Bearer ")
        return authService.validateToken(token) ?: throw RuntimeException("Invalid token")
    }
}