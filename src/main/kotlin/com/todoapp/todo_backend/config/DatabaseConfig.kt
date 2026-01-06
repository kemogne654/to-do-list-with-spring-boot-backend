package com.todoapp.todo_backend.config

import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import java.net.URI
import javax.sql.DataSource

@Configuration
class DatabaseConfig(private val env: Environment) {

    @Bean
    fun dataSource(): DataSource {
        val databaseUrl = env.getProperty("DATABASE_URL")
        
        return if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            // Parse Render's PostgreSQL URL
            val uri = URI(databaseUrl)
            val jdbcUrl = "jdbc:postgresql://${uri.host}:${uri.port}${uri.path}"
            val username = uri.userInfo.split(":")[0]
            val password = uri.userInfo.split(":")[1]
            
            DataSourceBuilder.create()
                .url(jdbcUrl)
                .username(username)
                .password(password)
                .driverClassName("org.postgresql.Driver")
                .build()
        } else {
            // Use default configuration from application.yaml
            DataSourceBuilder.create().build()
        }
    }
}