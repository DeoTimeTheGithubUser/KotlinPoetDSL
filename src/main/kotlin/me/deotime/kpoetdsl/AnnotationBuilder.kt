package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.required
import me.deotime.kpoetdsl.utils.withRequired

class AnnotationBuilder private constructor(private val cozy: Cozy<AnnotationBuilder>) :
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Buildable<AnnotationSpec> by Attributes.buildWith(cozy, AnnotationSpec.Builder::build) {

    private var type by required<ClassName>()
    override val source by withRequired { AnnotationSpec.builder(type) }

    inline fun member(assembler: Assembler<CodeBuilder>) {
        source.addMember(CodeBuilder.cozy().buildWith(assembler))
    }

    fun target(selector: Target.Companion.() -> Target) {
        source.useSiteTarget(selector(Target).source)
    }

    fun type(type: ClassName) {
        this.type = type
    }

    inline fun <reified T> type() = type(T::class.asClassName())

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

    companion object Initializer : Cozy.Initializer<AnnotationBuilder>(::AnnotationBuilder)

}