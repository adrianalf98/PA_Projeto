package classes

import visitante.Visitor

/**
 * Objecto atributo
 *
 * @property nome o nome do atributo
 * @property valor o valor do atributo
 * @constructor Cria um atributo com o nome e valor dados
 */
data class Atributo(private var nome: String, private var valor: String): Componente(nome) {

    override fun accept(v: Visitor) {
        v.visit(this)
    }

    /**
     * Gera o pretty print deste atributo
     *
     * @return prettyPrint
     */
    fun printAtributo(): String {
        return "$nome=\"$valor\""
    }

    /**
     * Devolve o nome do atributo
     *
     * @return nome
     */
    override fun getNome():String{return this.nome}

    /**
     * Altera o nome do atributo
     *
     * @param nome
     */
    fun setNome(nome: String) {
        if (!nome.contains(" ")){
            this.nome = nome
        }
    }

    /**
     * Devolve o valor do atributo
     *
     * @return
     */
    fun getValor():String{return this.valor}

    /**
     * Altera o valor do atributo
     *
     * @param valor
     */
    fun setValor(valor: String) {
        this.valor = valor
    }
}
