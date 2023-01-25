package me.deotime.kpoetdsl.metadata

import kotlinx.metadata.Flag
import kotlinx.metadata.Flags
import kotlinx.metadata.KmClass
import kotlinx.metadata.KmConstructor
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ExperimentalKotlinPoetDSL
import me.deotime.kpoetdsl.FunctionBuilder
import me.deotime.kpoetdsl.PropertyBuilder.Initializer.invoke
import me.deotime.kpoetdsl.TypeBuilder
import me.deotime.kpoetdsl.TypeKind

@ExperimentalKotlinPoetDSL
fun KmConstructor.toSpec() = let { km ->
    FunctionBuilder {
        constructor()
        km.valueParameters.forEach { +it.toSpec() }
        modifiers(km.flags.toStandardModifiers())
    }
}

@ExperimentalKotlinPoetDSL
fun KmClass.toSpec() = let { km ->
    TypeBuilder.cozy(km.flags.toTypeKind()).buildWith {
        name(km.name.split("/").last())
        modifiers(flags.toClassModifiers())
        km.functions.forEach { +it.toSpec() }
        km.constructors.forEach {
            val spec = it.toSpec()
            if (!Flag.Constructor.IS_SECONDARY(it.flags)) {
                source.primaryConstructor(spec)
                val paramNames = spec.parameters.map { it.name }
                km.properties.forEach {
                    // good api design kotlinpoet!
                    val prop = it.toSpec()
                    if (it.name in paramNames) +prop {
                        initializer {
                            +it.name
                        }
                    } else +prop
                }
            }
            else {
                +spec
                km.properties.forEach { +it.toSpec() }
            }
        }
        typeParameters {
            +km.typeParameters.map { it.asTypeName() }
        }

        if (this is TypeBuilder.Enum) {
            km.enumEntries.forEach(this::entry)
            km.supertypes.filterNot { it.rawName.startsWith("kotlin.Enum") }.forEach { implement(it.asTypeName()) }
        } else {
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

fun Flags.toTypeKind() = with(TypeKind.Scope) {
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