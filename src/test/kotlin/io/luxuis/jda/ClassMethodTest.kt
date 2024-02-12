package io.luxuis.jda

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class ClassMethodTest : DescribeSpec({
    describe("className") {
        context("classPath with package name") {
            val classMethod = ClassMethod(
                "com/example/ClassName",
                "method()"
            )

            it("returns only className") {
                classMethod.className shouldBe "ClassName"
            }
        }

        context("classPath only class name") {
            val classMethod = ClassMethod(
                "ClassName",
                "method()"
            )

            it("returns only className") {
                classMethod.className shouldBe "ClassName"
            }
        }
    }

    describe("simpleMethodName") {
        context("method name without round brackets") {
            val classMethod = ClassMethod(
                "ClassName",
                "method"
            )

            it("returns itself") {
                classMethod.simpleMethodName shouldBe "method"
            }
        }

        context("method name without variables") {
            val classMethod = ClassMethod(
                "ClassName",
                "method()"
            )

            it("returns as same as itself") {
                classMethod.simpleMethodName shouldBe "method()"
            }
        }

        context("method name with variables") {
            val classMethod = ClassMethod(
                "ClassName",
                "method(com.example.Foo, com.example.Poo, int)"
            )

            it("returns only class name variables") {
                classMethod.simpleMethodName shouldBe "method(Foo, Poo, int)"
            }
        }
    }
})
