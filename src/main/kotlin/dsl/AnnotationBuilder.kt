package dsl

import com.squareup.kotlinpoet.AnnotationSpec
import dsl.utils.Cozy
import dsl.utils.withRequired

class AnnotationBuilder(private val cozy: Cozy<AnnotationBuilder> = Cozy()) :
    Attributes.Cozied<AnnotationBuilder>(cozy),
    Attributes.Sourced<AnnotationSpec.Builder>,
    Attributes.Buildable<AnnotationSpec> by Attributes.buildWith(cozy, AnnotationSpec.Builder::build),
    Attributes.Has.Code by Attributes.codeVisitor(cozy, AnnotationSpec.Builder::addMember),
    Attributes.Has.Type by Attributes.typedHolder(cozy) {

    override val source by withRequired { AnnotationSpec.builder(type) }

}