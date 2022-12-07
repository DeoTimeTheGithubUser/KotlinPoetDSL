package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.asTypeName
import dsl.utils.Assembler
import dsl.utils.CollectionAssembler
import dsl.utils.buildCollectionTo
import dsl.utils.buildWith
import dsl.utils.requiredByIdentity
import kotlin.properties.Delegates
import kotlin.reflect.KClass

interface Attributes {

    interface Sourced<S> : Attributes {
        val source: S
    }

    fun interface Identity<T> : Attributes {
        fun identity(): T
    }

    fun interface Buildable<T> : Attributes {
        fun build(): T
    }

    interface Property<I, S> : Sourced<S>, Identity<I>, Has.Modifiers, Has.Name, Has.Annotations

    interface Has : Attributes {
        interface Modifiers : Has {
            fun modifiers(assembler: CollectionAssembler<KModifier>)
        }
        
        interface Name : Has {
            val name: String
            fun name(name: String)
        }

        interface Type : Has {
            val type: ClassName
            fun type(type: ClassName)
            fun type(type: ParameterizedTypeName) = type(type.rawType)
            fun type(type: KClass<*>) = type(type.asTypeName())
        }

        interface Annotations : Has {
            fun annotation(assembler: Assembler<AnnotationBuilder>)
        }

        interface Code : Has {
            fun code(assembler: Assembler<CodeBuilder>)
            fun code(format: String, vararg args: Any?)
        }

        interface Comments : Has {
            fun comment(format: String, vararg args: Any?)
        }
    }

    
    


    companion object {

        private fun <I> overridenIdentity() = Identity<I> { error("Identity must be overriden.") }

        private fun <S> overridenSource() = object : Sourced<S> {
            override val source: S get() = error("Type ${this::class.simpleName} does not supply a required source.")
        }

        internal fun <S> modifierVisitor(holder: (S) -> MutableCollection<KModifier>): Has.Modifiers =
            object : Has.Modifiers, Sourced<S> by overridenSource() {
                override fun modifiers(assembler: CollectionAssembler<KModifier>) {
                    buildCollectionTo(holder(source), assembler)
                }
            }

        internal fun <S> annotationVisitor(holder: (S) -> MutableCollection<AnnotationSpec>): Has.Annotations =
            object : Has.Annotations, Sourced<S> by overridenSource() {
                override fun annotation(assembler: Assembler<AnnotationBuilder>) {
                    holder(source).add(AnnotationBuilder().buildWith(assembler))
                }
            }

        internal fun <S, B> buildWith(holder: (S) -> B): Buildable<B> =
            object : Buildable<B>, Sourced<S> by overridenSource() {
                override fun build() = holder(source)
            }

        internal fun <S> codeAdder(adder: (S, CodeBlock) -> Unit): Has.Code =
            object : Has.Code, Sourced<S> by overridenSource() {
                override fun code(assembler: Assembler<CodeBuilder>) {
                    adder(source, CodeBuilder().buildWith(assembler))
                }

                override fun code(format: String, vararg args: Any?) {
                    adder(source, CodeBlock.of(format, args))
                }
            }

        internal fun <S> commentAdder(adder: (S, String, Array<out Any?>) -> Unit): Has.Comments =
            object : Has.Comments, Sourced<S> by overridenSource() {
                override fun comment(format: String, vararg args: Any?) {
                    adder(source, format, args)
                }
            }

        internal fun <T> nameHolder(): Has.Name = object : Has.Name, Identity<T> by overridenIdentity() {
            override var name by requiredByIdentity<String>()
            override fun name(name: String) {
                this.name = name
            }
        }

        internal fun <T> typedHolder(): Has.Type = object : Has.Type, Identity<T> by overridenIdentity() {
            override var type by requiredByIdentity<ClassName>()
            override fun type(type: ClassName) {
                this.type = type
            }
        }

        internal fun <I, S> property(
            modifiers: (S) -> MutableCollection<KModifier>,
            annotations: (S) -> MutableCollection<AnnotationSpec>,
            identity: (I) -> I
        ): Property<I, S> =
            object :
                Property<I, S>,
                Sourced<S> by overridenSource(),
                Identity<I> by overridenIdentity(),
                Has.Modifiers by modifierVisitor(modifiers),
                Has.Name by nameHolder<I>(),
                Has.Annotations by annotationVisitor(annotations) {}

    }

}