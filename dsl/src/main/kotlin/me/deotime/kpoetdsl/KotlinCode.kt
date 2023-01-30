package me.deotime.kpoetdsl

data class KotlinCode(
    val source: String,
    val name: String,
    val packageName: String,
) {

    // TODO This is obviously not reliable and should be actually be checked somehow
    fun withoutPublic() = copy(source = source.replace("public ", ""))
    fun properFormatting() = copy(source = source.replace("  ", "\t"))

    override fun toString() = source
}