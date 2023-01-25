package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeAliasSpec
import kotlinx.metadata.ExperimentalContextReceivers
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmFunction
import kotlinx.metadata.KmType
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.FunctionBuilder
import me.deotime.kpoetdsl.TypeBuilder
import me.deotime.kpoetdsl.TypeKind

@ExperimentalKotlinPoetDSL
fun KmClass.toSpec() = let { km ->
    TypeBuilder.cozy(km.flags.toTypeKind()).buildWith {
        name(km.name.split("/").last())
        modifiers(flags.toClassModifiers())
        km.properties.forEach { +it.toSpec() }
        km.functions.forEach { +it.toSpec() }
        typeParameters {
            +km.typeParameters.map { it.asTypeName() }
        }

        if (this is TypeBuilder.Enum) {
            km.enumEntries.forEach(this::entry)
        } else {
            println("called?")
            km.supertypes.forEach {
                // There unfortunately does not seem to be a way to check if
                // this is an interface or a class kind, maybe some way of checking
                // could be added at some point

                @Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")
                if (source.isSimpleClass) inherit(it.asTypeName())
                else implement(it.asTypeName())
            }
        }
    }
}

fun Flags.toTypeKind() = with (TypeKind.Scope) {
    val flag = this@toTypeKind
    when {
        Flag.Class.IS_CLASS(flag) -> Class
        Flag.Class.IS_ENUM_CLASS(flag) -> Enum
        Flag.Class.IS_ANNOTATION_CLASS(flag) -> Annotation
        Flag.Class.IS_INTERFACE(flag) -> Interface
        Flag.Class.IS_OBJECT(flag) -> Object
        Flag.Class.IS_FUN(flag) -> Functional
        Flag.Class.IS_VALUE(flag) -> Value
        else -> error("Flag $flag did not match a type kind.")
    }
}