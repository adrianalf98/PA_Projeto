package classes

import visitante.Visitor

abstract class Componente(private var nome: String) {

    private lateinit var path: String

    open fun getNome(): String {
        return nome
    }

    open fun getPath(): String {
        return path
    }

    open fun setPath(path: String) {
        this.path = path
    }

    abstract fun accept(v: Visitor)
}