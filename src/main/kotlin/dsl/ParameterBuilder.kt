package dsl

import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass

class ParameterBuilder :
    Buildable<ParameterSpec>,
    Attributes.Property<ParameterSpec.Builder> by Attributes.property(
        modifiers = ParameterSpec.Builder::modifiers,
        annotations = ParameterSpec.Builder::annotations
    ) {

    override val source by lazy { ParameterSpec.builder(name, type) }

    private lateinit var type: TypeName

    fun type(type: KClass<*>) {
        this.type = type.asTypeName()
    }

    fun type(type: TypeName) {
        this.type = type
    }

    override fun build() = source.build()

}