package dsl

import com.squareup.kotlinpoet.AnnotationSpec

class AnnotationBuilder : Buildable<AnnotationSpec>,
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Typed by Attributes.typedHolder() {
    override val source by lazy { AnnotationSpec.builder(type) }
    override fun build() = source.build()
}