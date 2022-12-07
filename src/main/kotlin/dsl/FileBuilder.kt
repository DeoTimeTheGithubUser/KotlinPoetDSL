package dsl

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import dsl.utils.Assembler
import dsl.utils.Cozy
import dsl.utils.buildWith
import dsl.utils.required
import dsl.utils.withRequired
import kotlin.properties.Delegates

class FileBuilder(private val cozy: Cozy<FileBuilder> = Cozy()) :
    Attributes.Cozied<FileBuilder>(cozy),
    Attributes.Buildable<FileSpec> by Attributes.buildWith(cozy, FileSpec.Builder::build),
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Has.Name by Attributes.nameHolder(cozy),
    Attributes.Has.Annotations by Attributes.annotationVisitor(cozy, FileSpec.Builder::annotations) {

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