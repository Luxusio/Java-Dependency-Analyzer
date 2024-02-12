package io.luxuis.jda

class ClassMethodDependency(
    val classMethod: ClassMethod,
    val calledBy: List<ClassMethodDependency>,
) {
    val isLeaf: Boolean = calledBy.isEmpty()

    fun leafs(): List<ClassMethod> =
        if (isLeaf) {
            listOf(classMethod)
        } else {
            calledBy.flatMap { it.leafs() }
        }

    fun print(
        print: (String) -> Unit = { println(it) },
        indent: String = "",
        additional: ClassMethodDependency.() -> String = { "" }
    ) {
        print("$indent->${classMethod.className}#${classMethod.simpleMethodName}${additional(this)}")
        calledBy.forEach { it.print(print, indent, additional) }
    }
}
