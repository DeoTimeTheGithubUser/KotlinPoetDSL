package dsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import dsl.utils.Assembler
import dsl.utils.buildWith
import dsl.utils.required
import dsl.utils.withRequired
import kotlin.properties.Delegates

class FileBuilder :
    Attributes.Buildable<FileSpec> by Attributes.buildWith(FileSpec.Builder::build),
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Identity<FileBuilder>,
    Attributes.Has.Name by Attributes.nameHolder<FileBuilder>(),
    Attributes.Has.Annotations by Attributes.annotationVisitor(FileSpec.Builder::annotations) {

    override fun identity() = this

    private var pack by required<String>()
    override val source by withRequired { FileSpec.builder(pack, name) }

    fun inPackage(pack: String) {
        this.pack = pack
    }

    fun inPackage(pack: Package) = inPackage(pack.name)

    inline fun function(closure: FunctionBuilder.() -> Unit) {
        source.addFunction(FunctionBuilder().buildWith(closure))
    }

    inline fun type(assembler: Assembler<TypeBuilder>) {
        source.addType(TypeBuilder().buildWith(assembler))
    }
}

inline fun fileBuilder(closure: FileBuilder.() -> Unit) =
    FileBuilder().buildWith(closure)