package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import me.deotime.kpoetdsl.KotlinCode
import java.io.PrintWriter

fun SymbolProcessor.write(env: SymbolProcessorEnvironment, code: KotlinCode) = env.codeGenerator
    .createNewFile(Dependencies.ALL_FILES, code.source.packageName, code.source.name)
    .use { output ->
        PrintWriter(output).use { printer ->
            printer.write("$code")
        }
    }