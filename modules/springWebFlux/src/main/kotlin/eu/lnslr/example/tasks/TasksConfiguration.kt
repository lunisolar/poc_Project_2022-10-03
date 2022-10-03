package eu.lnslr.example.tasks

import eu.lunisolar.lava.rdf.api.t4.Dataset
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.router

@Configuration
internal class TasksConfiguration() {

    @Bean
    fun tasksRouter(handler: TasksHandler) = router {

        POST("/tasks", handler::register)

        GET("/tasks", handler::listAll)
        GET("/tasks/peekNext", handler::peekNext)
        GET("/tasks/{id}", handler::get)

    }

    @Bean
    fun syncTaskRepository(ds: Dataset) = SyncTaskRepository(ds)

    @Bean
    fun taskRepository() = TaskRepository()

    @Bean
    fun taskHandler() = TasksHandler()
}