package eu.lnslr.example.executor

import eu.lnslr.example.tasks.model.Result
import eu.lnslr.example.tasks.model.Task
import eu.lunisolar.magma.func.function.LFunction
import eu.lunisolar.magma.func.function.LFunction.func
import eu.lunisolar.magma.func.supp.check.Checks.attest
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments.arguments
import org.junit.jupiter.params.provider.MethodSource

internal class TaskSolutionTest {

    val dumyUpdater = { _: Int, _: Int -> }
    val position: LFunction<Result, Int> = func { r: Result -> r.position }
    val typos: LFunction<Result, Int> = func { r: Result -> r.typos }

    companion object {
        @JvmStatic
        fun data() = listOf(
            arguments("123", "123", 0, 0),
            arguments("", "", -1, 0),
            arguments("a", "a", 0, 0),
            arguments("aa", "a", 0, 0),
            arguments("a", "aa", -1, 0),
            arguments("ab", "aa", 0, 1),
            arguments("ba", "aa", 0, 1),
            arguments("bab", "aaa", 0, 2),
            arguments("bbb", "aaa", -1, 0),
            arguments("bba", "aa", 1, 1),

            arguments("bba_baa", "aaa", 4, 1), // looks for better result

            arguments("bba_baa", "", -1, 0),
            arguments(null, "a", -1, 0),
            arguments("a", null, -1, 0),
            arguments(null, null, -1, 0),
        )
    }

    @ParameterizedTest(name = "input {0} and pattern {1} should produce pos={2} typos={3}")
    @MethodSource("data")
    fun fullMatch(input: String?, pattern: String?, expectedPosition: Int, expectedTypos: Int) {

        attest(TaskSolution().process(Task(input, pattern), dumyUpdater))
            .check(position) { pos -> pos.mustBeEqual(expectedPosition) }
            .check(typos) { pos -> pos.mustBeEqual(expectedTypos) }

    }


}

