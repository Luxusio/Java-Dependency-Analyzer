package io.luxus.jda

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClassMethodTest : DescribeSpec({
    describe("className") {
        context("classPath with package name") {
            val classMethod = methodWithParameter

            it("returns only className") {
                classMethod.className shouldBe "MyService"
            }
        }
    }

    describe("simpleMethodName") {
        context("method name without variables") {
            val classMethod = noParameterMethod

            it("returns as same as itself") {
                classMethod.simpleMethodName shouldBe "foo()"
            }
        }

        context("method name with variables") {
            val classMethod = methodWithParameter

            it("returns only class name variables") {
                classMethod.simpleMethodName shouldBe "doSomething(Instance,int,String)"
            }
        }
    }
})
