package dsl

annotation class Glitchy

fun main() {


    val dsl = fileBuilder {
        name("GlitchyUtil") packaged "idk"
        type {
            name("GlitchyUtil")
            kind { Class }
            function {
                name("add")
                returns<Int>()
                annotation {
                    name("util")
                    type<Glitchy>()
                }
                parameter {
                    name("a")
                    type<Int>()
                }
                parameter {
                    name("b")
                    type<Int>()
                }
                comment("this is such a glitchy function")
                comment("it adds things together")
                code {
                    +"return a + b"()
                }
            }
        }
    }
    dsl.writeTo(System.out)
}