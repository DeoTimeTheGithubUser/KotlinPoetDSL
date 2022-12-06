package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import dsl.utils.CollectionAssembler
import dsl.utils.buildCollectionTo
import kotlin.reflect.KClass

interface Attributes {

    interface Sourced<S> : Attributes {
        val source: S
    }

    interface Modifiers {
        fun modifiers(assembler: CollectionAssembler<KModifier>)
    }

    interface Nameable {
        val name: String
        fun name(name: String)
    }

    interface Typed {
        val type: ClassName
        fun type(type: ClassName)
        fun type(type: ParameterizedTypeName) = type(type.rawType)
        fun type(type: KClass<*>) = type(type.asTypeName())
    }

    interface Annotatable {
        fun annotation(assembler: Assembler<AnnotationBuilder>)
    }

    interface Property<S> : Sourced<S>, Modifiers, Nameable, Annotatable

    companion object {

        private fun <T> overridenSource() = object : Sourced<T> {
            override val source: T get() = error("Type ${this::class.simpleName} does not supply a source which is required.")
        }

        internal fun <Source> modifierVisitor(holder: (Source) -> MutableCollection<KModifier>): Modifiers =
            object : Modifiers, Sourced<Source> by overridenSource() {
                override fun modifiers(assembler: CollectionAssembler<KModifier>) {
                    buildCollectionTo(holder(source), assembler)
                }
            }

        internal fun <Source> annotationVisitor(holder: (Source) -> MutableCollection<AnnotationSpec>): Annotatable =
            object : Annotatable, Sourced<Source> by overridenSource() {
                override fun annotation(assembler: Assembler<AnnotationBuilder>) {
                    holder(source).add(AnnotationBuilder().buildWith(assembler))
                }
            }

        internal fun nameHolder(): Nameable = object : Nameable {
            override lateinit var name: String
            override fun name(name: String) {
                this.name = name
            }
        }

        internal fun typedHolder(): Typed = object : Typed {
            override lateinit var type: ClassName
            override fun type(type: ClassName) {
                this.type = type
            }
        }

        internal fun <S> property(
            modifiers: (S) -> MutableCollection<KModifier>,
            annotations: (S) -> MutableCollection<AnnotationSpec>
        ): Property<S> =
            object :
                Sourced<S> by overridenSource(),
                Property<S>, Modifiers by modifierVisitor(modifiers),
                Nameable by nameHolder(),
                Annotatable by annotationVisitor(annotations) {}

    }

}