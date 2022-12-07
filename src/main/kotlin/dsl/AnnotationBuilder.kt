package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import dsl.utils.withRequired

class AnnotationBuilder :
    Attributes.Buildable<AnnotationSpec> by Attributes.buildWith(AnnotationSpec.Builder::build),
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Identity<AnnotationBuilder>,
    Attributes.Has.Code by Attributes.codeAdder(AnnotationSpec.Builder::addMember),
    Attributes.Has.Type by Attributes.typedHolder<AnnotationBuilder>() {

    override val source by withRequired { AnnotationSpec.builder(type) }
    override fun identity() = this

}