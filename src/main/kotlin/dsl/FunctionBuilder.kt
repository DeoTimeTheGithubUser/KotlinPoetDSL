package dsl

import com.squareup.kotlinpoet.FunSpec

class FunctionBuilder :
    Buildable<FunSpec>,
    Attributes.Property<FunSpec.Builder> by Attributes.property(
        modifiers = FunSpec.Builder::modifiers,
        annotations = FunSpec.Builder::annotations
    ) {

    override val source by lazy { FunSpec.builder(name) }

    override fun build() = source.build()

    inline fun parameter(closure: ParameterBuilder.() -> Unit) {
        source.addParameter(ParameterBuilder().buildWith(closure))
    }
}