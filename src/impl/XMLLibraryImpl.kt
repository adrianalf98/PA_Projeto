package impl

import api.XMLLibrary
import classes.Entidade
import kotlin.reflect.KClass
import kotlin.reflect.full.*


/**
 * Implementação da biblioteca XML.
 */
class XMLLibraryImpl : XMLLibrary {


    /**
     * Anotação para definir uma entidade XML.
     *
     * @param name Nome da entidade.
     */
    @Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
    annotation class XmlEntity(
        val name: String
    )

    /**
     * Anotação para definir um atributo XML.
     *
     * @param name Nome do atributo.
     */
    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlAtribute(
        val name: String
    )

    /**
     * Anotação para definir um adaptador XML.
     *
     * @param name Classe do adaptador.
     */
    @Target(AnnotationTarget.CLASS)
    annotation class XmlAdapter(
        val name: KClass<*>
    )

    /**
     * Anotação para definir uma string XML.
     *
     * @param name Classe alteradora da string.
     */
    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlString(
        val name: KClass<*>
    )


    /**
     * Anotação para esconder um elemento XML.
     */
    @Target(AnnotationTarget.PROPERTY)
    annotation class XmlHide()

    /**
     * Cria uma entidade a partir de um objeto.
     *
     * @param obj Objeto do qual se deseja criar a entidade.
     * @return Entidade criada.
     */
    override fun criarEntidade(obj: Any): Entidade {
        val clazz = obj::class
        //Verfica se é uma data class
        require(clazz.isData) { "A classe fornecida deve ser uma data class." }

        // Obtém o nome da entidade a partir da anotação @XmlEntity ou do nome da classe.
        val xmlEntity = clazz.findAnnotation<XmlEntity>()?.name ?: clazz.simpleName
        val properties = clazz.declaredMemberProperties

        // Cria uma nova entidade com o nome obtido.
        var entidade = Entidade(xmlEntity ?: "Entidade", null)
        for (property in properties) {
            val propertyName = property.name

            // Verifica se a propriedade tem a anotação @XmlEntity.
            if (property.hasAnnotation<XmlEntity>()){
                val type = property.returnType.classifier

                //Se o tipo da propriedade for uma lista
                if  (type == List::class){
                    val elementos = property.getter.call(obj) as List<*>
                    // Verifica se a propriedade tem a anotação @XmlHide.
                    if (property.hasAnnotation<XmlHide>()){
                        // Adiciona cada elemento da lista como subentidade da entidade principal.
                        for(elemento in elementos){
                            if (elemento != null) {
                                adicionarEntidade(elemento, entidade)
                            }
                        }
                    } else {
                        // Caso contrário, cria uma subentidade para a lista e adiciona os elementos
                        val subEntidade = Entidade(propertyName ?: "Entidade", entidade)
                        entidade.addSubEntidade(subEntidade)
                        for(elemento in elementos){
                            if (elemento != null) {
                                adicionarEntidade(elemento, subEntidade)
                            }
                        }
                    }
                }
                //Se o tipo for String/Double/etc, cria uma subentidade
                else {
                    //Vai buscar o valor à variável correspondente à actual propriedade
                    val subEntidade = Entidade(propertyName ?: "Entidade", entidade)
                    val valor = property.getter.call(obj)
                    subEntidade.setTextoAninhado(valor.toString())
                    entidade.addSubEntidade(subEntidade)
                }

            }
            // Verifica se a propriedade tem a anotação @XmlAtribute.
            else if (property.hasAnnotation<XmlAtribute>()){
                if (property.hasAnnotation<XmlString>()){
                    var valor = property.getter.call(obj)
                    val adapter = property.findAnnotation<XmlString>()!!.name
                    val instance = adapter.primaryConstructor!!.call()

                    for (function in adapter.functions){
                        if(!function.name.equals("toString") && !function.name.equals("equals") && !function.name.equals("hashCode")){
                            valor = function.call(instance, valor.toString())
                        }
                    }
                    entidade.adicionarAtributo(propertyName ?: "Atributo", valor.toString())
                } else {
                    // Adiciona a propriedade como um atributo da entidade.
                    //Vai buscar o valor à variável correspondente à atual propriedade
                    val valor = property.getter.call(obj) as String
                    entidade.adicionarAtributo(propertyName ?: "Atributo", valor)
                }
            }
        }
        // Verifica se a classe tem a anotação @XmlAdapter
        if (clazz.hasAnnotation<XmlAdapter>()){
            // Obtém a classe do adaptador e cria uma instância dele
            val adapter = clazz.findAnnotation<XmlAdapter>()!!.name
            val instance = adapter.primaryConstructor!!.call()
            // Chama todos os métodos do adapter na entidade, exceto os métodos padrão
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
     * @return String formatada como XML.
     */
    override fun prettyPrint(entidade: Entidade): String {
        val output = StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<${entidade.getNome()}")

        // Adiciona os atributos da entidade ao XML.
        for (atributo in entidade.getAtributos()) {
            output.append(" ").append(atributo.printAtributo())
        }

        // Quando a entidade não tem subentidades nem texto aninhado
        if (entidade.getEntidades().isEmpty() && (entidade.getTextoAninhado() == null || entidade.getTextoAninhado()!!.isEmpty())) {
            output.append("/>")
        }
        // Quando a entidade tem subentidades e/ou texto aninhado
        else {
            // Quando a entidade não tem subentidades, mas tem texto aninhado
            if (entidade.getEntidades().isEmpty()){
                output.append(">")
                output.append(entidade.getTextoAninhado())
                output.append("</${entidade.getNome()}>")
            }
            // Quando a entidade tem subentidades
            else {
                output.append(">\n")

                // Adiciona cada subentidade ao XML, com indentação
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

                // Por cada subentidade da entidade enviada, procura por entidades que correspondam ao novo xPath
                for (subEntidade in entidade.getEntidades()){
                    if (subEntidade.getNome() == caminhos.get(1)){
                        microXPath(subEntidade, newXPath, resultado)
                    }
                }
            }
            return resultado.toList()
        }
    }

    /**
     * Gera um documento XML a partir de um objeto.
     *
     * @param obj Objeto a ser convertido em XML.
     * @return String formatada como XML.
     */
    override fun gerarXML(obj: Any): String {
        return prettyPrint(criarEntidade(obj))
    }

    /**
     * Auxiliar para procurar entidades usando caminho XPath.
     *
     * @param entidade Entidade inicial para a busca.
     * @param xPath Caminho XPath para a busca.
     * @param resultado Lista de entidades encontradas.
     */
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
     * Pretty print da entidade e seus filhos.
     *
     * @param entidade Entidade a ser impressa.
     * @param profundidade Nível de profundidade na hierarquia XML.
     * @return String com os dados da entidade e filhos.
     */
    fun printEntidade(entidade: Entidade, profundidade: Int): String {
        val output = StringBuilder("")
        output.append(getTabbed(profundidade))

        // Adiciona o nome da entidade ao XML.
        output.append("<${entidade.getNome()}")

        // Adiciona os atributos da entidade ao XML
        for (atributo in entidade.getAtributos()) {
            output.append(" ").append(atributo.printAtributo())
        }

        // Quando a entidade não tem subentidades nem texto aninhado
        if (entidade.getEntidades().isEmpty() && (entidade.getTextoAninhado() == null || entidade.getTextoAninhado()!!.isEmpty())) {
            output.append("/>")
        }
        // Quando a entidade tem subentidades e/ou texto aninhado
        else {
            // Quando a entidade não tem subentidades, mas tem texto aninhado
            if (entidade.getEntidades().isEmpty()){
                output.append(">")
                output.append(entidade.getTextoAninhado())
                output.append("</${entidade.getNome()}>")
            }
            // Quando a entidade tem subentidades
            else {
                output.append(">\n")

                // Adiciona cada subentidade ao XML, com incremento na profundidade para a indentação correta
                for (subEntidade in entidade.getEntidades()) {
                    output.append(printEntidade(subEntidade, profundidade+1)).append("\n")
                }

                // Adiciona a indentação de fecho
                output.append(getTabbed(profundidade))

                // Quando também tem texto aninhado, adiciona o texto aninhado com incremento na profundidade para a indentação correta
                if(entidade.getTextoAninhado() != null && entidade.getTextoAninhado()!!.isNotEmpty()){
                    output.append(getTabbed(profundidade +1 ))
                    output.append(entidade.getTextoAninhado()).append("\n")
                }
                // Fecha a tag da entidade
                output.append("</${entidade.getNome()}>")
            }
        }
        // Retorna a string de saída contendo o XML
        return output.toString()
    }

    /**
     * Gera um string com indentação de tabulação.
     *
     * @param nivel Nível de profundidade para indentação.
     * @return String com espaços de indentação.
     */
    private fun getTabbed(depth: Int): String {
        val output = StringBuilder("")
        var i = 0
        // Adiciona uma tabulação à string de saída para cada nível de profundidade
        while (i < depth) {
            output.append("\t")
            i++
        }
        return output.toString()
    }

    /**
     * Adiciona entidade como subentidade de outra entidade.
     *
     * @param elemento Elemento a ser adicionado como subentidade.
     * @param entidade Entidade mãe.
     */
    fun adicionarEntidade(elemento: Any, entidade: Entidade) {
        val clazz = elemento::class
        require(clazz.isData) { "A classe fornecida deve ser uma data class." }

        // Obtém o nome da entidade XML a partir da anotação @XmlEntity ou o nome simples da classe.
        val xmlEntityName = clazz.findAnnotation<XmlEntity>()?.name ?: clazz.simpleName
        val properties = clazz.declaredMemberProperties

        // Cria uma subentidade e adiciona-a à entidade fornecida
        var subEntidade = Entidade(xmlEntityName ?: "Entidade", entidade)
        entidade.addSubEntidade(subEntidade)
        for (property in properties) {
            val propertyName = property.name

            if (property.hasAnnotation<XmlEntity>()){
                val type = property.returnType.classifier
                // Se o tipo da propriedade for uma lista
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
                }
                // Se o tipo não for uma lista, cria uma sub-subentidade
                else {
                    // Obtém o valor da propriedade e define-o como texto aninhado na sub-subentidade
					val subSubEntidade = Entidade(propertyName ?: "Entidade", entidade)
                    val valor = property.getter.call(elemento)
                    subSubEntidade.setTextoAninhado(valor.toString())
                    subEntidade.addSubEntidade(subSubEntidade)
                }

            }
            else if (property.hasAnnotation<XmlAtribute>()){
                if (property.hasAnnotation<XmlString>()){
                    var valor = property.getter.call(elemento)
                    val adapter = property.findAnnotation<XmlString>()!!.name
                    val instance = adapter.primaryConstructor!!.call()

                    for (function in adapter.functions){
                        if(!function.name.equals("toString") && !function.name.equals("equals") && !function.name.equals("hashCode")){
                            valor = function.call(instance, valor.toString())
                        }
                    }
                    subEntidade.adicionarAtributo(propertyName ?: "Atributo", valor.toString())
                } else {
                    // Se a propriedade tiver a anotação @XmlAtribute, adiciona-a como atributo à subentidade
                    val valor = property.getter.call(elemento)
                    subEntidade.adicionarAtributo(propertyName ?: "Atributo", valor.toString())
                }
            }
        }
        // Se a classe tiver a anotação @XmlAdapter, aplica o adaptador à subentidade
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
     * Este método percorre recursivamente a hierarquia XML e adiciona o atributo especificado
     * à entidade com o nome correspondente.
     *
     * @param entidade Entidade à qual queremos adicionar o atributo a entidade(s)
     * @param nomeEntidade Nome da entidade à qual queremos adicionar o atributo
     * @param nomeAtributo Nome do novo atributo
     * @param valorAtributo Valor do novo atributo
     */
    fun adicionarAtributoGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorAtributo: String) {
        if (nomeEntidade.isNotEmpty() && nomeAtributo.isNotEmpty() && valorAtributo.isNotEmpty()) {
            // Se o nome da entidade atual corresponde ao nome da entidade fornecida, adiciona o atributo
            if (entidade.getNome().equals(nomeEntidade)){
                entidade.adicionarAtributo(nomeAtributo, valorAtributo)
            }

            // Itera sobre cada subentidade da entidade atual e aplica a função recursivamente
            for(subEntidade in entidade.getEntidades()){
                adicionarAtributoGlobalmente(subEntidade, nomeEntidade, nomeAtributo, valorAtributo)
            }
        }
    }

    /**
     * Renomeia todas as entidades com o nome dado para o novo valor
     *
     * @param entidade Entidade onde se inicia a renomeação.
     * @param nomeAntigo Nome que se pretende alterar.
     * @param nomeNovo Nome que se pretende definir.
     */
    fun renomearEntidadesGlobalmente(entidade: Entidade, nomeAntigo: String, nomeNovo: String){
        if (nomeAntigo.isNotEmpty() && nomeNovo.isNotEmpty() && !nomeNovo.contains(" ")) {
            // Se o nome da entidade atual corresponder ao nome antigo, renomeia a entidade com o novo nome
            if (entidade.getNome() == nomeAntigo) {
                entidade.renomearEntidade(nomeNovo)
            }
            // Itera sobre cada subentidade da entidade atual e aplica a função recursivamente
            for (subEntidade in entidade.getEntidades()) {
                renomearEntidadesGlobalmente(subEntidade, nomeAntigo, nomeNovo)
            }
        }
    }

    /**
     * Remove todas as entidades com o nome dado
     *
     * @param entidade Entidade que se pretende encontrar a entidade a Remover
     * @param nome Nome das entidades que se pretende remover
     */
    fun removerEntidadesGlobalmente(entidade: Entidade, nome: String){
        //Verifica se está vazio
        if (nome.isNotEmpty()) {
            // Lista para armazenar as entidades que serão removidas
            var entidadesParaRemover = mutableListOf<Entidade>()

            // Obtém todas as entidades da entidade atual
            val entidades = entidade.getEntidades()
            for (subEntidade in entidades) {
                // Se o nome da subentidade corresponder ao nome fornecido, adiciona à lista de entidades para remover
                if (subEntidade.getNome() == nome) {
                    entidadesParaRemover.add(subEntidade)
                } else {
                    // Caso contrário, aplica a função recursivamente para verificar as subentidades aninhadas
                    removerEntidadesGlobalmente(subEntidade, nome)
                }
            }

            // Remove as entidades armazenadas na lista `entidadesParaRemover` da lista de entidades da entidade atual
            entidade.getEntidades().removeAll(entidadesParaRemover)
        }
    }

    /**
     * Renomeia todos os atributos com o nome dado para o novo valor
     *
     * @param entidade Entidade onde se inicia a renomeação.
     * @param nomeAntigo Nome que se pretende alterar.
     * @param nomeNovo Nome que se pretende definir.
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
     * Remove todos os atributos com o nome dado a entidades com o nome dado
     *
     * @param entidade Entidade onde se inicia a remoção dos atributos.
     * @param nomeEntidade Nome da entidade à qual se pretende remover o atributo.
     * @param nomeAtributo Nome do atributo que se pretende remover.
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
     * Altera todas os atributos com o nome dado a entidades com o nome dado
     *
     * @param entidade Entidade onde se inicia a alteração dos atributos.
     * @param nomeEntidade Nome da entidade à qual se pretende alterar o atributo.
     * @param nomeAtributo Nome do atributo que se pretende alterar.
     * @param valorNovo Novo valor para o atributo.
     */
   /* fun alterarAtributosGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorNovo: String){
        if (nomeEntidade.isNotEmpty() && nomeAtributo.isNotEmpty() && valorNovo.isNotEmpty()){
            if (entidade.getNome() == nomeEntidade){
                entidade.alterarAtributo(nomeAtributo, valorNovo)
            }
            for (subEntidade in entidade.getEntidades()){
                alterarAtributosGlobalmente(subEntidade, nomeEntidade, nomeAtributo, valorNovo)
            }
        }
    }*/

    /**
     * Adiciona uma subEntidade à entidade dada
     *
     * @param entidade Entidade à qual se pretende adicionar a nova subentidade.
     * @param nomeSubEntidade Nome da nova subentidade a ser adicionada.
     * @param nomeEntidadePai Nome da entidade à qual se pretende adicionar a subentidade.
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