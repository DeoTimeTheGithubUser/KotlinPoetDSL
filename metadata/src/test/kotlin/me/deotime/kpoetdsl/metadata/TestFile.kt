@file:JvmName("TestMetadataFile")

package me.deotime.kpoetdsl.metadata

object Test {

    suspend inline fun test(): String {
        println("Hello")
        return "ok"
    }

}

enum class EnumThings {
    Thing,
    Other,
    Awesome
}