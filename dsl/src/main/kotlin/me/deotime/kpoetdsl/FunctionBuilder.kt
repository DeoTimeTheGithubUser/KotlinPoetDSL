package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import me.deotime.kpoetdsl.Attributes.Has.Type.Companion.type
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired
import kotlin.reflect.KClass
import kotlin.reflect.typeOf

class FunctionBuilder private constructor(private val cozy: Cozy<FunctionBuilder>) :
    Attributes.Sourced<FunSpec.Builder>,
    Attributes.Buildable<FunSpec> by Attributes.buildWith(cozy, FunSpec.Builder::build),
    Attributes.Body by Attributes.body(
        cozy = cozy,
        codeVisitor = FunSpec.Builder::addCode,
        commentVisitor = FunSpec.Builder::addComment,
        documentationVisitor = FunSpec.Builder::addKdoc
    ),
    Attributes.Property by Attributes.property(
        cozy = cozy,
        modifiers = FunSpec.Builder::modifiers,
        annotations = FunSpec.Builder::annotations,
    ),
    Maybe<FunSpec.Builder> by maybe(),
    Required.Holder by requiredHolder() {

    override val source by withRequired { model.builder(name) }

    private var model = Model.Normal

    private enum class Model(val builder: (String) -> FunSpec.Builder) {
        Normal(FunSpec.Companion::builder),
        Constructor({ FunSpec.constructorBuilder() }),
        Getter({ FunSpec.getterBuilder() }),
        Setter({ FunSpec.setterBuilder() })
    }

    fun constructor() {
        nameNoOp()
        model = Model.Constructor
    }

    fun getter() {
        nameNoOp()
        model = Model.Getter
    }

    fun setter() {
        nameNoOp()
        model = Model.Setter
    }

    inline fun parameter(assembler: Assembler<ParameterBuilder>) {
        source.addParameter(ParameterBuilder.cozy().buildWith(assembler))
    }

    inline fun parameter(name: String, assembler: Assembler<ParameterBuilder>) {
        source.addParameter(ParameterBuilder.cozy().apply { name(name) }.buildWith(assembler))
    }

    inline fun <reified T> parameter(name: String, overload: Nothing? = null, assembler: Assembler<ParameterBuilder> = {}) {
        parameter(name) {
            type<T>()
            assembler()
        }
    }


    fun operator(op: Operator.Companion.() -> Operator) {
        name(op(Operator).name)
        modifiers { +KModifier.OPERATOR }
    }

    fun returns(type: TypeName) {
        source.returns(type)
    }

    fun returns(type: KClass<*>) = returns(type.asTypeName())
    inline fun <reified T> returns() = returns(typeOf<T>().asTypeName())

    fun receiver(type: TypeName) {
        source.receiver(type)
    }

    fun receiver(type: KClass<*>) = receiver(type.asTypeName())
    inline fun <reified T> receiver() = receiver(typeOf<T>().asTypeName())

    private fun nameNoOp() {
        name("no-op")
    }

    companion object Initializer :
        Cozy.Initializer.Simple<FunctionBuilder> by cozied(::FunctionBuilder),
        Crumple<FunSpec, FunctionBuilder> by unstableMaybeCozyCrumple({ Initializer }, FunSpec::toBuilder)

    @JvmInline
    value class Operator private constructor(val name: String) {
        companion object {
            val Plus = Operator("plus")
            val Minus = Operator("minus")
            val Times = Operator("times")
            val Divide = Operator("divide")
            val Modulo = Operator("rem")

            val Not = Operator("not")

            val PlusAssign = Operator("plusAssign")
            val MinusAssign = Operator("minusAssign")
            val TimesAssign = Operator("timesAssign")
            val DivideAssign = Operator("divideAssign")
            val ModuloAssign = Operator("remAssign")

            val UnaryPlus = Operator("unaryPlus")
            val UnaryMinus = Operator("unaryMinus")

            val Inc = Operator("inc")
            val Dec = Operator("dec")

            val Get = Operator("get")
            val Set = Operator("set")

            val Range = Operator("rangeTo")
            val Contains = Operator("contains")
            val Invoke = Operator("invoke")
            val Equals = Operator("equals")
            val Compare = Operator("compareTo")
        }
    }
}