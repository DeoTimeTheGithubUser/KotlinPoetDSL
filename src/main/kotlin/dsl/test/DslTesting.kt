package dsl.test

import dsl.invoke
import dsl.kotlin
import dsl.type
import dsl.utils.typeParameter


fun main() {
//
//    val dsl = kotlin {
//        name("Calculator") packaged "idk"
//
//        val typeParam = typeParameter<Number>("T")
//        type("Calculator") {
//            kind { Class }
//            property {
//                name("glitch")
//                type<String>()
//                initializer {
//                    +"%T().toString()"(Any::class)
//                }
//            }
//            typeParameters {
//                +typeParam
//            }
//            function("asdf") {
//                operator { Plus }
//                receiver(typeParam)
//                returns(typeParam)
//                parameter("other") { type(typeParam) }
//                comment("This function will add two integers")
//                comment("together, and then return that value.")
//                code {
//                    +"return this + other"
//                }
//
//
//            }
//        }
//    }
//    dsl.writeTo(System.out)
}