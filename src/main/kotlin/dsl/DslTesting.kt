package dsl

import kotlin.reflect.KProperty

fun main() {

    val dsl = fileBuilder {
        type {
            name("asdf")

        }
    }
//    val dsl = fileBuilder {
//        function {
//
//        }
//        TypeBuilder.Type.Selector.Anonymous
//        type {
//            name("Typer")
//        }
//    }
}