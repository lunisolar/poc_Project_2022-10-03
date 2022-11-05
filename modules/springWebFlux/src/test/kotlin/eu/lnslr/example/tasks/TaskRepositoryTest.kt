package eu.lnslr.example.tasks

import eu.lnslr.example.schema.TasksRs
import eu.lnslr.example.schema.TasksRs.createdAt
import eu.lnslr.example.schema.TasksRs.input
import eu.lnslr.example.schema.TasksRs.pattern
import eu.lnslr.example.schema.TasksRs.progress
import eu.lnslr.example.schema.TasksRs.updatedAt
import eu.lnslr.example.schema.TasksRs.status
import eu.lnslr.example.tasks.model.Status
import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.lava.data.types.equality.EqualEnough
import eu.lunisolar.lava.data.types.xsd.XS
import eu.lunisolar.lava.rdf.api.DefaultRdf.rdf
import eu.lunisolar.lava.rdf.api.RdfManager
import eu.lunisolar.lava.rdf.api.RdfP
import eu.lunisolar.lava.rdf.api.parts.spo.As
import eu.lunisolar.lava.rdf.api.parts.spo.Spo.g
import eu.lunisolar.lava.rdf.api.schema.RdfRs.type
import eu.lunisolar.lava.rdf.api.t4.Dataset
import eu.lunisolar.magma.asserts.TestFlow.State
import eu.lunisolar.magma.asserts.TestFlow.test
import eu.lunisolar.magma.func.function.LFunction.func
import eu.lunisolar.magma.func.supp.Be
import eu.lunisolar.magma.func.supp.Have
import eu.lunisolar.magma.func.supp.P
import eu.lunisolar.magma.func.supp.check.Checks.Check
import eu.lunisolar.magma.func.supp.check.Checks.attest
import org.junit.jupiter.api.Test
import java.lang.Thread.sleep
import java.time.LocalDateTime

internal class TaskRepositoryTest {

    val graph = TasksRs.TasksGraph
    val longTimeAgo = LocalDateTime.of(1, 1, 1, 1, 1, 1)

    // for simplification we re just to have 1 scenario (no other corner cases)
    @Test
    fun simpleCrudWorks() {

        test().given {
            object : State() {
                val ds = RdfManager.dataset("lava-rdf:jena:memory:/new");
                val repo = SyncTaskRepository(ds)
                var createdTask: Task? = null
                var readTask: Task? = null
            }
        }.step("Create") {
            it.`when` { state ->
                state.createdTask = state.repo.save(Task("123", "abc"))
            }.then { state ->

                attest(state.ds)
                    .mustEx(RdfP::containInCountEx, g().s().p().o(), 6)
                    .mustEx(RdfP::containInCountEx, g(graph).s().p().o(), 6)
                    .mustEx(RdfP::containOneEx, g(graph).s().p(type).a(TasksRs.Task))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(input).v("123"))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(pattern).v("abc"))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(status).v(Status.CREATED))
                    .mustEx(P.haveEx({ ds: Dataset -> RdfP.theObject(ds, g(graph).s().p(createdAt).o()) }, RdfP::dataTypeEx, XS.DATETIME))
                    .mustEx(P.haveEx({ ds: Dataset -> RdfP.theObject(ds, g(graph).s().p(updatedAt).o()) }, RdfP::dataTypeEx, XS.DATETIME))

                val created = state.ds.quads().g(graph).s().p(createdAt).o().theOne(As::aDateTime).toLocal()
                val updated = state.ds.quads().g(graph).s().p(updatedAt).o().theOne(As::aDateTime).toLocal()

                attest(created).must(EqualEnough::equalEnough, updated, "Most be equal to the seconds.")
            }
        }.and("Simulating time progression (only in object state)") { state ->
            state.createdTask!!.createdAt = longTimeAgo
        }.step("Update") {
            it.`when` { state ->
                state.createdTask!!.status = Status.FINISHED
                state.repo.save(state.createdTask!!)
            }.then { state ->
                attest(state.ds)
                    .mustEx(RdfP::containInCountEx, g().s().p().o(), 6)
                    .mustEx(RdfP::containInCountEx, g(graph).s().p().o(), 6)
                    .mustEx(RdfP::containOneEx, g(graph).s().p(type).a(TasksRs.Task))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(input).v("123"))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(pattern).v("abc"))
                    .mustEx(RdfP::containOneEx, g(graph).s().p(status).v(Status.FINISHED.name))  // TODO extension methods for Kotlin to avoid casting (as Int)?
                    .mustEx(P.haveEx({ ds: Dataset -> RdfP.theObject(ds, g(graph).s().p(createdAt).o()) }, RdfP::dataTypeEx, XS.DATETIME))
                    .mustEx(P.haveEx({ ds: Dataset -> RdfP.theObject(ds, g(graph).s().p(updatedAt).o()) }, RdfP::dataTypeEx, XS.DATETIME))

                val created = state.ds.quads().g(graph).s().p(createdAt).o().theOne(As::aDateTime).toLocal()
                val updated = state.ds.quads().g(graph).s().p(updatedAt).o().theOne(As::aDateTime).toLocal()

                attest(created)
                    .mustEx(Be::equalEx, longTimeAgo)
                    .mustNot(EqualEnough::equalEnough, updated, "Update date ")
            }
        }.step("Read") {
            it.`when` { state ->
                // TODO reconsider Rdf.* @Contract( null-> null)
                state.readTask = state.repo.findById_(rdf().id(state.createdTask!!.id!!)!!).orElseThrow()   // TODO Add missing decorations in RdfRepository
            }.then { state ->
                attest(state.readTask)
                    .check(Task::id, Check<String?>::mustBeNotNull)
                    .check(Task::input) { it.mustBeEqual("123") }
                    .check(Task::pattern) { it.mustBeEqual("abc") }
                    .check(Task::status) { it.mustBeEqual(Status.FINISHED) }
                    .check(Task::createdAt) { it.mustBeEqual(longTimeAgo) }
                    .check(Task::updatedAt) { it.mustBeNotNull() }
            }.and { state ->
                attest(state.repo.findAll().count()).mustBeEqual(1)
            }
        }


    }

    @Test
    fun listsWithLimiAndOffset() {

        test().given {
            object : State() {
                val ds = RdfManager.dataset("lava-rdf:jena:memory:/new");
                val repo = SyncTaskRepository(ds)

            }
        }.step("finaAll") {
            it.`when` { state ->
                state.repo.save(Task("1", "abc"))
                sleep(100)
                state.repo.save(Task("2", "abc"))
                sleep(100)
                state.repo.save(Task("3", "abc"))
                sleep(100)
                state.repo.save(Task("4", "abc"))
            }.then { state ->

                attest(
                    state.repo.findAll(0, 3)
                        .map(func(Task::input))
                        .toList()
                )
                    .mustEx(Have::sizeEx, 3)
                    .mustEx(Have::containExactlyEx, arrayOf("4", "3", "2"))

                attest(
                    state.repo.findAll(2, 3)
                        .map(func(Task::input))
                        .toList()
                )
                    .mustEx(Have::sizeEx, 2)
                    .mustEx(Have::containExactlyEx, arrayOf("2", "1"))

            }
        }


    }

}