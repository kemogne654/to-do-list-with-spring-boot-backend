package com.todoapp.todo_backend.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import javax.sql.DataSource

@Configuration
class DatabaseConfig(private val env: Environment) {

    @Bean
    fun dataSource(): DataSource {
        val databaseUrl = env.getProperty("DATABASE_URL")
        
        return if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            // Convert Render's PostgreSQL URL to JDBC format
            val jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://")
            
            DataSourceBuilder.create()
                .url(jdbcUrl)
                .driverClassName("org.postgresql.Driver")
                .build()
        } else {
            // Use default configuration from application.yaml
            DataSourceBuilder.create().build()
        }
    }
}