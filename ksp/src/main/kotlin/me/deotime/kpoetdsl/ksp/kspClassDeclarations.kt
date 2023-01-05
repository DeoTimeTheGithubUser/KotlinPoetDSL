package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.ClassKind
import me.deotime.kpoetdsl.TypeKind

val ClassKind.typeKind get() = let {
    with(TypeKind.Scope) {
        when (it) {
            ClassKind.CLASS -> Class
            ClassKind.ENUM_CLASS -> Enum
            ClassKind.ANNOTATION_CLASS -> Annotation
            ClassKind.INTERFACE -> Interface
            ClassKind.OBJECT -> Object
            ClassKind.ENUM_ENTRY -> error("TypeBuilder does not support enum entries.")
        }
    }
}