package dsl

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import dsl.utils.withRequired
import kotlin.reflect.KClass

class ParameterBuilder :
    Attributes.Buildable<ParameterSpec> by Attributes.buildWith(ParameterSpec.Builder::build),
    Attributes.Has.Type by Attributes.typedHolder<ParameterBuilder>(),
    Attributes.Property<ParameterBuilder, ParameterSpec.Builder> by Attributes.property(
        modifiers = ParameterSpec.Builder::modifiers,
        annotations = ParameterSpec.Builder::annotations,
        identity = ParameterBuilder::identity
    ) {

    override fun identity() = this
    override val source by withRequired { ParameterSpec.builder(name, type) }

}