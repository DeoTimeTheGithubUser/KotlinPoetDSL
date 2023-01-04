package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.TypeSpec

@JvmInline
value class TypeKind<T : TypeBuilder, N : TypeKind.Naming> private constructor(val init: (String) -> TypeSpec.Builder) {

    sealed interface Naming {
        sealed interface None : Naming
    }

    companion object {
        private fun normalKind(init: (String) -> TypeSpec.Builder) =
            lazy(LazyThreadSafetyMode.NONE) { TypeKind<TypeBuilder, Naming>(init) }

        val FileBuilder.Class by normalKind(TypeSpec.Companion::classBuilder)
        val FileBuilder.Interface by normalKind(TypeSpec.Companion::interfaceBuilder)
        val FileBuilder.Annotation by normalKind(TypeSpec.Companion::annotationBuilder)
        val FileBuilder.Object by normalKind(TypeSpec.Companion::objectBuilder)
        val FileBuilder.Value by normalKind(TypeSpec.Companion::valueClassBuilder)
        val FileBuilder.Functional by normalKind(TypeSpec.Companion::funInterfaceBuilder)


        @PublishedApi
        internal val _Anonymous = TypeKind<TypeBuilder, Naming.None> { TypeSpec.anonymousClassBuilder() }
        val FileBuilder.Anonymous get() = _Anonymous

        internal val _Enum = TypeKind<TypeBuilder.Enum, Naming>(TypeSpec.Companion::enumBuilder)
        val FileBuilder.Enum get() = _Enum
    }
}