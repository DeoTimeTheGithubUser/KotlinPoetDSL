package me.deotime.kpoetdsl.test

import me.deotime.kpoetdsl.invoke
import me.deotime.kpoetdsl.kotlin
import me.deotime.kpoetdsl.type
import me.deotime.kpoetdsl.utils.typeParameter

fun main() {

    val dsl = kotlin {
        name("Calculator") packaged "idk"

        val typeParam = typeParameter<Number>("T")
        type("Calculator") {
            kind { Class }
            property {
                name("glitch")
                type<String>()
                initializer {
                    +"%T().toString()"(Any::class)
                }
            }
            typeParameters {
                +typeParam
            }
            function {
                operator { Plus }
                receiver(typeParam)
                returns(typeParam)
                parameter("other") { type(typeParam) }
                comment("This function will add two numbers")
                comment("together, and then return that value.")
                code {
                    +"return this + other"
                }


            }
        }
    }
    dsl.writeTo(System.out)
}