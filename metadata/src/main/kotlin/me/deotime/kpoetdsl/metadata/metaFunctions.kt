package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.Flag
import kotlinx.metadata.KmFunction
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.FunctionBuilder

fun KmFunction.toSpec() = let { km ->
    FunctionBuilder {
        name(km.name)
        modifiers(km.flags.toModifiers())
    }
}