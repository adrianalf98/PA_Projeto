package impl

import api.XMLLibrary
import classes.Entidade
import kotlin.reflect.KClass
import kotlin.reflect.full.*

class XMLLibraryImpl : XMLLibrary {

    @Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
    annotation class XmlEntity(
        val name: String
    )

    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlAtribute(
        val name: String
    )

    @Target(AnnotationTarget.CLASS)
    annotation class XmlAdapter(
        val name: KClass<*>
    )

    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlString(
        val name: KClass<*>
    )

    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlHide()

    override fun criarEntidade(obj: Any): Entidade {
        val clazz = obj::class
        require(clazz.isData) { "A classe fornecida deve ser uma data class." }

        val xmlEntity = clazz.findAnnotation<XmlEntity>()?.name ?: clazz.simpleName
        val properties = clazz.declaredMemberProperties


        var entidade = Entidade(xmlEntity ?: "Entidade", null)
        for (property in properties) {
            val propertyName = property.name

            if (property.hasAnnotation<XmlEntity>()){
                val type = property.returnType.classifier

                //Se o tipo da propriedade for uma lista
                if  (type == List::class){
                    val elementos = property.getter.call(obj) as List<*>
                    if (property.hasAnnotation<XmlHide>()){
                        for(elemento in elementos){
                            if (elemento != null) {
                                adicionarEntidade(elemento, entidade)
                            }
                        }
                    } else {
                        val subEntidade = Entidade(propertyName, entidade)
                        entidade.addSubEntidade(subEntidade)
                        for(elemento in elementos){
                            if (elemento != null) {
                                adicionarEntidade(elemento, subEntidade)
                            }
                        }
                    }
                }
                //Se o tipo for String/Double/etc
                else {
                    val subEntidade = Entidade(propertyName, entidade)
                    //Vai buscar o valor à variável correspondente à actual propriedade
                    val valor = property.getter.call(obj)
                    subEntidade.setTextoAninhado(valor.toString())
                    entidade.addSubEntidade(subEntidade)
                }

            }
            else if (property.hasAnnotation<XmlAtribute>()){
                //Vai buscar o valor à variável correspondente à actual propriedade
                val valor = property.getter.call(obj)
                entidade.adicionarAtributo(propertyName, valor.toString())
            }
        }
        if (clazz.hasAnnotation<XmlAdapter>()){
            val adapter = clazz.findAnnotation<XmlAdapter>()!!.name
            val instance = adapter.primaryConstructor!!.call()
            for (function in adapter.functions){
                if(!function.name.equals("toString") && !function.name.equals("equals") && !function.name.equals("hashCode")){
                    entidade = function.call(instance, entidade) as Entidade
                }
            }
        }

        return entidade
    }

    /**
     * Devolve uma string da entidade segundo a estrutura de um ficheiro XML
     *
     * @param entidade Entidade que se pretende imprimir como Documento XML
     * @return
     */
    override fun prettyPrint(entidade: Entidade): String {
        val output = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<${entidade.getNome()}")

        for (atributo in entidade.getAtributos()) {
            output.append(" ").append(atributo.printAtributo())
        }

        //Quando não tem subentidade nem texto aninhado
        if (entidade.getEntidades().isEmpty() && (entidade.getTextoAninhado() == null || entidade.getTextoAninhado()!!.isEmpty())) {
            output.append("/>")
        }
        //Quando tem subentidades e/ou texto aninhado
        else {
            //Quando não tem subentidades e tem texto aninhado
            if (entidade.getEntidades().isEmpty()){
                output.append(">")
                output.append(entidade.getTextoAninhado())
                output.append("</${entidade.getNome()}>")
            }
            //Quando tem subentidades
            else {
                output.append(">\n")

                for (subEntidade in entidade.getEntidades()) {
                    output.append(printEntidade(subEntidade, 1)).append("\n")
                }

                //Quando também tem texto aninhado
                if(entidade.getTextoAninhado() != null && entidade.getTextoAninhado()!!.isNotEmpty()){
                    output.append(getTabbed(1 ))
                    output.append(entidade.getTextoAninhado()).append("\n")
                }

                output.append("</${entidade.getNome()}>")
            }
        }

        return output.toString()
    }

    override fun microXPath(entidade: Entidade, xPath: String): List<Entidade> {
        //Se xPath for vazio devolve uma lista apenas com a entidade recebida nos argumentos
        if (xPath.isEmpty()){
            return listOf(entidade)
        } else {
            val resultado: MutableList<Entidade> = ArrayList()
            //Separa o xPath numa lista de caminhos (entidades pelas quais se pretende passar)
            val caminhos = xPath.split('/')

            //Se o xPath tiver apenas uma palavra irá verificar se o nome da
            // classe enviada é esse e se for adiciona-o à lista do resultado
            if(caminhos.size == 1){
                if (entidade.getNome() == caminhos.first()){
                    resultado.add(entidade)
                }
            }
            else {
                //Novo xPath terá apenas os "caminhos" posteriores ao primeiro
                val newXPath = caminhos.drop(1).joinToString(separator = "/")

                //Por cada subentidade da entidade enviada erá procurar por entidades
                // que corespondam ao novo xPath
                for (subEntidade in entidade.getEntidades()){
                    if (subEntidade.getNome() == caminhos.get(1)){
                        microXPath(subEntidade, newXPath, resultado)
                    }
                }
            }
            return resultado.toList()
        }
    }

