package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.FileSpec

class KotlinCode(
    override val source: FileSpec
) : Attributes.Sourced<FileSpec> {
    private val value = StringBuilder().apply(source::writeTo).toString()

    override fun toString() = value
}