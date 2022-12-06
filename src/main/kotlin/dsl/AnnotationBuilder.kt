package dsl

import com.squareup.kotlinpoet.AnnotationSpec

class AnnotationBuilder : Buildable<AnnotationSpec>,
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Has.Code by Attributes.codeAdder(AnnotationSpec.Builder::addMember),
    Attributes.Has.Type by Attributes.typedHolder() {
    override val source by lazy { AnnotationSpec.builder(type) }
    override fun build() = source.build()
}