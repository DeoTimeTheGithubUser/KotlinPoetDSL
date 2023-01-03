package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.ParameterSpec
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired

class ParameterBuilder private constructor(private val cozy: Cozy<ParameterBuilder>) :
    Attributes.Sourced<ParameterSpec.Builder>,
    Attributes.Buildable<ParameterSpec> by Attributes.buildWith(cozy, ParameterSpec.Builder::build),
    Attributes.Has.Type by Attributes.typeHolder(cozy),
    Attributes.Has.Documentation by Attributes.documentationVisitor(cozy, ParameterSpec.Builder::addKdoc),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = ParameterSpec.Builder::modifiers,
        annotations = ParameterSpec.Builder::annotations,
    ),
    Required.Holder by requiredHolder() {

    override val source by withRequired { ParameterSpec.builder(name, type) }

    fun default(assembler: Assembler<CodeBuilder>) {
        source.defaultValue(CodeBuilder.cozy().buildWith(assembler))
    }

    companion object Initializer : Cozy.Initializer<ParameterBuilder> by cozied(::ParameterBuilder)
}