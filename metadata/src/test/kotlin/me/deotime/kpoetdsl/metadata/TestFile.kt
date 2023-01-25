@file:JvmName("TestMetadataFile")

package me.deotime.kpoetdsl.metadata

abstract class Test(
    val weight: Double
) {

    constructor(weight: Number) : this(weight.toDouble())

    suspend inline fun test(): String {
        println("Hello")
        return "ok"
    }

}

interface Other : Runnable {

}