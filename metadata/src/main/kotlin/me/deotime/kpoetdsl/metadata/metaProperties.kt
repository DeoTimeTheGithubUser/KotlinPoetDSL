package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.Flag
import kotlinx.metadata.KmProperty
import kotlinx.metadata.jvm.getterSignature
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.PropertyBuilder
import me.deotime.kpoetdsl.PropertyBuilder.Initializer.invoke

@ExperimentalKotlinPoetDSL
fun KmProperty.toSpec() = let { km ->
    PropertyBuilder {
        name(km.name)
        type(km.returnType.asTypeName())
        modifiers {
            +km.flags.toStandardModifiers()
        }
    }
}
