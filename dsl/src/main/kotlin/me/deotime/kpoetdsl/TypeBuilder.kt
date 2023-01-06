package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.TypeKind.Scope.Companion.Enum
import me.deotime.kpoetdsl.TypeKind.Scope.Companion.Unknown
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

sealed class TypeBuilder private constructor(
    private val cozy: Cozy<out TypeBuilder>,
    private val kind: TypeKind<*, *>
) :
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
    Required.Holder by requiredHolder(),
    Maybe<TypeSpec.Builder> by maybe() {

    override val source by withRequired { kind.init(if (kind == TypeKind.Scope.Enum) "no-op" else name) }

    private val primaryConstructor = FunctionBuilder.cozy().apply { constructor() }

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

    class Enum(cozy: Cozy<Enum>) : TypeBuilder(cozy, TypeKind.Scope.Enum) {

        inline val entries get(): Map<String, TypeSpec> = source.enumConstants
        inline fun entry(name: String, assembler: Assembler<TypeBuilder> = { }) {
            val spec = TypeBuilder.cozy(TypeKind.Scope.Enum).buildWith(assembler)
            source.addEnumConstant(name, spec)
        }

        companion object Initializer : Cozy.Initializer.Simple<Enum> by cozied(::Enum)
    }


    private class Normal(cozy: Cozy<out TypeBuilder>, kind: TypeKind<*, *>) : TypeBuilder(cozy, kind)

    companion object Initializer :
        Cozy.Initializer<TypeBuilder, TypeKind<*, *>> by cozied(::Normal),
        Crumple<TypeSpec, TypeBuilder> {
        override fun TypeSpec.invoke(closure: TypeBuilder.() -> Unit) = cozy(TypeKind.Scope.Unknown).apply {
            value = this@invoke.toBuilder()
        }.buildWith(closure)
    }

}

private typealias NormalTypeKind = TypeKind<TypeBuilder, TypeKind.Naming>

@JvmInline
value class TypeKind<T : TypeBuilder, N : TypeKind.Naming> private constructor(val init: (String) -> TypeSpec.Builder) {

    operator fun getValue(ref: Any?, prop: KProperty<*>?) = this

    sealed interface Naming {
        sealed interface None : Naming
    }

    interface Scope {
        companion object : Scope {

            val Scope.Class by NormalTypeKind(TypeSpec.Companion::classBuilder)
            val Scope.Interface by NormalTypeKind(TypeSpec.Companion::interfaceBuilder)
            val Scope.Annotation by NormalTypeKind(TypeSpec.Companion::annotationBuilder)
            val Scope.Object by NormalTypeKind(TypeSpec.Companion::objectBuilder)
            val Scope.Value by NormalTypeKind(TypeSpec.Companion::valueClassBuilder)
            val Scope.Functional by NormalTypeKind(TypeSpec.Companion::funInterfaceBuilder)
            val Scope.Anonymous by TypeKind<TypeBuilder, Naming.None> { TypeSpec.anonymousClassBuilder() }
            val Scope.Enum by TypeKind<TypeBuilder.Enum, Naming>(TypeSpec.Companion::enumBuilder)

            internal val Scope.Unknown by TypeKind<Nothing, Nothing> { error("Unknown kind cannot be created") }
        }
    }
}