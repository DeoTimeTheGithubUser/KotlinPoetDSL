package dsl

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import dsl.utils.Assembler
import dsl.utils.Cozy
import dsl.utils.buildWith
import dsl.utils.withRequired
import kotlin.reflect.KClass

class FunctionBuilder(private val cozy: Cozy<FunctionBuilder> = Cozy()) :
    Attributes.Cozied<FunctionBuilder>(cozy),
    Attributes.Sourced<FunSpec.Builder>,
    Attributes.Buildable<FunSpec> by Attributes.buildWith(cozy, FunSpec.Builder::build),
    Attributes.Body by Attributes.body(
        cozy = cozy,
        codeVisitor = FunSpec.Builder::addCode,
        commentVisitor = FunSpec.Builder::addComment
    ),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = FunSpec.Builder::modifiers,
        annotations = FunSpec.Builder::annotations,
    ) {

    override val source by withRequired { FunSpec.builder(name) }

    inline fun parameter(assembler: Assembler<ParameterBuilder>) { source.addParameter(ParameterBuilder().buildWith(assembler)) }
    inline fun parameter(name: String, assembler: Assembler<ParameterBuilder>) { source.addParameter(ParameterBuilder().apply { name(name) }.buildWith(assembler)) }

    fun returns(type: KClass<*>) { source.returns(type) }
    inline fun <reified T> returns() = returns(T::class)

    fun receiver(type: KClass<*>) { source.receiver(type) }
    inline fun <reified T> receiver() = returns(T::class)
}