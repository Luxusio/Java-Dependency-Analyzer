package io.luxuis.jda

import org.objectweb.asm.ClassReader
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import java.io.File
import java.util.jar.JarEntry
import java.util.jar.JarInputStream


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


