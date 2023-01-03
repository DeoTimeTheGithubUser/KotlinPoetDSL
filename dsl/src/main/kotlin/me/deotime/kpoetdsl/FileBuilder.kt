package me.deotime.kpoetdsl

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.MemberName
import me.deotime.kpoetdsl.utils.Assembler
import me.deotime.kpoetdsl.utils.Required
import me.deotime.kpoetdsl.utils.Uses
import me.deotime.kpoetdsl.utils.buildWith
import me.deotime.kpoetdsl.utils.required
import me.deotime.kpoetdsl.utils.requiredHolder
import me.deotime.kpoetdsl.utils.withRequired
import kotlin.reflect.KClass

class FileBuilder private constructor(private val cozy: Cozy<FileBuilder>) :
    Attributes.Buildable<KotlinCode>,
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Has.Name by Attributes.nameHolder(cozy),
    Attributes.Has.Properties by Attributes.propertiesVisitor(cozy, FileSpec.Builder::members),
    Attributes.Has.Comments by Attributes.commentVisitor(cozy, FileSpec.Builder::addFileComment),
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, FileSpec.Builder::addFunction),
    Attributes.Has.Annotations by Attributes.annotationVisitor(cozy, FileSpec.Builder::annotations),
    Required.Holder by requiredHolder() {

    private var pack by required<String>()
    override val source by withRequired { FileSpec.builder(pack, name) }

    infix fun Uses.Name.packaged(pack: String) {
        this@FileBuilder.pack = pack
    }

    inline fun type(assembler: Assembler<TypeBuilder>) {
        source.addType(TypeBuilder.cozy().buildWith(assembler))
    }

    inline fun type(name: String, assembler: Assembler<TypeBuilder>) {
        source.addType(TypeBuilder.cozy().apply { name(name) }.buildWith(assembler))
    }

    fun import(vararg types: KClass<*>) {
        types.forEach(source::addImport)
    }

    fun import(vararg types: ClassName) {
        types.forEach(source::addImport)
    }

    fun import(packageName: String, vararg children: String) {
        source.addImport(packageName, *children)
    }

    fun import(vararg types: MemberName) {
        types.map { it.packageName to it.simpleName }.forEach { (a, b) -> import(a, b) }
    }

    inline fun <reified T> import() = import(T::class)

    override fun build() = KotlinCode(source.build())

    companion object Initializer : Cozy.Initializer<FileBuilder>(::FileBuilder)
}

inline fun kotlin(closure: FileBuilder.() -> Unit) =
    FileBuilder.cozy().buildWith(closure)