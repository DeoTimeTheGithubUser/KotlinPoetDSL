package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import me.deotime.kpoetdsl.utils.buildWith

class CodeBuilder private constructor(private val cozy: Cozy<CodeBuilder>) :
    Attributes.Buildable<CodeBlock> by Attributes.buildWith(cozy, CodeBlock.Builder::build),
    Attributes.Sourced<CodeBlock.Builder> {

    override val source = CodeBlock.builder()
    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }

    operator fun String.unaryPlus() = +this()
    companion object Initializer : Cozy.Initializer.Simple<CodeBuilder> by cozied(::CodeBuilder)
}

inline fun code(closure: CodeBuilder.() -> Unit) = CodeBuilder.cozy().buildWith(closure)

operator fun String.invoke(vararg args: Any?) = CodeBlock.of(this, *args)

