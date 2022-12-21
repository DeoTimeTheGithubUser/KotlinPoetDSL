package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.KSDeclaration
import com.squareup.kotlinpoet.ClassName

val KSDeclaration.className
    get() = ClassName(
        packageName.asString(),
        generateSequence(this) { it.parentDeclaration }
            .map { it.simpleName.asString() }
            .toList()
            .asReversed()
    )