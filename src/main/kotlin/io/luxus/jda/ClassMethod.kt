package io.luxus.jda

import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.util.Objects.hash

class ClassMethod(
    val classNode: ClassNode,
    val methodNode: MethodNode
) {
    val classPath: String = classNode.name
    val methodName: String = toMethodNameWithParams(methodNode.name, methodNode.desc)

    val className: String
        get() = classPath.substringAfterLast("/")
    val simpleMethodName: String
        get() {
            val index = methodName.indexOf("(")
            if (index == -1) {
                return methodName
            }

            return methodName.substring(0, index) + "(" +
                    methodName.substring(index + 1, methodName.length - 1)
                        .split(",")
                        .joinToString(",") { it.substringAfterLast(".") } + ")"
        }

    override fun toString(): String = "ClassMethod($classPath, $methodName)"
    override fun hashCode(): Int = hash(classPath, methodName)
    override fun equals(other: Any?): Boolean =
        other is ClassMethod
                && other.classPath == classPath
                && other.methodName == methodName
}
