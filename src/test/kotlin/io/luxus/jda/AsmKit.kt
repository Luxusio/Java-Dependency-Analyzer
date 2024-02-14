package io.luxus.jda

import org.objectweb.asm.tree.ClassNode
import java.io.File

val classNodes: List<ClassNode> by lazy {
    File("SampleJar/build/libs").allFiles.flatMap { it.readJar() }
}