    override fun gerarXML(obj: Any): String {
        return prettyPrint(criarEntidade(obj))
    }

    private fun microXPath(entidade: Entidade, xPath: String, resultado: MutableList<Entidade>) {
        //Separa o xPath numa lista de caminhos (entidades pelas quais se pretende passar)
        var caminhos = xPath.split('/')

        //Se o xPath tiver apenas uma palavra irá verificar se o nome da
        // classe enviada é esse e se for adiciona-o à lista do resultado
        if(caminhos.size == 1){
            if (entidade.getNome() == caminhos.first()){
                resultado.add(entidade)
            }

        }
        else {
            //Novo xPath terá apenas os "caminhos" posteriores ao primeiro
            val newXPath = caminhos.drop(1).joinToString(separator = "/")

            //Por cada subentidade da entidade enviada erá procurar por entidades
            // que corespondam ao novo xPath
            for (subEntidade in entidade.getEntidades()){
                if (subEntidade.getNome() == caminhos.get(1)){
                    microXPath(subEntidade, newXPath, resultado)
                }
            }
        }
    }

    /**
     * Pretty print da entidade e seus filhos
     *
     * @return string com os dados da entidade e filhos
     */
    fun printEntidade(entidade: Entidade, profundidade: Int): String {
        val output = StringBuilder("")
        output.append(getTabbed(profundidade))

        output.append("<${entidade.getNome()}")

        for (atributo in entidade.getAtributos()) {
            output.append(" ").append(atributo.printAtributo())
        }
        if (entidade.getEntidades().isEmpty() && (entidade.getTextoAninhado() == null || entidade.getTextoAninhado()!!.isEmpty())) {
            output.append("/>")
        }
        else {
            if (entidade.getEntidades().isEmpty()){
                output.append(">")
                output.append(entidade.getTextoAninhado())
                output.append("</${entidade.getNome()}>")
            } else {
                output.append(">\n")

                for (subEntidade in entidade.getEntidades()) {
                    output.append(printEntidade(subEntidade, profundidade+1)).append("\n")
                }

                output.append(getTabbed(profundidade))
                if(entidade.getTextoAninhado() != null && entidade.getTextoAninhado()!!.isNotEmpty()){
                    output.append(getTabbed(profundidade +1 ))
                    output.append(entidade.getTextoAninhado()).append("\n")
                }
                output.append("</${entidade.getNome()}>")
            }
        }
        return output.toString()
    }

    private fun getTabbed(depth: Int): String {
        val output = StringBuilder("")
        var i = 0
        while (i < depth) {
            output.append("\t")
            i++
        }
        return output.toString()
    }

    fun adicionarEntidade(elemento: Any, entidade: Entidade) {
        val clazz = elemento::class
        require(clazz.isData) { "A classe fornecida deve ser uma data class." }

        val xmlEntityName = clazz.findAnnotation<XmlEntity>()?.name ?: clazz.simpleName
        val properties = clazz.declaredMemberProperties

        var subEntidade = Entidade(xmlEntityName ?: "Entidade", entidade)
        entidade.addSubEntidade(subEntidade)
        for (property in properties) {
            val propertyName = property.name

            if (property.hasAnnotation<XmlEntity>()){
                val type = property.returnType.classifier
                if  (type == List::class){
                    val elementos = property.getter.call(elemento) as List<*>
                    if (property.hasAnnotation<XmlHide>()){
                        for(elemento in elementos) {
                            if (elemento != null) {
                                adicionarEntidade(elemento, subEntidade)
                            }
                        }
                    } else {
                        for(elemento in elementos){
                            if (elemento != null) {
                                adicionarEntidade(elemento, subEntidade)
                            }
                        }
                    }
                } else {
					val subSubEntidade = Entidade(propertyName, entidade)
                    val valor = property.getter.call(elemento)
                    subSubEntidade.setTextoAninhado(valor.toString())
                    subEntidade.addSubEntidade(subSubEntidade)
                }

            }
            else if (property.hasAnnotation<XmlAtribute>()){
                val valor = property.getter.call(elemento)
                subEntidade.adicionarAtributo(propertyName, valor.toString())
            }
        }
		
		if (clazz.hasAnnotation<XmlAdapter>()){
            val adapter = clazz.findAnnotation<XmlAdapter>()!!.name
            val instance = adapter.primaryConstructor!!.call()
            for (function in adapter.functions){
                if(!function.name.equals("toString") && !function.name.equals("equals") && !function.name.equals("hashCode")){
                    subEntidade = function.call(instance, subEntidade) as Entidade
                }
            }
        }
    }


