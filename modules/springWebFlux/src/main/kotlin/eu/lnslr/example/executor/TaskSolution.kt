package eu.lnslr.example.executor

import eu.lnslr.example.tasks.model.Task
import eu.lnslr.example.tasks.model.Result

class TaskSolution {

    fun notFound(): Result = Result(-1, 0)

    fun process(task: Task, update: (position: Int, max: Int) -> Unit): Result {

        if (task.input == null || task.pattern == null) return notFound()
        val input = task.input!!;
        val pattern = task.pattern!!;
        val lastPossibleIndex = input.length - pattern.length
        if (lastPossibleIndex < 0 || lastPossibleIndex == input.length) return notFound()

        var bestMatchingChars = 0
        var bestMatchingIndex = -1

        for (i in 0..lastPossibleIndex) {
            update(i, lastPossibleIndex)

            var matchingChars = 0

            for (p in pattern.indices) {
                if (input[i + p] == pattern[p]) matchingChars++
            }

            if (bestMatchingChars < matchingChars) {
                bestMatchingIndex = i
                bestMatchingChars = matchingChars
            }
        }

        return Result(bestMatchingIndex, if (bestMatchingIndex >= 0) pattern.length - bestMatchingChars else 0)
    }

}