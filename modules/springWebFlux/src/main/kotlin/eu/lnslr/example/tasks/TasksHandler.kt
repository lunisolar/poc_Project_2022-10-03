package eu.lnslr.example.tasks

import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.lava.rdf.api.DefaultRdf.rdf
import org.springframework.context.annotation.Lazy
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.inject.Inject


internal open class TasksHandler() {

    @Lazy
    @Inject
    private lateinit var tasks: TaskRepository

    open fun register(request: ServerRequest): Mono<ServerResponse> = request
        .bodyToMono(Task::class.java)
        .flatMap {
            // Little protection for input, since whole Task state can be potentially included.
            tasks.save(Task(it.input, it.pattern))
        }
        .map(::simplifyId)
        .flatMap { ServerResponse.ok().body(fromValue(it)) }


    open fun listAll(request: ServerRequest): Mono<ServerResponse> {

        val stream: Flux<Task> = request.toMono()
            .flatMapMany { request ->
                val offset: Long = request.queryParam("offset").orElse(null)?.toLong() ?: 0
                val limit: Long = request.queryParam("offset").orElse(null)?.toLong() ?: 10000

                tasks.findAll(offset, limit);
            }.map(::simplifyId)

        return ServerResponse.ok().body(stream, Task::class.java)
    }

    open fun get(request: ServerRequest): Mono<ServerResponse> = request
        .toMono()
        // Not validating the ID - POC
        .flatMap {
            val id = rdf().id(Task.fullId(request.pathVariable("id").orEmpty()))
            tasks.findById(id!!)
        }
        .map(::simplifyId)
        .flatMap {
            ServerResponse.ok().body(fromValue(it))
        }.switchIfEmpty(
            ServerResponse.status(404).body(fromValue("No task was found."))
        )

    open fun peekNext(request: ServerRequest): Mono<ServerResponse> = request
        .toMono()
        .flatMap { tasks.nextToProcess() }
        .map(::simplifyId)
        .flatMap { ServerResponse.ok().body(fromValue(it)) }


    private fun simplifyId(task: Task): Task {
        return task.apply { id = Task.simpleId(id) }
    }

}