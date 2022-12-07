package dsl

import com.squareup.kotlinpoet.CodeBlock

class CodeBuilder :
    Attributes.Buildable<CodeBlock> by Attributes.buildWith(CodeBlock.Builder::build),
    Attributes.Sourced<CodeBlock.Builder>,
    Attributes.Identity<CodeBuilder> {

    override val source = CodeBlock.builder()
    override fun identity() = this
    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }

}

fun String.invoke(vararg args: Any?) = CodeBlock.of(this, args)

