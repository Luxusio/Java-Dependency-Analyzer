package io.luxus.jda

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainOnly
import io.kotest.matchers.shouldBe

class MainKtTest : DescribeSpec({

    describe("findDependencies") {
        it("should find valid dependencies") {
            val dependencies = findDependencies(
                path = "SampleJar/build/libs",
                packagePrefix = "io/luxus/sample/include",
                classPath = "io/luxus/sample/include/MyVisibleRepository",
                methodName = "doSomething()",
            )

            dependencies.size shouldBe 1
            dependencies[0].classMethod.classPath shouldBe "io/luxus/sample/include/MyVisibleRepository"
            dependencies[0].classMethod.simpleMethodName shouldBe "doSomething()"
            dependencies[0].calledBy.size shouldBe 2
            dependencies[0].calledBy.map { it.classMethod.classPath } shouldContainOnly listOf("io/luxus/sample/include/MyServiceImpl")
            dependencies[0].calledBy.map { it.classMethod.simpleMethodName } shouldContainAll listOf(
                "doSomething(Instance,int,String)",
                "foo()"
            )
        }
    }


})
