package dsl

import com.squareup.kotlinpoet.CodeBlock

class CodeBuilder private constructor(private val cozy: Cozy<CodeBuilder>) :
    Attributes.Buildable<CodeBlock> by Attributes.buildWith(cozy, CodeBlock.Builder::build),
    Attributes.Sourced<CodeBlock.Builder> {

    override val source = CodeBlock.builder()
    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }

    companion object Initializer : Cozy.Initializer<CodeBuilder>(::CodeBuilder)
}

operator fun String.invoke(vararg args: Any?) = CodeBlock.of(this, args)

