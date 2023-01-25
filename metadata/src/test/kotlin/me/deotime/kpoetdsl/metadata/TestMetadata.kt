package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.jvm.KotlinClassMetadata
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.kotlin
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles

const val TestFileName = "TestMetadataFile"

@OptIn(ExperimentalKotlinPoetDSL::class)
fun main() {
    val self = MethodHandles.lookup().lookupClass()
    val metadata = Class.forName("${self.packageName}.$TestFileName").annotations.toList().filterIsInstance<Metadata>().firstOrNull() ?: error("Couldn't find metadata")
    val parsed = KotlinClassMetadata.read(metadata) as KotlinClassMetadata.FileFacade
    val code = kotlin {
        name("Test") packaged "idk"
        parsed.toKmPackage().functions.map {
            +it.toSpec()
        }
    }
    println(code)

}