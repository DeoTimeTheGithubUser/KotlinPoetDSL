@file:JvmName("TestMetadataFile")

package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.typeNameOf

abstract class Test<T, V : Runnable>(
    val weight: Double
) {

    val yes = 55
    val ok = "asd"

    constructor(weight: Number) : this(weight.toDouble())

    suspend inline fun test(amount: String?): String {
        println("Hello")
        return "ok"
    }

}

interface Other : Runnable {

}