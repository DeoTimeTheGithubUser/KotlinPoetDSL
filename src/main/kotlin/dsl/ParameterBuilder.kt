package dsl

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

class ParameterBuilder :
    Buildable<ParameterSpec>,
    Attributes.Typed by Attributes.typedHolder(),
    Attributes.Property<ParameterSpec.Builder> by Attributes.property(
        modifiers = ParameterSpec.Builder::modifiers,
        annotations = ParameterSpec.Builder::annotations
    ) {

    override val source by lazy { ParameterSpec.builder(name, type) }
    override fun build() = source.build()

}