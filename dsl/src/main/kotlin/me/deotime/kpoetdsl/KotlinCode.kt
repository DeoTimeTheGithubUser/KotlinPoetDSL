package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.FileSpec

@JvmInline
value class KotlinCode(
    val source: String
) {

    // TODO This is obviously not reliable and should be actually be checked somehow
    fun withoutPublic() = KotlinCode(source.replace("public ", ""))
    fun properFormatting() = KotlinCode(source.replace("  ", "\t"))

    override fun toString() = source
}