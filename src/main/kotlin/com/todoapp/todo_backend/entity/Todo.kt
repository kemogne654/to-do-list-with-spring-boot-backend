package com.todoapp.todo_backend.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDate
import java.time.LocalDateTime

enum class Category { Personal, Work, Shopping, Health, Other }
enum class Priority { low, medium, high }
enum class Status { pending, `in-process`, completed }

@Entity
@Table(name = "todos")
data class Todo(
    @Id
    val id: String,
    
    @Column(name = "user_id", nullable = false)
    val userId: String,
    
    @Column(nullable = false)
    val title: String,
    
    @Column(columnDefinition = "TEXT")
    val description: String? = null,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val category: Category,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val priority: Priority,
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: Status = Status.pending,
    
    @Column(name = "due_date")
    val dueDate: LocalDate? = null,
    
    @Column(name = "assigned_to")
    val assignedTo: String? = null,
    
    @Column(name = "completion_note", columnDefinition = "TEXT")
    val completionNote: String? = null,
    
    @CreationTimestamp
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    
    @Column(name = "completed_at")
    val completedAt: LocalDateTime? = null
)