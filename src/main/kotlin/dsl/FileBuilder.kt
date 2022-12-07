package dsl

import com.squareup.kotlinpoet.FileSpec
import dsl.utils.Assembler
import dsl.utils.Cozy
import dsl.utils.buildWith
import dsl.utils.required
import dsl.utils.withRequired
import kotlin.reflect.KClass

class FileBuilder(private val cozy: Cozy<FileBuilder> = Cozy()) :
    Attributes.Cozied<FileBuilder>(cozy),
    Attributes.Buildable<FileSpec> by Attributes.buildWith(cozy, FileSpec.Builder::build),
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Has.Name by Attributes.nameHolder(cozy),
    Attributes.Has.Comments by Attributes.commentVisitor(cozy, FileSpec.Builder::addFileComment),
    Attributes.Has.Functions by Attributes.functionVisitor(cozy, FileSpec.Builder::addFunction),
    Attributes.Has.Annotations by Attributes.annotationVisitor(cozy, FileSpec.Builder::annotations) {

    private var pack by required<String>()
    override val source by withRequired { FileSpec.builder(pack, name) }

    infix fun Unit.packaged(pack: String) {
        this@FileBuilder.pack = pack
    }

    inline fun type(assembler: Assembler<TypeBuilder>) {
        source.addType(TypeBuilder().buildWith(assembler))
    }

    inline fun type(name: String, assembler: Assembler<TypeBuilder>) {
        source.addType(TypeBuilder().apply { name(name) }.buildWith(assembler))
    }

    fun import(type: KClass<*>) { source.addImport(type) }
    inline fun <reified T> import() = import(T::class)
}

inline fun fileBuilder(closure: FileBuilder.() -> Unit) =
    FileBuilder().buildWith(closure)