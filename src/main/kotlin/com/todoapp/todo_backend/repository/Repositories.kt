package com.todoapp.todo_backend.repository

import com.todoapp.todo_backend.entity.User
import com.todoapp.todo_backend.entity.Todo
import com.todoapp.todo_backend.entity.Category
import com.todoapp.todo_backend.entity.Priority
import com.todoapp.todo_backend.entity.Status
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface UserRepository : JpaRepository<User, String> {
    fun findByEmail(email: String): User?
}

@Repository
interface TodoRepository : JpaRepository<Todo, String> {
    fun findByUserId(userId: String): List<Todo>
    fun findByUserIdAndStatus(userId: String, status: Status): List<Todo>
    fun findByUserIdAndCategory(userId: String, category: Category): List<Todo>
    fun findByUserIdAndPriority(userId: String, priority: Priority): List<Todo>
    fun findByUserIdAndStatusAndCategory(userId: String, status: Status, category: Category): List<Todo>
    fun findByUserIdAndStatusAndPriority(userId: String, status: Status, priority: Priority): List<Todo>
    fun findByUserIdAndCategoryAndPriority(userId: String, category: Category, priority: Priority): List<Todo>
    fun findByUserIdAndStatusAndCategoryAndPriority(userId: String, status: Status, category: Category, priority: Priority): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId AND t.dueDate < :today AND t.status != 'completed'")
    fun findOverdueTodos(userId: String, today: LocalDate): List<Todo>
    
    // New methods for assigned todos
    @Query("SELECT t FROM Todo t WHERE t.userId = :userId OR t.assignedTo = :userEmail")
    fun findByUserIdOrAssignedTo(userId: String, userEmail: String): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE (t.userId = :userId OR t.assignedTo = :userEmail) AND t.status = :status")
    fun findByUserIdOrAssignedToAndStatus(userId: String, userEmail: String, status: Status): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE (t.userId = :userId OR t.assignedTo = :userEmail) AND t.category = :category")
    fun findByUserIdOrAssignedToAndCategory(userId: String, userEmail: String, category: Category): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE (t.userId = :userId OR t.assignedTo = :userEmail) AND t.priority = :priority")
    fun findByUserIdOrAssignedToAndPriority(userId: String, userEmail: String, priority: Priority): List<Todo>
    
    @Query("SELECT t FROM Todo t WHERE (t.userId = :userId OR t.assignedTo = :userEmail) AND t.dueDate < :today AND t.status != 'completed'")
    fun findOverdueTodosForUserOrAssigned(userId: String, userEmail: String, today: LocalDate): List<Todo>
    
    fun countByUserIdAndStatus(userId: String, status: Status): Int
    fun countByUserIdAndCategory(userId: String, category: Category): Int
    fun countByUserIdAndPriority(userId: String, priority: Priority): Int
    fun countByUserId(userId: String): Int
}