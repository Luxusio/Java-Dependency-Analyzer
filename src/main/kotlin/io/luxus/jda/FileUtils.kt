package io.luxus.jda

import java.io.File

val File.allFiles: List<File>
    get() {
        return if (isDirectory) {
            listFiles()?.flatMap { it.allFiles } ?: listOf()
        } else {
            listOf(this)
        }
    }
