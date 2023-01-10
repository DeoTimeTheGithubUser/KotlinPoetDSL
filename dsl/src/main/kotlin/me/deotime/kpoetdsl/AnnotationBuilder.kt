package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.required
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired

class AnnotationBuilder private constructor(private val cozy: Cozy<AnnotationBuilder>) :
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Buildable<AnnotationSpec> by Attributes.buildWith(cozy, AnnotationSpec.Builder::build),
    Maybe<AnnotationSpec.Builder> by maybe(),
    Required.Holder by requiredHolder() {

    private var type by required<ClassName>()
    override val source by withRequired { AnnotationSpec.builder(type) }

    inline fun member(assembler: Assembler<CodeBuilder>) {
        source.addMember(CodeBuilder.cozy().buildWith(assembler))
    }

    fun target(target: AnnotationSpec.UseSiteTarget) {
        source.useSiteTarget(target)
    }

    fun type(type: ClassName) {
        this.type = type
    }

    inline fun <reified T> type() = type(T::class.asClassName())

    companion object Initializer :
        Cozy.Initializer.Simple<AnnotationBuilder> by cozied(::AnnotationBuilder),
        Crumple<AnnotationSpec, AnnotationBuilder> by unstableMaybeCozyCrumple(
            { Initializer },
            AnnotationSpec::toBuilder
        )

    @Suppress("unused")
    object UseSiteTarget {
        inline val AnnotationBuilder.Get get() = AnnotationSpec.UseSiteTarget.GET
        inline val AnnotationBuilder.Set get() = AnnotationSpec.UseSiteTarget.SET
        inline val AnnotationBuilder.Property get() = AnnotationSpec.UseSiteTarget.PROPERTY
        inline val AnnotationBuilder.Field get() = AnnotationSpec.UseSiteTarget.FIELD
        inline val AnnotationBuilder.Delegate get() = AnnotationSpec.UseSiteTarget.DELEGATE
        inline val AnnotationBuilder.SetParameter get() = AnnotationSpec.UseSiteTarget.SETPARAM
        inline val AnnotationBuilder.File get() = AnnotationSpec.UseSiteTarget.FILE
        inline val AnnotationBuilder.Receiver get() = AnnotationSpec.UseSiteTarget.RECEIVER
        inline val AnnotationBuilder.Parameter get() = AnnotationSpec.UseSiteTarget.PARAM
    }
}