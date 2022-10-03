package eu.lnslr.example.executor

import eu.lnslr.example.tasks.SyncTaskRepository
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers


@Configuration
internal class ExecutorConfiguration() : Logging {

    @Bean
    fun taskClient(tasks: SyncTaskRepository): TasksClient = TasksClient(tasks)

    @Bean
    fun taskSupplier(tasks: TasksClient): TaskSupplier = TaskSupplier(tasks)

    @Bean
    fun taskProcessor(tasks: TasksClient): TaskProcessor = TaskProcessor(tasks)

    @Bean
    fun configureTasksPipeline(tasks: TaskSupplier, processor: TaskProcessor): ApplicationRunner {

        return ApplicationRunner {
            logger.info("Configuring...")

            val scheduler: Scheduler = Schedulers.newBoundedElastic(5, 5, "MyThreadGroup")

            tasks.asFlux()
                .parallel()
                .runOn(scheduler)
                .map { processor.process(it) }
                .subscribe();

            logger.info("Configuration done")
        }
    }

}
