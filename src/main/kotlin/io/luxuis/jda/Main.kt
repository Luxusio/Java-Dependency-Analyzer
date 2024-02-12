package io.luxuis.jda

import java.io.File


fun main(args: Array<String>) {


}

fun process(path: String, packagePrefix: String) {
    val jarFiles = File(path).allFiles.filter { it.extension in setOf("jar", "war") }
    val classNodes = jarFiles.flatMap { it.readJar() }


}


