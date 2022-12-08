package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import dsl.utils.Assembler
import dsl.utils.CollectionAssembler
import dsl.utils.Uses
import dsl.utils.buildCollectionTo
import dsl.utils.buildWith
import dsl.utils.requiredByCozy
import kotlin.reflect.KClass

interface Attributes {

    interface Sourced<S> : Attributes {
        val source: S
    }

    fun interface Buildable<T> : Attributes {
        fun build(): T
    }

    interface Property :
        Has.Modifiers,
        Has.Name,
        Has.Annotations


    interface Body :
        Has.Code,
        Has.Comments

    interface Has : Attributes {
        interface Modifiers : Has {
            fun modifiers(assembler: CollectionAssembler<KModifier>)
        }

        interface Name : Has {
            val name: String
            fun name(name: String): Uses.Name
        }

        interface Type : Has {
            val type: TypeName
            fun type(type: TypeName)
            fun type(type: KClass<*>) = type(type.asTypeName())

            fun interface Parameters : Has {
                fun typeParameters(builder: CollectionAssembler<TypeVariableName>)
            }
        }


        interface Annotations : Has {
            fun annotation(assembler: Assembler<AnnotationBuilder>)
        }

        interface Functions : Has {
            fun function(assembler: Assembler<FunctionBuilder>)
            fun function(name: String, assembler: Assembler<FunctionBuilder>)
        }

        interface Properties : Has {
            fun property(assembler: Assembler<PropertyBuilder>)
        }

        interface Code : Has {
            fun code(assembler: Assembler<CodeBuilder>)
            fun code(format: String, vararg args: Any?)
        }

        interface Comments : Has {
            fun comment(format: String, vararg args: Any)
        }

        interface Documentation : Has {
            fun documentation(assembler: Assembler<CodeBuilder>)
            fun documentation(format: String, vararg args: Any)
        }
    }


