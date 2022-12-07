package dsl

import com.squareup.kotlinpoet.FunSpec
import dsl.utils.Assembler
import dsl.utils.Cozy
import dsl.utils.buildWith
import dsl.utils.withRequired

class FunctionBuilder(private val cozy: Cozy<FunctionBuilder> = Cozy()) :
    Attributes.Cozied<FunctionBuilder>(cozy),
    Attributes.Sourced<FunSpec.Builder>,
    Attributes.Buildable<FunSpec> by Attributes.buildWith(cozy, FunSpec.Builder::build),
    Attributes.Has.Code by Attributes.codeAdder(cozy, FunSpec.Builder::addCode),
    Attributes.Property<FunctionBuilder, FunSpec.Builder> by Attributes.property(
        cozy = cozy,
        modifiers = FunSpec.Builder::modifiers,
        annotations = FunSpec.Builder::annotations,
    ) {

    override val source by withRequired { FunSpec.builder(name) }
    inline fun parameter(assembler: Assembler<ParameterBuilder>) {
        source.addParameter(ParameterBuilder().buildWith(assembler))
    }
}