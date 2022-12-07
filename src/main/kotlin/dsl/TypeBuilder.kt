package dsl

import com.squareup.kotlinpoet.TypeSpec
import dsl.utils.Cozy
import dsl.utils.required
import dsl.utils.withRequired

class TypeBuilder(private val cozy: Cozy<TypeBuilder> = Cozy()) :
    Attributes.Cozied<TypeBuilder>(cozy),
    Attributes.Sourced<TypeSpec.Builder>,
    Attributes.Buildable<TypeSpec> by Attributes.buildWith(cozy, TypeSpec.Builder::build),
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, TypeSpec.Builder::addFunction),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = TypeSpec.Builder::modifiers,
        annotations = TypeSpec.Builder::annotationSpecs,
    ) {

    override val source by withRequired { kind.init(if (kind == Type.Selector.Anonymous) "no-op" else name) }
    private var kind by required<Type>()

    // closure here can be replaced when context
    // receivers become non-experimental
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