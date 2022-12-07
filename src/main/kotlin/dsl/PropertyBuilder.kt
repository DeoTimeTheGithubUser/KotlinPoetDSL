package dsl

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.jvm.transient
import com.squareup.kotlinpoet.jvm.volatile
import dsl.utils.Assembler
import dsl.utils.buildWith
import dsl.utils.withRequired

class PropertyBuilder(private val cozy: Cozy<PropertyBuilder> = Cozy()) :
    Attributes.Cozied<PropertyBuilder>(cozy),
    Attributes.Sourced<PropertySpec.Builder>,
    Attributes.Buildable<PropertySpec> by Attributes.buildWith(cozy, PropertySpec.Builder::build),
    Attributes.Has.Type by Attributes.typeHolder(cozy),
    Attributes.Has.Type.Parameters by Attributes.parameterizedTypeVisitor(cozy, PropertySpec.Builder::typeVariables),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = PropertySpec.Builder::modifiers,
        annotations = PropertySpec.Builder::annotations
    ) {

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

    fun initializer(assembler: Assembler<CodeBuilder>) {
        source.initializer(CodeBuilder().buildWith(assembler))
    }

    fun delegate(assembler: Assembler<CodeBuilder>) {
        source.delegate(CodeBuilder().buildWith(assembler))
    }

    fun getter(assembler: Assembler<FunctionBuilder>) {
        source.getter(FunctionBuilder().apply { getter() }.buildWith(assembler))
    }

    fun setter(assembler: Assembler<FunctionBuilder>) {
        source.setter(FunctionBuilder().apply { setter() }.buildWith(assembler))
    }
}