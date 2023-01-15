package me.deotime.kpoetdsl.types

import com.squareup.kotlinpoet.ExperimentalKotlinPoetApi
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.typeNameOf
import me.deotime.kpoetdsl.Attributes
import me.deotime.kpoetdsl.Attributes.Buildable.Companion.buildWith
import me.deotime.kpoetdsl.KotlinPoetDsl
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.CollectionAssembler
import me.deotime.kpoetdsl.utils.buildCollectionTo

@KotlinPoetDsl
class LambdaTypeBuilder(
    var suspend: Boolean = false,
    var receiver: TypeName? = null,
    var returns: TypeName = typeNameOf<Unit>(),
    @PublishedApi
    internal val contextReceivers: MutableList<TypeName> = mutableListOf(),
    @PublishedApi
    internal val parameters: MutableList<ParameterSpec> = mutableListOf(),
) : Attributes.Buildable<LambdaTypeName> {

    @KotlinPoetDsl
    inline fun parameters(assembler: CollectionAssembler<TypeName>) {
        buildCollectionTo(parameters, ParameterSpec.Companion::unnamed, assembler)
    }

    @KotlinPoetDsl
    inline fun context(assembler: CollectionAssembler<TypeName>) {
        buildCollectionTo(contextReceivers, assembler)
    }

    @OptIn(ExperimentalKotlinPoetApi::class)
    override fun build() = LambdaTypeName.get(
        receiver = receiver,
        parameters = parameters,
        returnType = returns,
        contextReceivers = contextReceivers
    ).copy(suspending = suspend)
}

@KotlinPoetDsl
inline fun lambdaType(assembler: Assembler<LambdaTypeBuilder>) = LambdaTypeBuilder().buildWith(assembler)