package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.CodeBlock
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.Assembler

@KotlinPoetDsl
class CodeBuilder private constructor(private val cozy: Cozy<CodeBuilder>) :
    Attributes.Buildable<CodeBlock> by Attributes.buildWith(cozy, CodeBlock.Builder::build),
    Attributes.Sourced<CodeBlock.Builder> {

    override val source = CodeBlock.builder()
    operator fun CodeBlock.unaryPlus() {
        source.add(this)
    }

    operator fun String.unaryPlus() = +this()

    fun namedArguments(args: Map<String, Any?>) =
        args.map { (name, value) -> "$name = $value" }.joinToString()

    companion object Initializer : Cozy.Initializer.Simple<CodeBuilder> by cozied(::CodeBuilder)
}

operator fun String.invoke(vararg args: Any?) = CodeBlock.of(this, *args)

typealias CodeAssembler = Assembler<CodeBuilder>

