package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import dsl.utils.Assembler
import dsl.utils.buildWith
import dsl.utils.withRequired

class AnnotationBuilder(private val cozy: Cozy<AnnotationBuilder> = Cozy()) :
    Attributes.Cozied<AnnotationBuilder>(cozy),
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Buildable<AnnotationSpec> by Attributes.buildWith(cozy, AnnotationSpec.Builder::build),
    Attributes.Has.Type by Attributes.typeHolder(cozy) {

    override val source by withRequired { AnnotationSpec.builder(type) }
    inline fun member(assembler: Assembler<CodeBuilder>) {
        source.addMember(CodeBuilder().buildWith(assembler))
    }
    fun target(selector: Target.Companion.() -> Target) {
        source.useSiteTarget(selector(Target.Companion).source)
    }

    @JvmInline
    value class Target private constructor(internal val source: AnnotationSpec.UseSiteTarget) {
        companion object {
            val File = Target(AnnotationTarget.FILE)
            val Type = Target(AnnotationTarget.TYPE)
            val Annotation = Target(AnnotationTarget.ANNOTATION_CLASS)
            val Field = Target(AnnotationTarget.FIELD)
            val Class = Target(AnnotationTarget.CLASS)
            val Constructor = Target(AnnotationTarget.CONSTRUCTOR)
            val Local = Target(AnnotationTarget.LOCAL_VARIABLE)
            val Expression = Target(AnnotationTarget.EXPRESSION)
            val Function = Target(AnnotationTarget.FUNCTION)
            val TypeAlias = Target(AnnotationTarget.TYPEALIAS)
            val Property = Target(AnnotationTarget.PROPERTY)
            val Getter = Target(AnnotationTarget.PROPERTY_GETTER)
            val Setter = Target(AnnotationTarget.PROPERTY_SETTER)
            val TypeParameter = Target(AnnotationTarget.TYPE_PARAMETER)
            val ValueParameter = Target(AnnotationTarget.VALUE_PARAMETER)

        }
    }

}