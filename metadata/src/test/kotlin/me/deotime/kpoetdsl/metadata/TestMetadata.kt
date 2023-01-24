package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.jvm.KotlinClassMetadata
import kotlinx.metadata.jvm.Metadata

val TestMetadata = Metadata(
    2,
    intArrayOf(1, 7, 1),
    arrayOf("\u0000\b\n\u0000\n\u0002\u0010\u0002\n\u0000\u001a\u0006\u0010\u0000\u001a\u00020\u0001\u00a8\u0006\u0002"),
    arrayOf("test", "", "metadata")
)

fun main() {
    val meta = KotlinClassMetadata.read(TestMetadata) as KotlinClassMetadata.FileFacade
    meta.toKmPackage().let {
        it.functions.forEach {

        }
    }
}