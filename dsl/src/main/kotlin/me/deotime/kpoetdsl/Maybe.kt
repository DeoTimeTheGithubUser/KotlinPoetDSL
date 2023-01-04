package me.deotime.kpoetdsl

internal interface Maybe<T> {
    var value: T?
}

internal fun <T> maybe(): Maybe<T> = object : Maybe<T> {
    override var value: T? = null
}