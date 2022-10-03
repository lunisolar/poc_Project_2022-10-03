package eu.lnslr.example.tasks

import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.lava.lang.concurrentsemi.EventualCompute
import eu.lunisolar.lava.rdf.api.data.Id
import eu.lunisolar.magma.func.action.LAction
import org.reactivestreams.Publisher
import org.springframework.context.annotation.Lazy
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import javax.inject.Inject

internal open class TaskRepository() : ReactiveCrudRepository<Task, Id> {

    @Lazy
    @Inject
    private lateinit var syncRepo: SyncTaskRepository

    // Intended to aggregate multiple writes into one write transaction.
    val eventualWrite = EventualCompute.eventualCompute(100, 200) { action -> this.self.execute(action) }

    @Scheduled(fixedDelay = 100)
    open fun scheduledTick() = eventualWrite.tick()

    @Lazy
    @Inject
    private lateinit var self: TaskRepository;

    @Transactional(readOnly = false)
    public open fun execute(writeAction: LAction) {
        writeAction.execute();
    }

    override fun <S : Task> save(entity: S): Mono<S> = Mono.fromFuture(eventualWrite.compute { syncRepo.save(entity) })

    override fun <S : Task> saveAll(entities: MutableIterable<S>): Flux<S> =
        Mono.fromFuture(eventualWrite.compute { this.syncRepo.saveAll(entities) }).flatMapMany { Flux.fromIterable(it) }

    @Transactional
    override fun findById(id: Id): Mono<Task> = this.syncRepo.findById_(id).map { Mono.just(it) }.orElseGet { Mono.empty<Task>() }

    override fun existsById(id: Id): Mono<Boolean> = Mono.just(this.syncRepo.existsById(id))

    @Transactional
    override fun findAll(): Flux<Task> = Flux.fromStream(this.syncRepo.findAll().toList().stream())

    @Transactional
    open fun findAll(offset: Long, limit: Long): Flux<Task> = Flux.fromStream(syncRepo.findAll(offset, limit).toList().stream())

    override fun count(): Mono<Long> = Mono.fromSupplier { this.syncRepo.count() }

    override fun deleteAll(): Mono<Void> = Mono.fromRunnable { this.syncRepo.deleteAll() }

    override fun deleteAll(entities: MutableIterable<Task>): Mono<Void> = Mono.fromRunnable { this.syncRepo.deleteAll(entities) }

    override fun deleteAllById(ids: MutableIterable<Id>): Mono<Void> = Mono.fromRunnable { this.syncRepo.deleteAllById(ids) }

    override fun delete(entity: Task): Mono<Void> = Mono.fromRunnable { this.syncRepo.delete(entity) }

    override fun deleteById(id: Id): Mono<Void> = Mono.fromRunnable { this.syncRepo.deleteById(id) }

    @Transactional
    override fun findAllById(ids: MutableIterable<Id>): Flux<Task> = Flux.fromStream(this.syncRepo.findAllById(ids).toList().stream())

    override fun <S : Task?> saveAll(entityStream: Publisher<S>): Flux<S> = TODO("Not yet implemented")
    override fun findById(id: Publisher<Id>): Mono<Task> = TODO("Not yet implemented")
    override fun existsById(id: Publisher<Id>): Mono<Boolean> = TODO("Not yet implemented")
    override fun deleteAll(entityStream: Publisher<out Task>): Mono<Void> = TODO("Not yet implemented")
    override fun deleteById(id: Publisher<Id>): Mono<Void> = TODO("Not yet implemented")
    override fun findAllById(idStream: Publisher<Id>): Flux<Task> = TODO("Not yet implemented")

    open fun nextToProcess(): Mono<Task> = Mono.fromSupplier { this.syncRepo.nextToProcess().nullable() }
    open fun lockNextToProcess(): Mono<Task> = Mono.fromSupplier { this.syncRepo.lockNextToProcess().nullable() }

}

