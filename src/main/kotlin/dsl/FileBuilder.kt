package dsl

import com.squareup.kotlinpoet.FileSpec

class FileBuilder : Buildable<FileSpec>,
    Attributes.Sourced<FileSpec.Builder>,
    Attributes.Nameable by Attributes.nameHolder(),
    Attributes.Annotatable by Attributes.annotationVisitor(FileSpec.Builder::annotations) {

    private lateinit var pack: String
    override val source: FileSpec.Builder by lazy { FileSpec.builder(pack, name) }

    override fun build() = source.build()

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