package eu.lnslr.example.tasks

import eu.lnslr.example.schema.TasksNs
import eu.lnslr.example.schema.TasksRs
import eu.lnslr.example.tasks.model.Result
import eu.lnslr.example.tasks.model.Status
import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.lava.data.types.xsd.XS
import eu.lunisolar.lava.lang.seq.Seq
import eu.lunisolar.lava.lang.time.AnyDateTime
import eu.lunisolar.lava.rdf.api.DefaultRdf.rdf
import eu.lunisolar.lava.rdf.api.data.Id
import eu.lunisolar.lava.rdf.api.parts.spo.AsO
import eu.lunisolar.lava.rdf.api.schema.RdfRs
import eu.lunisolar.lava.rdf.api.t3.ModelAspect
import eu.lunisolar.lava.rdf.api.t4.Dataset
import eu.lunisolar.lava.rdf.spring.RdfRepository
import eu.lunisolar.magma.basics.exceptions.X
import eu.lunisolar.magma.func.supp.check.Checks.arg
import eu.lunisolar.magma.func.supp.opt.Opt
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


/**
 * Repository assumed to work on non-remote triplestore.
 *
 */
// Made public only as a replacement for REST client.
open class SyncTaskRepository(private val dataset: Dataset) : RdfRepository<Task>, Logging {

    private fun exists(id: Id) = myGraph().contains().s(id).p(RdfRs.type).o()
    private fun existsAndOfType(id: Id) = myGraph().contains().s(id).p(RdfRs.type).a(TasksRs.Task)

    private fun myGraph() = dataset.graph(TasksRs.TasksGraph).orElseThrow(X::state)

    override fun <S : Task> save(entity: S) = entity.apply {
        val now = LocalDateTime.now()

        if (entity.id == null) entity.id = Task.newId()

        // TODO time logic should not really be part of repository
        if (entity.createdAt == null) entity.createdAt = now
        entity.updatedAt = now

        ModelAspect.modelAspect(myGraph()).resource(entity.id!!)
            .update().p(RdfRs.type).a(TasksRs.Task)

            .update().p(TasksRs.input).v(entity.input)
            .update().p(TasksRs.pattern).v(entity.pattern)

            .update().p(TasksRs.status).v(entity.status)
            .update().p(TasksRs.progress).v(entity.progress)
            .update().p(TasksRs.resultPosition).v(entity.result?.position)
            .update().p(TasksRs.resultTypos).v(entity.result?.typos)

            .update().p(TasksRs.createdAt).v(AnyDateTime.of(entity.createdAt!!))
            .update().p(TasksRs.updatedAt).v(AnyDateTime.of(entity.updatedAt!!))
    }

    override fun existsById(id: Id) = existsAndOfType(id)

    override fun count() = myGraph().count().s().p(RdfRs.type).a(TasksRs.Task)


    override fun deleteById(id: Id) {
        if (exists(id)) {
            arg(id).must(this::existsAndOfType, "The resource is not of expected type.")

            myGraph().remove().s(id).p().o();
        }
    }

    override fun delete(entity: Task) = deleteById(rdf().id(entity.id)!!)

    override fun deleteAll(entities: MutableIterable<Task>) {
        super.deleteAll(entities)
    }

    override fun findById_(id: Id): Opt<Task> {
        if (existsById(id)) {
            val prop = ModelAspect.modelAspect(myGraph()).resource(id).properties()

            val position = prop.p(TasksRs.resultPosition).aOneBy(AsO::isRegularInt).nullable()
            val typos = prop.p(TasksRs.resultTypos).aOneBy(AsO::isRegularInt).nullable()

            var result: Result? = null
            if (position != null || typos != null) {
                result = Result(position, typos)
            }



            return Opt.of(
                Task(
                    id.identity(),
                    prop.p(TasksRs.input).aOneBy(AsO::isRegularStr).alt(""),
                    prop.p(TasksRs.pattern).aOneBy(AsO::isRegularStr).alt(""),

                    prop.p(TasksRs.status).aOneBy(AsO::isRegularStr).filterEnum(Status::class.java).nullable(),
                    prop.p(TasksRs.progress).aOneBy(AsO::isRegularStr).alt(null as String?),

                    result,

                    prop.p(TasksRs.createdAt).aOneBy(AsO::isRegular, XS.DATETIME).map(AnyDateTime::toLocal).orElse(null),
                    prop.p(TasksRs.updatedAt).aOneBy(AsO::isRegular, XS.DATETIME).map(AnyDateTime::toLocal).orElse(null)
                )
            )
        }

        return Opt.empty();
    }

    // We have direct access to Dataset, we can query just for Id(s) and read other values later without a cost.
    private val listQuery = rdf().querying().prepared(
        """
        SELECT ?id
        WHERE {
            ?id a <${TasksNs.Task}> . 
            ?id <${TasksNs.createdAt}> ?createdAt .  
        }
        ORDER BY DESC(?createdAt)
    """.trimIndent()
    )

    private fun all(): Seq<Id> {
        var params = rdf().querying().params()
        return dataset.querying()
            // TODO lunisolar-lava: I Thought the proxy issue ws resolved (dataset.optAsDataset().get())
            .execute(listQuery, dataset.unionGraph(), params)
            .stream()
            .map { it.id("id") }
            .map { rdf().id(it)!! }
    }

    private fun allTasks(): Seq<Task> = all().optMap{ findById_(it) }

    private fun allTasks(offset: Long, limit: Long): Seq<Task> = all()
        .skip(arg(offset, "offset").mustBeGreaterEqual(0).value())
        .limit(arg(limit, "limit").mustBeGreaterEqual(1).value())
        .optMap{ findById_(it) }

    @Transactional
    override fun findAll(): Seq<Task> = all().optMap{ findById_(it) }

    @Transactional
    open fun findAll(offset: Long, limit: Long): Seq<Task> = allTasks(offset, limit);


    private val availableToProcess = rdf().querying().prepared(
        """
        SELECT ?id
        WHERE {
            ?id <${TasksNs.status}> "${Status.CREATED}" .
            ?id a <${TasksNs.Task}> .
        }
        ORDER BY ASC(?createdAt)
        LIMIT 1
    """.trimIndent()
    )

    @Transactional
    open fun nextToProcess(): Opt<Task> = this.dataset.querying()
        // TODO lunisolar-lava: I Thought the proxy issue ws resolved (dataset.optAsDataset().get())
        .execute(availableToProcess, dataset.unionGraph(), rdf().querying().params())
        // TODO lunisolar-lava - seems that shortcut for single value is only availabel for non-optional literal cases - add shortcut variants for optionality and Id
        .stream().aOne().map { row -> row.id("id") }
        .flatMap{ findById_(it) }

    private fun lock(task: Task) = save(task.apply { status = Status.RUNNING })

    open fun lockNextToProcess(): Opt<Task> {

//        state(dataset.txFlags().mapToBool { it.isWrite }.orElse(false))
//            .mustBeTrue("Dataset must be inn TX elevated to WRITE prior querying for available tasks.")


        // We use TDB2 limitation to our advantage - only one Writer is allowed. No other synchronization is needed.
        val result = nextToProcess().map { lock(it) }

        logger.debug { "Querying for lockNextToProcess: $result" }

        return result
    }


}