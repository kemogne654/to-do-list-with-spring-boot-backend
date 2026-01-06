package com.todoapp.todo_backend.service

import com.todoapp.todo_backend.dto.*
import com.todoapp.todo_backend.entity.*
import com.todoapp.todo_backend.repository.TodoRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

@Service
class TodoService(private val todoRepository: TodoRepository) {
    
    fun getTodos(userId: String, userEmail: String, status: Status?, category: Category?, priority: Priority?, overdue: Boolean?): TodosResponse {
        // Get tasks created by user
        val createdTodos = when {
            overdue == true -> todoRepository.findOverdueTodos(userId, LocalDate.now())
            status != null -> todoRepository.findByUserIdAndStatus(userId, status)
            category != null -> todoRepository.findByUserIdAndCategory(userId, category)
            priority != null -> todoRepository.findByUserIdAndPriority(userId, priority)
            else -> todoRepository.findByUserId(userId)
        }
        
        // Get tasks assigned to user
        val assignedTodos = when {
            status != null -> todoRepository.findByAssignedToAndStatus(userEmail, status)
            category != null -> todoRepository.findByAssignedToAndCategory(userEmail, category)
            priority != null -> todoRepository.findByAssignedToAndPriority(userEmail, priority)
            else -> todoRepository.findByAssignedTo(userEmail)
        }
        
        // Combine and deduplicate
        val allTodos = (createdTodos + assignedTodos).distinctBy { it.id }
        
        // Apply additional filters
        val filteredTodos = allTodos.filter { todo ->
            (status == null || todo.status == status) &&
            (category == null || todo.category == category) &&
            (priority == null || todo.priority == priority) &&
            (overdue != true || (todo.dueDate != null && todo.dueDate!! < LocalDate.now() && todo.status != Status.completed))
        }
        
        val todoResponses = filteredTodos.map { it.toResponse() }
        return TodosResponse(todoResponses, todoResponses.size)
    }
    
    fun createTodo(userId: String, request: CreateTodoRequest): TodoResponse {
        val todo = Todo(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = request.title,
            description = request.description,
            category = request.category,
            priority = request.priority,
            dueDate = request.dueDate,
            assignedTo = request.assignedTo
        )
        
        val savedTodo = todoRepository.save(todo)
        return savedTodo.toResponse()
    }
    
    fun updateTodo(userId: String, userEmail: String, todoId: String, request: UpdateTodoRequest): TodoResponse {
        val todo = todoRepository.findById(todoId).orElseThrow { RuntimeException("Todo not found") }
        
        // Allow update if user is creator OR assignee
        if (todo.userId != userId && todo.assignedTo != userEmail) {
            throw RuntimeException("Unauthorized")
        }
        
        val updatedTodo = todo.copy(
            title = request.title ?: todo.title,
            description = request.description ?: todo.description,
            category = request.category ?: todo.category,
            priority = request.priority ?: todo.priority,
            status = request.status ?: todo.status,
            dueDate = request.dueDate ?: todo.dueDate,
            assignedTo = request.assignedTo ?: todo.assignedTo
        )
        
        val savedTodo = todoRepository.save(updatedTodo)
        return savedTodo.toResponse()
    }
    
    fun deleteTodo(userId: String, todoId: String) {
        val todo = todoRepository.findById(todoId).orElseThrow { RuntimeException("Todo not found") }
        
        if (todo.userId != userId) {
            throw RuntimeException("Unauthorized")
        }
        
        todoRepository.deleteById(todoId)
    }
    
    fun completeTodo(userId: String, userEmail: String, todoId: String, request: CompleteTodoRequest): TodoResponse {
        val todo = todoRepository.findById(todoId).orElseThrow { RuntimeException("Todo not found") }
        
        // Allow completion if user is creator OR assignee
        if (todo.userId != userId && todo.assignedTo != userEmail) {
            throw RuntimeException("Unauthorized")
        }
        
        val completedTodo = todo.copy(
            status = Status.completed,
            completionNote = request.completionNote,
            completedAt = LocalDateTime.now()
        )
        
        val savedTodo = todoRepository.save(completedTodo)
        return savedTodo.toResponse()
    }
    
    fun bulkDelete(userId: String, request: BulkDeleteRequest): BulkDeleteResponse {
        val todos = todoRepository.findAllById(request.todoIds)
        val userTodos = todos.filter { it.userId == userId }
        
        todoRepository.deleteAll(userTodos)
        
        return BulkDeleteResponse(
            deletedCount = userTodos.size,
            message = "Todos deleted successfully"
        )
    }
    
    fun getStats(userId: String, userEmail: String): StatsResponse {
        val total = todoRepository.countByUserId(userId)
        val completed = todoRepository.countByUserIdAndStatus(userId, Status.completed)
        val pending = todoRepository.countByUserIdAndStatus(userId, Status.pending)
        val inProcess = todoRepository.countByUserIdAndStatus(userId, Status.`in-process`)
        
        val overdueTodos = todoRepository.findOverdueTodosForUserOrAssigned(userId, userEmail, LocalDate.now())
        val overdue = overdueTodos.size
        
        val categories = mapOf(
            "Personal" to todoRepository.countByUserIdAndCategory(userId, Category.Personal),
            "Work" to todoRepository.countByUserIdAndCategory(userId, Category.Work),
            "Shopping" to todoRepository.countByUserIdAndCategory(userId, Category.Shopping),
            "Health" to todoRepository.countByUserIdAndCategory(userId, Category.Health),
            "Other" to todoRepository.countByUserIdAndCategory(userId, Category.Other)
        )
        
        val priorities = mapOf(
            "low" to todoRepository.countByUserIdAndPriority(userId, Priority.low),
            "medium" to todoRepository.countByUserIdAndPriority(userId, Priority.medium),
            "high" to todoRepository.countByUserIdAndPriority(userId, Priority.high)
        )
        
        return StatsResponse(
            total = total,
            completed = completed,
            pending = pending,
            inProcess = inProcess,
            overdue = overdue,
            categories = categories,
            priorities = priorities
        )
    }
    
    private fun Todo.toResponse() = TodoResponse(
        id = id,
        title = title,
        description = description,
        category = category,
        priority = priority,
        status = status,
        dueDate = dueDate,
        assignedTo = assignedTo,
        completionNote = completionNote,
        createdAt = createdAt,
        completedAt = completedAt
    )
}