    /**
     * Adiciona um novo atributo à entidade designada
     *
     * @param entidade Entidade à qual queremos adicionar o atributo a entidade(s)
     * @param nomeEntidade Nome da entidade à qual queremos adicionar o atributo
     * @param nomeAtributo Nome do novo atributo
     * @param valorAtributo Valor do novo atributo
     */
    fun adicionarAtributoGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorAtributo: String) {
        if (nomeEntidade.isNotEmpty() && nomeAtributo.isNotEmpty() && valorAtributo.isNotEmpty()) {
            if (entidade.getNome().equals(nomeEntidade)){
                entidade.adicionarAtributo(nomeAtributo, valorAtributo)
            }

            for(subEntidade in entidade.getEntidades()){
                adicionarAtributoGlobalmente(subEntidade, nomeEntidade, nomeAtributo, valorAtributo)
            }
        }
    }

    /**
     * Renomear todas as entidades com o nome dado para o novo valor
     *
     * @param nomeAntigo Nome que se pretende alterar
     * @param nomeNovo Nome que se pretende definir
     */
    fun renomearEntidadesGlobalmente(entidade: Entidade, nomeAntigo: String, nomeNovo: String){
        if (nomeAntigo.isNotEmpty() && nomeNovo.isNotEmpty() && !nomeNovo.contains(" ")) {
            if (entidade.getNome() == nomeAntigo) {
                entidade.renomearEntidade(nomeNovo)
            }
            for (subEntidade in entidade.getEntidades()) {
                renomearEntidadesGlobalmente(subEntidade, nomeAntigo, nomeNovo)
            }
        }
    }

    /**
     * Remover todas as entidades com o nome dado
     *
     * @param entidade Entidade que se pretende encontrar a entidade a Remover
     * @param nome Nome das entidades que se pretende remover
     */
    fun removerEntidadesGlobalmente(entidade: Entidade, nome: String){
        if (nome.isNotEmpty()) {
            var entidadesParaRemover = mutableListOf<Entidade>()

            val entidades = entidade.getEntidades()
            for (subEntidade in entidades) {
                if (subEntidade.getNome() == nome) {
                    entidadesParaRemover.add(subEntidade)
                } else {
                    removerEntidadesGlobalmente(subEntidade, nome)
                }
            }

            // Remover entidades fora da iteração
            entidade.getEntidades().removeAll(entidadesParaRemover)
        }
    }

    /**
     * Renomear todos os atributos com o nome dado para o novo valor
     *
     * @param entidade entidade
     * @param nomeAntigo Nome que se pretende alterar
     * @param nomeNovo Nome que se pretende definir
     */
    fun renomearAtributosGlobalmente(entidade: Entidade, nomeAntigo: String, nomeNovo: String){
        if (nomeNovo.isNotEmpty() && nomeAntigo.isNotEmpty()){
            for (atributo in entidade.getAtributos()){
                if (atributo.getNome() == nomeAntigo){
                    atributo.setNome(nomeNovo)
                }
            }
            for (subEntidade in entidade.getEntidades()){
                renomearAtributosGlobalmente(subEntidade, nomeAntigo, nomeNovo)
            }
        }
    }

    /**
     * Remover todas os atributos com o nome dado a entidades com o nome dado
     *
     * @param nomeEntidade nome da entidade à qual se pretende remover o atributo
     * @param nomeAtributo nome do atributo
     */
    fun removerAtributosGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String){
        if (nomeEntidade.isNotEmpty() && nomeAtributo.isNotEmpty()){

            if (entidade.getNome() == nomeEntidade){
                entidade.removerAtributo(nomeAtributo)
            }
            for (subEntidade in entidade.getEntidades()){
                removerAtributosGlobalmente(subEntidade, nomeEntidade, nomeAtributo)
            }
        }
    }

    /**
     * Alterar todas os atributos com o nome dado a entidades com o nome dado
     *
     * @param nomeEntidade nome da entidade à qual se pretende remover o atributo
     * @param nomeAtributo nome do atributo
     */
    fun alterarAtributosGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorNovo: String){
        if (nomeEntidade.isNotEmpty() && nomeAtributo.isNotEmpty() && valorNovo.isNotEmpty()){
            if (entidade.getNome() == nomeEntidade){
                entidade.alterarAtributo(nomeAtributo, valorNovo)
            }
            for (subEntidade in entidade.getEntidades()){
                alterarAtributosGlobalmente(subEntidade, nomeEntidade, nomeAtributo, valorNovo)
            }
        }
    }

    /**
     * Adiciona uma subEntidade à entidade dada
     *
     * @param entidade Entidade onde se pretende procurar onde adicionar a nova subEntidade
     * @param nome Nome da nova entidade
     */
    fun adicionarSubEntidade(entidade: Entidade, nomeSubEntidade: String, nomeEntidadePai: String){
        if (nomeSubEntidade.isNotEmpty() && nomeEntidadePai.isNotEmpty() && !nomeSubEntidade.contains(" ")){
            if (entidade.getNome().equals(nomeEntidadePai)){
                entidade.criarSubEntidade(nomeSubEntidade)
            }
            for (subEntidade in entidade.getEntidades()){
                adicionarSubEntidade(subEntidade, nomeSubEntidade, nomeEntidadePai)
            }
        }
    }

}