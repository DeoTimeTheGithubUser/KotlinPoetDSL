package dsl

import com.squareup.kotlinpoet.CodeBlock

class CodeBuilder(private val cozy: Cozy<CodeBuilder> = Cozy()) :
    Attributes.Cozied<CodeBuilder>(cozy),
    Attributes.Buildable<CodeBlock> by Attributes.buildWith(cozy, CodeBlock.Builder::build),
    Attributes.Sourced<CodeBlock.Builder> {

    override val source = CodeBlock.builder()
    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }

}

operator fun String.invoke(vararg args: Any?) = CodeBlock.of(this, args)

