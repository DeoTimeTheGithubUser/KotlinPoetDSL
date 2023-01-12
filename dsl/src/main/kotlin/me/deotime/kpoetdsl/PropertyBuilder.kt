package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.jvm.transient
import com.squareup.kotlinpoet.jvm.volatile
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired

@KotlinPoetDsl
class PropertyBuilder private constructor(private val cozy: Cozy<PropertyBuilder>) :
    Attributes.Sourced<PropertySpec.Builder>,
    Attributes.Buildable<PropertySpec> by Attributes.buildWith(cozy, PropertySpec.Builder::build),
    Attributes.Has.Type by Attributes.typeHolder(cozy),
    Attributes.Has.Type.Parameters by Attributes.parameterizedTypeVisitor(cozy, PropertySpec.Builder::typeVariables),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = PropertySpec.Builder::modifiers,
        annotations = PropertySpec.Builder::annotations
    ),
    Required.Holder by requiredHolder(),
    Maybe<PropertySpec.Builder> by maybe() {

    override val source by withRequired { PropertySpec.builder(name, type) }

    fun mutable() {
        source.mutable(true)
    }

    fun immutable() {
        source.mutable(false)
    }

    fun transient() {
        source.transient()
    }

    fun volatile() {
        source.volatile()
    }

    @KotlinPoetDsl
    fun initializer(assembler: Assembler<CodeBuilder>) {
        source.initializer(CodeBuilder.cozy().buildWith(assembler))
    }

    @KotlinPoetDsl
    fun delegate(assembler: Assembler<CodeBuilder>) {
        source.delegate(CodeBuilder.cozy().buildWith(assembler))
    }

    @KotlinPoetDsl
    fun getter(assembler: Assembler<FunctionBuilder>) {
        source.getter(FunctionBuilder.cozy().apply { getter() }.buildWith(assembler))
    }

    @KotlinPoetDsl
    fun setter(assembler: Assembler<FunctionBuilder>) {
        source.setter(FunctionBuilder.cozy().apply { setter() }.buildWith(assembler))
    }

    companion object Initializer :
        Cozy.Initializer.Simple<PropertyBuilder> by cozied(::PropertyBuilder),
        Crumple<PropertySpec, PropertyBuilder> by unstableMaybeCozyCrumple({ Initializer }, PropertySpec::toBuilder)
}