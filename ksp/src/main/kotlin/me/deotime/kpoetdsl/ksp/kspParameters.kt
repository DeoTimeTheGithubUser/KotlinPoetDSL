package me.deotime.kpoetdsl.ksp

import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.KModifier
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.invoke
import me.deotime.kpoetdsl.ParameterBuilder

fun KSValueParameter.toSpec() = let { ks ->
    ParameterBuilder {
        ks.name?.asString()?.let { name(it) }
        type(ks.type.resolve().asTypeName())
        modifiers {
            if (ks.isVararg) +KModifier.VARARG
            if (ks.isCrossInline) +KModifier.NOINLINE
            if (ks.isCrossInline) +KModifier.CROSSINLINE
        }
    }
}