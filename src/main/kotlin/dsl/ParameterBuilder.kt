package dsl

import com.squareup.kotlinpoet.ParameterSpec
import dsl.utils.Assembler
import dsl.utils.buildWith
import dsl.utils.withRequired

class ParameterBuilder(private val cozy: Cozy<ParameterBuilder> = Cozy()) :
    Attributes.Cozied<ParameterBuilder>(cozy),
    Attributes.Sourced<ParameterSpec.Builder>,
    Attributes.Buildable<ParameterSpec> by Attributes.buildWith(cozy, ParameterSpec.Builder::build),
    Attributes.Has.Type by Attributes.typeHolder(cozy),
    Attributes.Has.Documentation by Attributes.documentationVisitor(cozy, ParameterSpec.Builder::addKdoc),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = ParameterSpec.Builder::modifiers,
        annotations = ParameterSpec.Builder::annotations,
    ) {

    override val source by withRequired { ParameterSpec.builder(name, type) }

    fun default(assembler: Assembler<CodeBuilder>) {
        source.defaultValue(CodeBuilder().buildWith(assembler))
    }
}