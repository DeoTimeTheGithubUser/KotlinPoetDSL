@file:JvmName("TestMetadataFile")

package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.typeNameOf

abstract class Test<T, V : Runnable>(
    val weight: Double
) {

    constructor(weight: Number) : this(weight.toDouble())

    suspend inline fun test(amount: String?): String {
        println("Hello")
        return "ok"
    }

    fun accept(closure: context(Short, Char) Double.(String, Int) -> Boolean) {

    }

}

interface Other : Runnable {

}