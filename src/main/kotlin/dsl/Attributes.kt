package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
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
    
    interface Has : Attributes {
        interface Modifiers {
            fun modifiers(assembler: CollectionAssembler<KModifier>)
        }
        
        interface Name {
            val name: String
            fun name(name: String)
        }

        interface Type {
            val type: ClassName
            fun type(type: ClassName)
            fun type(type: ParameterizedTypeName) = type(type.rawType)
            fun type(type: KClass<*>) = type(type.asTypeName())
        }

        interface Annotations {
            fun annotation(assembler: Assembler<AnnotationBuilder>)
        }

        interface Code {
            fun code(assembler: Assembler<CodeBuilder>)
            fun code(format: String, vararg args: Any?)
        }

        interface Comments {
            fun comment(format: String, vararg args: Any?)
        }
    }

    
    

    interface Property<S> : Sourced<S>, Has.Modifiers, Has.Name, Has.Annotations

    companion object {

        private fun <T> overridenSource() = object : Sourced<T> {
            override val source: T get() = error("Type ${this::class.simpleName} does not supply a source which is required.")
        }

        internal fun <Source> modifierVisitor(holder: (Source) -> MutableCollection<KModifier>): Has.Modifiers =
            object : Has.Modifiers, Sourced<Source> by overridenSource() {
                override fun modifiers(assembler: CollectionAssembler<KModifier>) {
                    buildCollectionTo(holder(source), assembler)
                }
            }

        internal fun <Source> annotationVisitor(holder: (Source) -> MutableCollection<AnnotationSpec>): Has.Annotations =
            object : Has.Annotations, Sourced<Source> by overridenSource() {
                override fun annotation(assembler: Assembler<AnnotationBuilder>) {
                    holder(source).add(AnnotationBuilder().buildWith(assembler))
                }
            }

        internal fun <Source> codeAdder(adder: (Source, CodeBlock) -> Unit): Has.Code =
            object : Has.Code, Sourced<Source> by overridenSource() {
                override fun code(assembler: Assembler<CodeBuilder>) {
                    adder(source, CodeBuilder().buildWith(assembler))
                }

                override fun code(format: String, vararg args: Any?) {
                    adder(source, CodeBlock.of(format, args))
                }
            }

        internal fun <Source> commentAdder(adder: (Source, CodeBlock) -> Unit): Has.Comments =
            object : Has.Comments, Sourced<Source> by overridenSource() {

                override fun code(format: String, vararg args: Any?) {
                    adder(source, CodeBlock.of(format, args))
                }
            }

        internal fun nameHolder(): Has.Name = object : Has.Name {
            override lateinit var name: String
            override fun name(name: String) {
                this.name = name
            }
        }

        internal fun typedHolder(): Has.Type = object : Has.Type {
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
                Property<S>, Has.Modifiers by modifierVisitor(modifiers),
                Has.Name by nameHolder(),
                Has.Annotations by annotationVisitor(annotations) {}

    }

}