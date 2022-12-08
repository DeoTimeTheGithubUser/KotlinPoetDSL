package dsl.test

import dsl.fileBuilder
import dsl.invoke
import dsl.type
import dsl.utils.typeParameter

annotation class NumericalOperation

fun main() {


//    val dsl = fileBuilder {
//        name("Calculator") packaged "idk"
//        type("Calculator") {
//            kind { Class }
//            typeParameters {
//                +typeParameter<Number>("T")
//            }
//            function {
//                operator { Plus }
//                returns<Int>()
//                annotation { type<NumericalOperation>() }
//                receiver<Int>()
//                parameter("other") { type<Int>() }
//                comment("This function will add two integers")
//                comment("together, and then return that value.")
//                code {
//                    +"return this + other"()
//                }
//            }
//        }
//    }
//    dsl.writeTo(System.out)
}