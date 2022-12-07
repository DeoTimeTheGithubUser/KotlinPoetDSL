package dsl

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import dsl.utils.bounds
import dsl.utils.typeParameter

annotation class NumericalOperation

fun main() {


    val dsl = fileBuilder {
        name("Calculator") packaged "idk"
        type("Calculator") {
            kind { Class }
            typeParameters {
                +typeParameter<Number>("T")
            }
            function("plus") {
                returns<Int>()
                modifiers { +KModifier.OPERATOR }
                annotation { type<NumericalOperation>() }
                receiver<Int>()
                parameter("other") { type<Int>() }
                comment("This function will add two integers")
                comment("together, and then return that value.")
                code {
                    +"return this + other"()
                }
            }
        }
    }
    dsl.writeTo(System.out)
}