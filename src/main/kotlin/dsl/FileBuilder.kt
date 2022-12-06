package dsl

import com.squareup.kotlinpoet.FileSpec

class FileBuilder : Buildable<FileSpec> {

    private lateinit var pack: String
    private lateinit var name: String
    val _source: FileSpec.Builder by lazy { FileSpec.builder(pack, name) }

    override fun build() = _source.build()

    fun name(name: String) { this.name = name }
    fun inPackage(pack: String) { this.pack = pack }
    fun inPackage(pack: Package) = inPackage(pack.name)

    inline fun function(closure: FunctionBuilder.() -> Unit) {
        _source.addFunction(FunctionBuilder().buildWith(closure))
    }

    inline fun type(assembler: Assembler<TypeBuilder>) {
        _source.addType(TypeBuilder().buildWith(assembler))
    }
}

inline fun fileBuilder(closure: FileBuilder.() -> Unit) =
    FileBuilder().buildWith(closure)