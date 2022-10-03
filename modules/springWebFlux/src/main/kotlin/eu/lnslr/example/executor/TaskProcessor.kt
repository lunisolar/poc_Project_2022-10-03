package eu.lnslr.example.executor

import eu.lnslr.example.tasks.model.Result
import eu.lnslr.example.tasks.model.Status
import eu.lnslr.example.tasks.model.Task
import org.apache.logging.log4j.kotlin.Logging
import reactor.core.publisher.Mono

internal open class TaskProcessor(private val tasks: TasksClient) : Logging {

    private fun updateProgress(task: Task, i: Int, max: Int) {
        Thread.sleep(1000)
        tasks.save(task.apply {
            val percent = ((i.toDouble() / max.toDouble()) * 100).toInt()
            task.progress = "$percent%"
            task.status = Status.RUNNING
        })
        logger.debug { "Processing:  $task" }
    }

    fun process(task: Task): Mono<Task> {

        val result = TaskSolution().process(task) { i, max -> updateProgress(task, i, max) }

        finish(task, result)

        return Mono.just(task)
    }

    private fun finishNotFound(task: Task) {
        finish(task, Result(-1, 0))
    }

    fun finish(task: Task, result: Result) {
        tasks.save(task.apply {
            task.status = Status.FINISHED
            task.progress = "100%"
            task.result = result
        })
    }
}