    companion object {

        internal fun <S> sourcedByCozy(cozy: SourcedCozy<S>): Sourced<S> = object : Sourced<S> {
            private val sourced by cozy
            override val source get() = sourced.source
        }

        internal fun <S> modifierVisitor(
            cozy: SourcedCozy<S>,
            holder: (S) -> MutableCollection<KModifier>
        ): Has.Modifiers =
            object : Has.Modifiers, Sourced<S> by sourcedByCozy(cozy) {
                override fun modifiers(assembler: CollectionAssembler<KModifier>) {
                    buildCollectionTo(holder(source), assembler)
                }
            }

        internal fun <S> parameterizedTypeVisitor(
            cozy: SourcedCozy<S>,
            visitor: (S) -> MutableCollection<TypeVariableName>
        ): Has.Type.Parameters =
            object : Has.Type.Parameters, Sourced<S> by sourcedByCozy(cozy) {
                override fun typeParameters(builder: CollectionAssembler<TypeVariableName>) {
                    buildCollectionTo(visitor(source), builder)
                }
            }

        internal fun <S> annotationVisitor(
            cozy: SourcedCozy<S>,
            holder: (S) -> MutableCollection<AnnotationSpec>
        ): Has.Annotations =
            object : Has.Annotations, Sourced<S> by sourcedByCozy(cozy) {
                override fun annotation(assembler: Assembler<AnnotationBuilder>) {
                    holder(source).add(AnnotationBuilder.cozy().buildWith(assembler))
                }
            }



        internal fun <S> propertiesVisitor(
            cozy: SourcedCozy<S>,
            holder: (S) -> MutableCollection<Any> // some weird stuff in certain specs
        ): Has.Properties =
            object : Has.Properties, Sourced<S> by sourcedByCozy(cozy) {
                override fun property(assembler: Assembler<PropertyBuilder>) {

                    holder(source).add(PropertyBuilder.cozy().buildWith(assembler))
                }
            }

        @JvmName("propertyVisitor_typed")
        internal fun <S> propertiesVisitor(
            cozy: SourcedCozy<S>,
            holder: (S) -> MutableCollection<PropertySpec> // some weird stuff in certain specs
        ) = propertiesVisitor(cozy, holder as ((S) -> MutableCollection<Any>))

        internal fun <S, B> buildWith(cozy: SourcedCozy<S>, holder: (S) -> B): Buildable<B> =
            object : Buildable<B>, Sourced<S> by sourcedByCozy(cozy) {
                override fun build() = holder(source)
            }

        internal fun <S> codeVisitor(cozy: SourcedCozy<S>, visitor: (S, CodeBlock) -> Unit): Has.Code =
            object : Has.Code, Sourced<S> by sourcedByCozy(cozy) {
                override fun code(assembler: Assembler<CodeBuilder>) {
                    visitor(source, CodeBuilder.cozy().buildWith(assembler))
                }

                override fun code(format: String, vararg args: Any?) {
                    visitor(source, CodeBlock.of(format, args))
                }
            }

        internal fun <S> documentationVisitor(
            cozy: SourcedCozy<S>,
            visitor: (S, CodeBlock) -> Unit
        ): Has.Documentation =
            object : Has.Documentation, Sourced<S> by sourcedByCozy(cozy) {
                override fun documentation(assembler: Assembler<CodeBuilder>) {
                    visitor(source, CodeBuilder.cozy().buildWith(assembler))
                }

                override fun documentation(format: String, vararg args: Any) {
                    visitor(source, CodeBlock.of(format, args))
                }
            }

        internal fun <S> commentVisitor(
            cozy: SourcedCozy<S>,
            visitor: (S, String, Array<out Any>) -> Unit
        ): Has.Comments =
            object : Has.Comments, Sourced<S> by sourcedByCozy(cozy) {
                override fun comment(format: String, vararg args: Any) {
                    visitor(source, format, args)
                }
            }

        internal fun <S> functionVisitor(cozy: SourcedCozy<S>, visitor: (S, FunSpec) -> Unit): Has.Functions =
            object : Has.Functions, Sourced<S> by sourcedByCozy(cozy) {
                override fun function(assembler: Assembler<FunctionBuilder>) {
                    visitor(source, FunctionBuilder.cozy().buildWith(assembler))
                }

                override fun function(name: String, assembler: Assembler<FunctionBuilder>) {
                    visitor(source, FunctionBuilder.cozy().apply { name(name) }.buildWith(assembler))
                }
            }

        internal fun nameHolder(cozy: Cozy<*>): Has.Name = object : Has.Name {
            override var name by requiredByCozy<String>(cozy)
            override fun name(name: String): Uses.Name {
                this.name = name
                return Uses.Name
            }
        }

        internal fun typeHolder(cozy: Cozy<*>): Has.Type = object : Has.Type {
            override var type by requiredByCozy<TypeName>(cozy)
            override fun type(type: TypeName) {
                this.type = type
            }
        }

        internal fun <S> body(
            cozy: SourcedCozy<S>,
            codeVisitor: (S, CodeBlock) -> Unit,
            commentVisitor: (S, String, Array<out Any>) -> Unit,
            documentationVisitor: (S, CodeBlock) -> Unit
        ): Body = object :
            Body,
            Has.Code by codeVisitor(cozy, codeVisitor),
            Has.Comments by commentVisitor(cozy, commentVisitor),
            Has.Documentation by documentationVisitor(cozy, documentationVisitor) {}

        internal fun <S> property(
            cozy: SourcedCozy<S>,
            modifiers: (S) -> MutableCollection<KModifier>,
            annotations: (S) -> MutableCollection<AnnotationSpec>,
        ): Property =
            object :
                Property,
                Sourced<S> by sourcedByCozy(cozy),
                Has.Modifiers by modifierVisitor(cozy, modifiers),
                Has.Name by nameHolder(cozy),
                Has.Annotations by annotationVisitor(cozy, annotations) {}

    }

}

inline fun <reified T> Attributes.Has.Type.type() = type(T::class)

private typealias SourcedCozy<S> = Cozy<out Attributes.Sourced<S>>