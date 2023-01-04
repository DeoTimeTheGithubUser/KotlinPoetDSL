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

sealed class TypeBuilder private constructor(private val cozy: Cozy<out TypeBuilder>) :
    Attributes.Sourced<TypeSpec.Builder>,
    Attributes.Buildable<TypeSpec>,
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, TypeSpec.Builder::funSpecs),
    Attributes.Has.Properties by Attributes.propertiesVisitor(cozy, TypeSpec.Builder::propertySpecs),
    Attributes.Has.Type.Parameters by Attributes.parameterizedTypeVisitor(cozy, TypeSpec.Builder::typeVariables),
    Attributes.Has.Documentation by Attributes.documentationVisitor(cozy, TypeSpec.Builder::addKdoc),
    Attributes.Has.Classes by Attributes.classesVisitor(cozy, TypeSpec.Builder::typeSpecs),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = TypeSpec.Builder::modifiers,
        annotations = TypeSpec.Builder::annotationSpecs,
    ),
    Required.Holder by requiredHolder() {

    override val source by withRequired { kind.init(if (kind == Type.Selector.Anonymous) "no-op" else name) }
    protected open var kind by required<Type>()

    private val primaryConstructor = FunctionBuilder.cozy().apply { constructor() }

    open fun kind(selector: Type.Selector.() -> Type) {
        kind = selector(Type.Selector)
    }

    fun constructor(assembler: Assembler<FunctionBuilder>) {
        source.primaryConstructor(FunctionBuilder.cozy().buildWith(assembler))
    }

    fun constructorProperty(assembler: Assembler<PropertyBuilder>) {
        val prop = PropertyBuilder.cozy().buildWith {
            assembler()
            initializer { +name }
        }
        primaryConstructor.parameter(prop.name) {
            type(prop.type)
        }
        source.addProperty(prop)
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

    override fun build() =
        source.apply {
            primaryConstructor.build().takeIf { it.parameters.isNotEmpty() }?.let { primaryConstructor(it) }
        }.build()

    class Enum(cozy: Cozy<Enum>) : TypeBuilder(cozy) {

        init {
            kind = Type.Selector.Enum
        }

        @Deprecated("Enum cannot have its kind changed.", level = DeprecationLevel.HIDDEN)
        override fun kind(selector: Type.Selector.() -> Type) = error("Enum cannot have its kind changed.")

        inline val entries get(): Map<String, TypeSpec> = source.enumConstants
        inline fun entry(name: String, assembler: Assembler<TypeBuilder> = { kind { Anonymous } }) {
            val cozy = TypeBuilder.cozy()
            val spec = cozy.buildWith(assembler)
            source.addEnumConstant(name, spec)
        }

        companion object Initializer : Cozy.Initializer<Enum> by cozied(::Enum)
    }

    @JvmInline
    value class Type private constructor(val init: (String) -> TypeSpec.Builder) {
        object Selector {
            val Class = Type(TypeSpec.Companion::classBuilder)
            val Interface = Type(TypeSpec.Companion::interfaceBuilder)
            val Annotation = Type(TypeSpec.Companion::annotationBuilder)
            val Object = Type(TypeSpec.Companion::objectBuilder)
            val Value = Type(TypeSpec.Companion::valueClassBuilder)
            val Anonymous = Type { TypeSpec.anonymousClassBuilder() }
            val Functional = Type(TypeSpec.Companion::funInterfaceBuilder)
            internal val Enum = Type(TypeSpec.Companion::enumBuilder)
        }
    }


    private class Normal(cozy: Cozy<out TypeBuilder>) : TypeBuilder(cozy)

    companion object Initializer : Cozy.Initializer<TypeBuilder> by cozied(::Normal)

}