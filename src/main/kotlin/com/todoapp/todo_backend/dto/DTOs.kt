package com.todoapp.todo_backend.dto

import com.todoapp.todo_backend.entity.Category
import com.todoapp.todo_backend.entity.Priority
import com.todoapp.todo_backend.entity.Status
import com.todoapp.todo_backend.entity.Role
import java.time.LocalDate
import java.time.LocalDateTime

// Auth DTOs
data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String, val role: Role = Role.user)
data class AuthResponse(val token: String, val user: UserResponse)
data class UserResponse(val id: String, val email: String, val name: String, val role: Role)

// Todo DTOs
data class CreateTodoRequest(
    val title: String,
    val description: String? = null,
    val category: Category,
    val priority: Priority,
    val dueDate: LocalDate? = null,
    val assignedTo: String? = null
)

data class UpdateTodoRequest(
    val title: String? = null,
    val description: String? = null,
    val category: Category? = null,
    val priority: Priority? = null,
    val status: Status? = null,
    val dueDate: LocalDate? = null,
    val assignedTo: String? = null
)

data class CompleteTodoRequest(val completionNote: String? = null)

data class TodoResponse(
    val id: String,
    val title: String,
    val description: String?,
    val category: Category,
    val priority: Priority,
    val status: Status,
    val dueDate: LocalDate?,
    val assignedTo: String?,
    val completionNote: String?,
    val createdAt: LocalDateTime,
    val completedAt: LocalDateTime?
)

data class TodosResponse(val todos: List<TodoResponse>, val total: Int)

data class BulkDeleteRequest(val todoIds: List<String>)
data class BulkDeleteResponse(val deletedCount: Int, val message: String)

data class StatsResponse(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val inProcess: Int,
    val overdue: Int,
    val categories: Map<String, Int>,
    val priorities: Map<String, Int>
)