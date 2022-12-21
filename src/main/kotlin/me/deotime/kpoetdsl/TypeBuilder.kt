package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.required
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired
import kotlin.reflect.KClass

class TypeBuilder private constructor(private val cozy: Cozy<TypeBuilder>) :
    Attributes.Sourced<TypeSpec.Builder>,
    Attributes.Buildable<TypeSpec> by Attributes.buildWith(cozy, TypeSpec.Builder::build),
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, TypeSpec.Builder::addFunction),
    Attributes.Has.Properties by Attributes.propertiesVisitor(cozy, TypeSpec.Builder::propertySpecs),
    Attributes.Has.Type.Parameters by Attributes.parameterizedTypeVisitor(cozy, TypeSpec.Builder::typeVariables),
    Attributes.Has.Documentation by Attributes.documentationVisitor(cozy, TypeSpec.Builder::addKdoc),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = TypeSpec.Builder::modifiers,
        annotations = TypeSpec.Builder::annotationSpecs,
    ),
    Required.Holder by requiredHolder() {

    override val source by withRequired { kind.init(if (kind == Type.Selector.Anonymous) "no-op" else name) }
    private var kind by required<Type>()

    fun kind(selector: Type.Selector.() -> Type) {
        kind = selector(Type.Selector)
    }

    fun constructor(assembler: Assembler<FunctionBuilder>) {
        function {
            constructor()
            assembler()
        }
    }

    fun initializer(assembler: Assembler<CodeBuilder>) {
        source.addInitializerBlock(CodeBuilder.cozy().buildWith(assembler))
    }

    fun inherit(vararg types: KClass<*>) {
        types.forEach(source::superclass)
    }

    fun inherit(vararg types: TypeName) {
        types.forEach(source::superclass)
    }

    inline fun <reified T> inherit() = inherit(T::class)

    fun implement(vararg interfaces: TypeName) {
        interfaces.forEach(source::addSuperinterface)
    }

    fun implement(vararg interfaces: KClass<*>) {
        interfaces.forEach(source::addSuperinterface)
    }

    inline fun <reified T> implement() = implement(T::class.asTypeName())

    @JvmInline
    value class Type private constructor(val init: (String) -> TypeSpec.Builder) {
        object Selector {
            val Class = Type(TypeSpec.Companion::classBuilder)
            val Enum = Type(TypeSpec.Companion::enumBuilder)
            val Interface = Type(TypeSpec.Companion::interfaceBuilder)
            val Annotation = Type(TypeSpec.Companion::annotationBuilder)
            val Object = Type(TypeSpec.Companion::objectBuilder)
            val Value = Type(TypeSpec.Companion::valueClassBuilder)
            val Anonymous = Type { TypeSpec.anonymousClassBuilder() }
            val Functional = Type(TypeSpec.Companion::funInterfaceBuilder)
        }
    }

    companion object Initializer : Cozy.Initializer<TypeBuilder>(::TypeBuilder)

}