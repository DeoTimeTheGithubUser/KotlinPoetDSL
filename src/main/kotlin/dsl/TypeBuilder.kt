package dsl

import com.squareup.kotlinpoet.TypeSpec
import dsl.utils.required
import dsl.utils.withRequired

class TypeBuilder :
    Attributes.Buildable<TypeSpec> by Attributes.buildWith(TypeSpec.Builder::build),
    Attributes.Property<TypeBuilder, TypeSpec.Builder> by Attributes.property(
        modifiers = TypeSpec.Builder::modifiers,
        annotations = TypeSpec.Builder::annotationSpecs,
        identity = TypeBuilder::identity
    ) {

    override fun identity() = this
    override val source by withRequired { kind.init(if (kind == Type.Selector.Anonymous) "no-op" else name) }
    private var kind by required<Type>()

    // closure here can be replaced when context selectors become
    // non-experimental
    fun kind(selector: Type.Selector.() -> Type) {
        kind = selector(Type.Selector)
    }


    @JvmInline
    value class Type private constructor(val init: (String) -> TypeSpec.Builder) {
        object Selector {
            val Class = Type(TypeSpec.Companion::classBuilder)
            val Enum = Type(TypeSpec.Companion::enumBuilder)
            val Interface = Type(TypeSpec.Companion::interfaceBuilder)
            val Annotation = Type(TypeSpec.Companion::annotationBuilder)
            val Object = Type(TypeSpec.Companion::objectBuilder)
            val Value = Type(TypeSpec.Companion::valueClassBuilder)
            val Anonymous = Type({ TypeSpec.anonymousClassBuilder() })
            val Functional = Type(TypeSpec.Companion::funInterfaceBuilder)
        }
    }

}