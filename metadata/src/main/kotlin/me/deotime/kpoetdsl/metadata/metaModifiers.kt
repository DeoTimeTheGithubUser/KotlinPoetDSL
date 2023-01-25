package me.deotime.kpoetdsl.metadata

import com.squareup.kotlinpoet.KModifier
import kotlinx.metadata.Flag
import kotlinx.metadata.Flags

fun Flags.toStandardModifiers() = buildList {
    val flag = this@toStandardModifiers

    if (Flag.IS_ABSTRACT(flag)) add(KModifier.ABSTRACT)
    if (Flag.IS_FINAL(flag)) add(KModifier.FINAL)
    if (Flag.IS_OPEN(flag)) add(KModifier.OPEN)
    if (Flag.IS_SEALED(flag)) add(KModifier.SEALED)

    // Public not included because kotlin poet doesnt give
    // us the option to disable it being on by default :|
    if (Flag.IS_INTERNAL(flag)) add(KModifier.INTERNAL)
    if (Flag.IS_PRIVATE(flag)) add(KModifier.PRIVATE)
    if (Flag.IS_PROTECTED(flag)) add(KModifier.PROTECTED)

}

fun Flags.toFunctionModifiers() = buildList {
    val flag = this@toFunctionModifiers

    addAll(flag.toStandardModifiers())

    // functions
    if (Flag.Function.IS_EXPECT(flag)) add(KModifier.EXPECT)
    if (Flag.Function.IS_INFIX(flag)) add(KModifier.INFIX)
    if (Flag.Function.IS_EXTERNAL(flag)) add(KModifier.EXTERNAL)
    if (Flag.Function.IS_INLINE(flag)) add(KModifier.INLINE)
    if (Flag.Function.IS_OPERATOR(flag)) add(KModifier.OPERATOR)
    if (Flag.Function.IS_SUSPEND(flag)) add(KModifier.SUSPEND)
    if (Flag.Function.IS_TAILREC(flag)) add(KModifier.TAILREC)
}

fun Flags.toPropertyModifiers() = buildList {
    val flag = this@toPropertyModifiers

    addAll(flag.toStandardModifiers())

    // properties
    if (Flag.Property.IS_LATEINIT(flag)) add(KModifier.LATEINIT)
    if (Flag.Property.IS_EXTERNAL(flag)) add(KModifier.EXTERNAL)
    if (Flag.Property.IS_CONST(flag)) add(KModifier.CONST)
}

fun Flags.toParameterModifiers() = buildList {
    val flag = this@toParameterModifiers

    // parameters
    if (Flag.ValueParameter.IS_CROSSINLINE(flag)) add(KModifier.CROSSINLINE)
    if (Flag.ValueParameter.IS_NOINLINE(flag)) add(KModifier.NOINLINE)
}