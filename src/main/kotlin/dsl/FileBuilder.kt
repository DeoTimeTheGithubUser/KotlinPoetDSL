package dsl

import com.squareup.kotlinpoet.FileSpec
import dsl.utils.Assembler
import dsl.utils.Uses
import dsl.utils.buildWith
import dsl.utils.required
import dsl.utils.withRequired
import kotlin.reflect.KClass

class FileBuilder private constructor(private val cozy: Cozy<FileBuilder>) :
    Attributes.Buildable<FileSpec> by Attributes.buildWith(cozy, FileSpec.Builder::build),
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Has.Name by Attributes.nameHolder(cozy),
    Attributes.Has.Properties by Attributes.propertiesVisitor(cozy, FileSpec.Builder::members),
    Attributes.Has.Comments by Attributes.commentVisitor(cozy, FileSpec.Builder::addFileComment),
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, FileSpec.Builder::addFunction),
    Attributes.Has.Annotations by Attributes.annotationVisitor(cozy, FileSpec.Builder::annotations) {

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

    inline fun <reified T> import() = import(T::class)

    companion object Initializer : Cozy.Initializer<FileBuilder>(::FileBuilder)
}

inline fun kotlin(closure: FileBuilder.() -> Unit) =
    FileBuilder.cozy().buildWith(closure)