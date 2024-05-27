package classes

import visitante.Visitor

/**
 * Objecto entidade
 *
 * @param nome nome da entidade
 * @constructor cria uma entidade com o nome dado
 */
data class Entidade(private var nome: String, private var entidadeMae: Entidade?) : Componente(nome){

    private var atributos: MutableList<Atributo> = ArrayList()

    private var entidades: MutableList<Entidade> = ArrayList()

    private var textoAninhado: String? = null

    override fun accept(v: Visitor) {
        if(v.visit(this)){
            entidades.forEach{
                v.visit(this)
            }

            atributos.forEach {
                v.visit(this)
            }
        }
        v.endVisit(this)
    }

    /**
     * Altera o nome da Entidade
     *
     * @param nome o novo nome da entidade
     */
    fun renomearEntidade(nome: String) {
        if(!nome.contains(" ")) {
            this.nome = nome
        }
    }

    /**
     * Adicionar uma entidade à lista de entidades da entidade actual
     *
     * @param nome o novo nome da nova entidade
     */
    fun criarSubEntidade(nome: String) {
        entidades.add(Entidade(nome, this))
    }

    /**
     * Adiciona um novo atributo à entidade
     *
     * @param nome
     * @param valor
     */
    fun adicionarAtributo(nome: String, valor: String) {
        //Para não haverem nomes de atributos com espaços (parte a formatação do XML
        //E para não haverem atributos repetidos
        if(!nome.contains(" ") && !atributos.any { it.getNome() == nome }) {
            atributos.add(Atributo(nome, valor))
        }
    }

    /**
     * Remover o atributo com o nome dado
     *
     * @param nome
     */
    fun removerAtributo(nome: String) {
        for (atributo in atributos) {
            if (atributo.getNome() == nome){
                atributos.remove(atributo)
                break
            }
        }
    }

    /**
     * Altera o nome do atributo
     *
     * @param nomeAntigo nome actual do atributo
     * @param nomeNovo nome novo do atributo
     */
    fun renomearAtributo(nomeAntigo: String, nomeNovo: String) {
        if(!nome.contains(" ")) {
            for (atributo in atributos) {
                if (atributo.getNome() == nomeAntigo) {
                    atributo.setNome(nomeNovo)
                }
            }
        }
    }

    /**
     * Altera o valor do atributo com o nome dado
     *
     * @param nome nome do atributo a alterar
     * @param valorNovo valor novo do atributo alterado
     */
    fun alterarAtributo(nome: String, valorNovo: String) {
        for (atributo in atributos) {
            if (atributo.getNome() == nome) {
                atributo.setValor(valorNovo)
            }
        }
    }

    /**
     * Alterar valor de atributos de subEntidades com o nome dado
     *
     * @param nome o nome do atributo a alterar
     * @param valorNovo o valor substituto do atributo
     */
    fun alterarAtributosSubEntidades(nome: String, valorNovo: String) {
        for (entidade in entidades) {
            for (entidadeAtributo in entidade.getAtributos()) {
                if (entidadeAtributo.getNome() == nome) {
                    entidadeAtributo.setValor(valorNovo)
                }
            }
        }
    }

    /**
     * Devolve o nome da entidade
     *
     * @return nome da entidade
     */
    override fun getNome():String{return this.nome}

    /**
     * Devolve a lista de atributos da entidade
     *
     * @return lista de atributos
     */
    fun getAtributos(): MutableList<Atributo> {
        return this.atributos
    }

    /**
     * Altera a lista de atributos da entidade
     *
     * @param atributos Nova lista de atributos da entidade
     */
    fun setAtributos(atributos: MutableList<Atributo>)  {
        this.atributos = atributos
    }

    /**
     * Devolve a lista de entidades da entidade
     *
     * @return lista de entidades
     */
    fun getEntidades(): MutableList<Entidade> {
        return this.entidades
    }

    /**
     * Devolve a lista de entidades da entidade
     *
     * @return lista de entidades
     */
    fun setEntidades(entidades: MutableList<Entidade>)  {
        this.entidades = entidades
    }

    /**
     * Devolve o texto aninhado da entidade
     *
     * @return texto aninhado da entidade
     */
    fun getTextoAninhado(): String? {return this.textoAninhado}

    /**
     * Insere texto aninhado na entidade
     *
     * @param textoAninhado
     */
    fun setTextoAninhado(textoAninhado: String) {
        this.textoAninhado = textoAninhado
    }

    /**
     * Devolve o componente pai, seja ele a raíz (documento) ou um filho (entidade)
     *
     * @return entidade
     */
    fun getEntidadeMae(): Entidade? {
        return entidadeMae
    }

    fun addSubEntidade(subEntidade: Entidade) {
        subEntidade.entidadeMae = this
        entidades.add(subEntidade)
    }

    fun removeSubEntidade(subEntidade: Entidade) {
        entidades.remove(subEntidade)
    }
}
