package dsl

import com.squareup.kotlinpoet.TypeSpec

class TypeBuilder :
    Buildable<TypeSpec>,
    Attributes.Property<TypeSpec.Builder> by Attributes.property(TypeSpec.Builder::modifiers) {
    private var type: Type? = null

    override val source by lazy {
        type!!.init(if (type == Type.Selector.Anonymous) "no-op" else name)
    }

    // closure here can be replaced when context selectors become
    // non-experimental
    fun kind(selector: (Type.Selector) -> Type) {
        type = selector(Type.Selector)
    }

    override fun build() = source.build()


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