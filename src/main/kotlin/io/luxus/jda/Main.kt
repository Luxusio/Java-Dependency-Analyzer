package io.luxus.jda

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Handle
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarInputStream


fun main(args: Array<String>) {
    if (args.size != 4) {
        println("Usage: java -jar <programFileName> <jarPath> <packagePrefix> <classPath> <methodName>")
        println("Example: java -jar jda.jar SampleJar/build/libs io/luxus/sample/include io/luxus/sample/include/MyVisibleRepository doSomething()")
        return
    }

    findDependencies(
        args[0],
        args[1],
        args[2],
        args[3]
    ).forEach { it.print() }
}

fun findDependencies(
    path: String,
    packagePrefix: String,
    classPath: String,
    methodName: String
): List<ClassMethodDependency> {
    val jarFiles = File(path).allFiles.filter { it.extension in setOf("jar", "war") }
    val classNodes = jarFiles.flatMap { it.readJar() }
    val classMethods: List<ClassMethod> = classNodes.toClassMethods()
    val classMethodMap: Map<Triple<String, String, String>, ClassMethod> =
        classMethods.associateBy { Triple(it.classNode.name, it.methodNode.name, it.methodNode.desc) }
    val childrenMap: Map<String, List<String>> = getChildrenMap(classNodes)
    val callMap = getCallMap(classMethods, classMethodMap, childrenMap) { it.startsWith(packagePrefix) }
    val dependencyMap = getDependenciesMap(callMap)

    val methods = classMethods.filter { it.classPath == classPath && it.methodName.startsWith(methodName) }
    val dependencies = methods.map { findDependencyGraph(it, dependencyMap) }
    return dependencies
}

/**
 * read jar file and return ClassNodes
 */
fun File.readJar(): List<ClassNode> {
    this.inputStream().use { fis ->
        JarInputStream(fis).use { jis ->
            val classNodes = mutableListOf<ClassNode>()

            var jarEntry: JarEntry? = jis.nextJarEntry
            while (jarEntry != null) {
                if (jarEntry.name.endsWith(".class")) {
                    classNodes.add(ClassNode().apply {
                        ClassReader(jis).accept(this, 0)
                    })
                }
                jarEntry = jis.nextJarEntry
            }

            return classNodes.toList()
        }
    }
}

/**
 * returns method name with full-parameter path
 */
fun toMethodNameWithParams(methodName: String, methodDescriptor: String): String {
    val methodType = Type.getMethodType(methodDescriptor)
    val parameterSignatures = methodType.argumentTypes.joinToString(",") { it.className }
    return "$methodName($parameterSignatures)"
}

fun List<ClassNode>.toClassMethods(): List<ClassMethod> =
    this.flatMap { classNode ->
        classNode.methods.map { methodNode ->
            ClassMethod(classNode, methodNode)
        }
    }

fun getChildrenMap(classNodes: List<ClassNode>): Map<String, List<String>> {
    val childrenMap = mutableMapOf<String, MutableList<String>>()

    classNodes.forEach { classNode ->
        classNode.superName?.let { superName ->
            childrenMap.getOrPut(superName) { mutableListOf() }.add(classNode.name)
        }
        classNode.interfaces.forEach { interfaceName ->
            childrenMap.getOrPut(interfaceName) { mutableListOf() }.add(classNode.name)
        }
    }

    return childrenMap.mapValues { it.value.toList() }
}

fun getCallMap(
    classMethods: List<ClassMethod>,
    classMethodMap: Map<Triple<String, String, String>, ClassMethod>,
    childrenMap: Map<String, List<String>>,
    packageFilter: (String) -> Boolean,
): Map<ClassMethod, List<ClassMethod>> = classMethods.associateWith { classMethod ->
    val calls = mutableListOf<ClassMethod>()
    fun addCalls(owner: String, name: String, desc: String) {
        val classMethod = classMethodMap[Triple(owner, name, desc)]
        if (classMethod == null) {
            println("Not found: $owner, $name, $desc")
            return // not found
        }

        calls.add(classMethod)

        childrenMap[classMethod.classPath]?.let { children ->
            calls.addAll(children.mapNotNull {
                classMethodMap[Triple(
                    it,
                    classMethod.methodNode.name,
                    classMethod.methodNode.desc
                )]
            })
        }
    }

    classMethod.methodNode.instructions.forEach { instruction ->
        if (instruction is InvokeDynamicInsnNode) { // invokeDynamic (=Lambda)
            val methodHandle = instruction.bsmArgs[1] as Handle
            addCalls(methodHandle.owner, methodHandle.name, methodHandle.desc)
        } else if (instruction is MethodInsnNode) { // methodInsn (=just call)
            if (packageFilter(instruction.owner)) {
                addCalls(instruction.owner, instruction.name, instruction.desc)
            }
        }
    }

    calls.toList()
}

fun getDependenciesMap(
    callMap: Map<ClassMethod, List<ClassMethod>>
): Map<ClassMethod, List<ClassMethod>> {
    val dependencyMap = mutableMapOf<ClassMethod, MutableList<ClassMethod>>()

    callMap.forEach { (classMethod, calls) ->
        calls.forEach { call ->
            dependencyMap.getOrPut(call) { mutableListOf() }.add(classMethod)
        }
    }

    return dependencyMap.mapValues { it.value.toList() }
}

fun findDependencyGraph(
    classMethod: ClassMethod,
    dependencyMap: Map<ClassMethod, List<ClassMethod>>
): ClassMethodDependency {
    return ClassMethodDependency(
        classMethod,
        dependencyMap[classMethod]?.map { findDependencyGraph(it, dependencyMap) } ?: listOf()
    )
}
