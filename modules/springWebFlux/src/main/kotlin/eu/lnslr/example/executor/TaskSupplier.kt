package eu.lnslr.example.executor

import eu.lnslr.example.tasks.model.Task
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.scheduling.annotation.Scheduled
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import reactor.core.publisher.Sinks.EmitFailureHandler.FAIL_FAST

/**
 * Assumption: might need to switch to remotely call repository via REST webservices.
 */
open class TaskSupplier(private val tasks: TasksClient): Logging {

    val sink: Sinks.Many<Task> = Sinks.many().multicast().onBackpressureBuffer<Task>(1)
    val flux = sink.asFlux()

    public fun asFlux(): Flux<Task> = flux;

    @Scheduled(fixedDelay = 2000) // value set just to not flood the logs
    open fun checkForTasks() {

        do {
            val next = tasks.next()

            next.map { task ->

                logger.debug("Emitting...: $task")
                val result = sink.emitNext(next.get(), FAIL_FAST)
                logger.debug("Emitting done: $task")

                return@map true
            }

        } while (next.isPresent)


    }

    private fun unlockTask(task: Task) {
        tasks.save(task.apply { progress = null })
    }


}