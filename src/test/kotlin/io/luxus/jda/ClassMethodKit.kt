package io.luxus.jda

val classMethods: List<ClassMethod> by lazy {
    classNodes.flatMap { classNode ->
        classNode.methods.map { methodNode ->
            ClassMethod(classNode, methodNode)
        }
    }
}

val noParameterMethod: ClassMethod by lazy {
    classMethods.first { it.classPath == "io/luxus/sample/include/MyServiceImpl" && it.methodName == "foo()" }
}

val methodWithParameter: ClassMethod by lazy {
    classMethods.first { it.classPath == "io/luxus/sample/include/MyService" && it.methodName.startsWith("doSomething(") }
}
