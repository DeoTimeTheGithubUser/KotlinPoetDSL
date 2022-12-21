package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.ClassKind
import me.deotime.kpoetdsl.TypeBuilder

fun TypeBuilder.kind(kind: ClassKind) = kind {
    when (kind) {
        ClassKind.CLASS -> Class
        ClassKind.ENUM_CLASS -> Enum
        ClassKind.ANNOTATION_CLASS -> Annotation
        ClassKind.INTERFACE -> Interface
        ClassKind.OBJECT -> Object
        ClassKind.ENUM_ENTRY -> error("TypeBuilder does not support enum entries.")
    }
}