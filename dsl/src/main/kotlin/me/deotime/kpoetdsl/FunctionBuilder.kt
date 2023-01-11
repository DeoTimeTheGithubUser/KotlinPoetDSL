package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import me.deotime.kpoetdsl.Attributes.Has.Type.Companion.type
import me.deotime.kpoetdsl.Cozy.Initializer.Simple.Companion.cozy
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
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


    fun operator(op: Operator) {
        name(op.name)
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
        Crumple<FunSpec, FunctionBuilder> by unstableMaybeCozyCrumple({ Initializer }, FunSpec::toBuilder) {

        @Suppress("EXTENSION_SHADOWED_BY_MEMBER")
        inline fun <reified T> FunctionBuilder.parameter(
            name: String,
            assembler: Assembler<ParameterBuilder> = {}
        ) {
            parameter(name) {
                type<T>()
                assembler()
            }
        }
    }

}

@JvmInline
value class Operator private constructor(val name: String) {

    interface Scope {
        companion object : Scope {
            val Scope.Plus by lazy { Operator("plus") }
            val Scope.Minus by lazy { Operator("minus") }
            val Scope.Times by lazy { Operator("times") }
            val Scope.Divide by lazy { Operator("divide") }
            val Scope.Modulo by lazy { Operator("rem") }

            val Scope.Not by lazy { Operator("not") }

            val Scope.PlusAssign by lazy { Operator("plusAssign") }
            val Scope.MinusAssign by lazy { Operator("minusAssign") }
            val Scope.TimesAssign by lazy { Operator("timesAssign") }
            val Scope.DivideAssign by lazy { Operator("divideAssign") }
            val Scope.ModuloAssign by lazy { Operator("remAssign") }

            val Scope.UnaryPlus by lazy { Operator("unaryPlus") }
            val Scope.UnaryMinus by lazy { Operator("unaryMinus") }

            val Scope.Inc by lazy { Operator("inc") }
            val Scope.Dec by lazy { Operator("dec") }

            val Scope.Get by lazy { Operator("get") }
            val Scope.Set by lazy { Operator("set") }

            val Scope.Range by lazy { Operator("rangeTo") }
            val Scope.Contains by lazy { Operator("contains") }
            val Scope.Invoke by lazy { Operator("invoke") }
            val Scope.Equals by lazy { Operator("equals") }
            val Scope.Compare by lazy { Operator("compareTo") }
        }
    }

}