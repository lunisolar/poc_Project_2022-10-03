package eu.lnslr.example.executor

import eu.lnslr.example.tasks.SyncTaskRepository
import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.magma.func.supp.opt.Opt
import org.springframework.transaction.annotation.Transactional

open class TasksClient(private val tasks: SyncTaskRepository /*in place of REST call to repository*/) {

    @Transactional(readOnly = false)
    open fun next(): Opt<Task> = tasks.lockNextToProcess();

    @Transactional(readOnly = false)
    open fun save(task: Task): Task = tasks.save(task);

}