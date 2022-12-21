package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import me.deotime.kpoetdsl.Attributes.Has.Type.Companion.type
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.CollectionAssembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.Uses
import me.deotime.kpoetdsl.utils.buildCollectionTo
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.requiredByCozy
import me.deotime.kpoetdsl.utils.requiredHolder
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf

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
            fun modifiers(vararg modifiers: KModifier)
            fun modifiers(assembler: CollectionAssembler<KModifier>)
        }

        interface Name : Has {
            val name: String
            fun name(name: String): Uses.Name
        }

        interface Type : Has {
            val type: TypeName
            fun type(type: TypeName)
            fun type(type: KType) = type(type.asTypeName())
            fun type(type: KClass<*>) = type(type.asTypeName())

            fun interface Parameters : Has {
                fun typeParameters(builder: CollectionAssembler<TypeVariableName>)
            }

            companion object {
                inline fun <reified T> Attributes.Has.Type.type() = type(typeOf<T>())
            }
        }


        interface Annotations : Has {
            fun annotation(assembler: Assembler<AnnotationBuilder>)

            companion object {
                inline fun <reified T> Annotations.annotation(
                    overload: Nothing? = null,
                    crossinline assembler: Assembler<AnnotationBuilder> = {}
                ) =
                    annotation {
                        type<T>()
                        assembler()
                    }
            }
        }

        interface Functions : Has {
            fun function(assembler: Assembler<FunctionBuilder>)
            fun function(name: String, assembler: Assembler<FunctionBuilder>)
        }

        interface Properties : Has {
            fun property(assembler: Assembler<PropertyBuilder>)
            fun property(name: String, assembler: Assembler<PropertyBuilder>)

            companion object {
                inline fun <reified T> Properties.property(
                    name: String,
                    overload: Nothing? = null,
                    crossinline assembler: Assembler<PropertyBuilder>
                ) =
                    property(name) {
                        type<T>()
                        assembler()
                    }
            }
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

                override fun modifiers(vararg modifiers: KModifier) {
                    holder(source) += modifiers
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

                override fun property(name: String, assembler: Assembler<PropertyBuilder>) {
                    holder(source).add(PropertyBuilder.cozy().apply { name(name) }.buildWith(assembler))
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
                    visitor(source, CodeBlock.of(format, *args))
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
                    visitor(source, CodeBlock.of(format, *args))
                }
            }

        internal fun <S> commentVisitor(
            cozy: SourcedCozy<S>,
            visitor: (S, String, Array<out Any>) -> Unit
        ): Has.Comments =
            object : Has.Comments, Sourced<S> by sourcedByCozy(cozy) {
                override fun comment(format: String, vararg args: Any) {
                    visitor(source, "$format\n", args)
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

        internal fun nameHolder(cozy: Cozy<out Required.Holder>): Has.Name =
            object : Has.Name, Required.Holder by requiredHolder() {
                override var name by requiredByCozy<String>(cozy)
                override fun name(name: String): Uses.Name {
                    this.name = name
                    return Uses.Name
                }
            }

        internal fun typeHolder(cozy: Cozy<out Required.Holder>): Has.Type =
            object : Has.Type, Required.Holder by requiredHolder() {
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

        internal fun <A, S> property(
            cozy: Cozy<A>,
            modifiers: (S) -> MutableCollection<KModifier>,
            annotations: (S) -> MutableCollection<AnnotationSpec>,
        ): Property
                where A : Sourced<S>, A : Required.Holder =
            object :
                Property,
                Sourced<S> by sourcedByCozy(cozy),
                Has.Modifiers by modifierVisitor(cozy, modifiers),
                Has.Name by nameHolder(cozy),
                Has.Annotations by annotationVisitor(cozy, annotations) {}

    }

}


private typealias SourcedCozy<S> = Cozy<out Attributes.Sourced<S>>
