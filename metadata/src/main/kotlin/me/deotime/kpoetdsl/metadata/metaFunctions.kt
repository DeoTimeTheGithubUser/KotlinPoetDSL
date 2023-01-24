package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.KmFunction
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.FunctionBuilder

@OptIn(ExperimentalContextReceivers::class)
@ExperimentalKotlinPoetDSL
fun KmFunction.toSpec() = let { km ->
    FunctionBuilder {
        name(km.name)
        returns(km.returnType.asTypeName())
        modifiers(km.flags.toStandardModifiers())
        km.valueParameters.forEach { +it.toSpec() }
        km.receiverParameterType?.let { receiver(it.asTypeName()) }
        context(km.contextReceiverTypes.map { it.asTypeName() })
    }
}