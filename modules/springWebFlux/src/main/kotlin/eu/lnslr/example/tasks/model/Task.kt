package eu.lnslr.example.tasks.model

import eu.lnslr.example.schema.TasksNs
import eu.lunisolar.lava.lang.utils.strings.OptStr.str
import java.time.LocalDateTime
import java.util.*

public class Task(
    var id: String?,

    var input: String?,
    var pattern: String?,

    var status: Status?,
    var progress: String?,
    var result: Result?,

    var createdAt: LocalDateTime?,
    var updatedAt: LocalDateTime?

) {

    constructor(input: String?, pattern: String?) : this(null, input, pattern, Status.CREATED, null, null, null, null)

    override fun toString(): String {
        return "Task(id=$id, status=$status, progress=$progress)"
    }


    companion object TasksIdentity {

        private const val idPrefix = TasksNs.`Task$` + "_";

        fun newId(): String = idPrefix + UUID.randomUUID()
        fun simpleId(id: String?) = str(id).removeStart(idPrefix).value()
        fun fullId(id: String?) = str(id).prependOrVoid(idPrefix).value()
    }
}


enum class Status {
    CREATED, RUNNING, FINISHED
}

public class Result(
    val position: Int?,
    val typos: Int?,
)