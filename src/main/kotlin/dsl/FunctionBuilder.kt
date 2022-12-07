package dsl

import com.squareup.kotlinpoet.FunSpec
import dsl.utils.buildWith
import dsl.utils.withRequired

class FunctionBuilder :
    Attributes.Buildable<FunSpec>,
    Attributes.Has.Code by Attributes.codeAdder(FunSpec.Builder::addCode),
    Attributes.Property<FunctionBuilder, FunSpec.Builder> by Attributes.property(
        modifiers = FunSpec.Builder::modifiers,
        annotations = FunSpec.Builder::annotations,
        identity = FunctionBuilder::identity
    ) {

    override fun identity() = this
    override val source by withRequired { FunSpec.builder(name) }

    override fun build() = source.build()

    inline fun parameter(closure: ParameterBuilder.() -> Unit) {
        source.addParameter(ParameterBuilder().buildWith(closure))
    }
}