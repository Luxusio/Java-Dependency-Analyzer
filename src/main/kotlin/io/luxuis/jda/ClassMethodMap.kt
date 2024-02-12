package io.luxuis.jda

import org.objectweb.asm.Handle
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InvokeDynamicInsnNode
import org.objectweb.asm.tree.MethodInsnNode

class ClassMethodMap(
    val classNodes: List<ClassNode>,
    val packageFilter: (String) -> Boolean,
) {
    val classMethods: List<ClassMethod> = classNodes.toClassMethods()
    val map: Map<Triple<String, String, String>, ClassMethod> =
        classMethods.associateBy { Triple(it.classNode.name, it.methodNode.name, it.methodNode.desc) }
    val callMap: Map<ClassMethod, List<ClassMethod>> = getCallMap()

    private fun getCallMap(): Map<ClassMethod, List<ClassMethod>> = classMethods.associateWith { classMethod ->
        val calls = mutableListOf<ClassMethod>()

        classMethod.methodNode.instructions.forEach { instruction ->
            if (instruction is InvokeDynamicInsnNode) { // invokeDynamic (=Lambda)
                val methodHandle = instruction.bsmArgs[1] as Handle
                map[Triple(methodHandle.owner, methodHandle.name, methodHandle.desc)]?.let { classMethod ->
                    calls.add(classMethod)

                    classMethod.classNode.superName?.let { superName ->
                        map[Triple(superName, methodHandle.name, methodHandle.desc)]?.let { classMethod ->
                            calls.add(classMethod)
                        }
                    }

                    classMethod.classNode.interfaces.forEach { interfaceName ->
                        map[Triple(interfaceName, methodHandle.name, methodHandle.desc)]?.let { classMethod ->
                            calls.add(classMethod)
                        }
                    }

                }
            } else if (instruction is MethodInsnNode) {
                if (packageFilter(instruction.owner)) {
                    map[Triple(instruction.owner, instruction.name, instruction.desc)]?.let { classMethod ->
                        calls.add(classMethod)
                    }
                }
            }
        }

        calls.toList()
    }

    fun findCalls(classMethod: ClassMethod): List<ClassMethod> = callMap[classMethod] ?: emptyList()

    fun findAllParentMethods(classMethod: ClassMethod): List<ClassMethod> {
        val methods = mutableListOf(classMethod)

        classMethod.classNode.superName?.let { superName ->
            map[Triple(superName, classMethod.methodNode.name, classMethod.methodNode.desc)]?.let {
                methods.addAll(findAllParentMethods(it))
            }
        }

        classMethod.classNode.interfaces.forEach { interfaceName ->
            map[Triple(interfaceName, classMethod.methodNode.name, classMethod.methodNode.desc)]?.let {
                methods.addAll(findAllParentMethods(it))
            }
        }

        return methods.toList()
    }
}

fun List<ClassNode>.toClassMethods(): List<ClassMethod> =
    this.flatMap { classNode ->
        classNode.methods.map { methodNode ->
            ClassMethod(classNode, methodNode)
        }
    }


