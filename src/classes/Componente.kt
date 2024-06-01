package classes

import visitante.Visitor

/**
 * Classe abstrata que representa um componente com um nome
 *
 * @param nome o nome do componente
 * @constructor cria um componente com o nome dado
 */
abstract class Componente(private var nome: String) {

    /**
     * Devolve o nome do componente
     *
     * @return o nome do componente
     */
    open fun getNome(): String {
        return nome
    }

    /**
     * Aceita um visitante para visitar este componente
     *
     * @param v o visitante
     */
    abstract fun accept(v: Visitor)
}