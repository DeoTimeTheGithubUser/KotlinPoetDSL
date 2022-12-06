package dsl

import com.squareup.kotlinpoet.CodeBlock

class CodeBuilder : Buildable<CodeBlock>, Attributes.Sourced<CodeBlock.Builder> {
    override val source = CodeBlock.builder()
    override fun build() = source.build()

    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }
}

fun String.invoke(vararg args: Any?) = CodeBlock.of(this, args)

