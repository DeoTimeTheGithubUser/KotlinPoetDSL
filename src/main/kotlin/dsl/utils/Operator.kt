package dsl.utils



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