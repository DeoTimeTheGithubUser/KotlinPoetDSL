package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.AnnotationUseSiteTarget
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import me.deotime.kpoetdsl.AnnotationBuilder
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Delegate
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Field
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.File
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Get
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Parameter
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Property
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Receiver
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.Set
import me.deotime.kpoetdsl.AnnotationBuilder.UseSiteTarget.SetParameter
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke

// todo arguments & defaults
fun KSAnnotation.toSpec() = let { ks ->
    AnnotationBuilder {
        type((ks.annotationType.resolve() as KSClassDeclaration).asClassName())
        useSiteTarget?.let {
            target(
                when (it) {
                    AnnotationUseSiteTarget.FILE -> File
                    AnnotationUseSiteTarget.PROPERTY -> Property
                    AnnotationUseSiteTarget.FIELD -> Field
                    AnnotationUseSiteTarget.GET -> Get
                    AnnotationUseSiteTarget.SET -> Set
                    AnnotationUseSiteTarget.RECEIVER -> Receiver
                    AnnotationUseSiteTarget.PARAM -> Parameter
                    AnnotationUseSiteTarget.DELEGATE -> Delegate
                    AnnotationUseSiteTarget.SETPARAM -> SetParameter
                }
            )
        }

    }
}