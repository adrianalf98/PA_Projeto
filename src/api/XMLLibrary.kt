package api

import classes.Entidade

interface XMLLibrary {

    /**
     * Cria uma entidade XML a partir de um objeto fornecido.
     *
     * @param obj Objeto a partir do qual será criada a entidade XML
     * @return A entidade XML criada
     */
    fun criarEntidade(obj: Any): Entidade

    /**
     * Gera uma representação em string XML bem formatada a partir de uma entidade XML fornecida.
     *
     * @param entidade Entidade XML para a qual será gerada a representação XML bem formatada.
     * @return String contendo a representação XML formatada da entidade.
     */
    fun prettyPrint(entidade: Entidade): String

    /**
     * Realiza uma consulta microXPath em uma entidade XML fornecida, retornando uma lista de entidades que correspondem ao caminho especificado.
     *
     * @param entidade Entidade XML na qual será realizada a consulta microXPath.
     * @param xPath Caminho microXPath a ser consultado na entidade XML.
     * @return Lista de entidades que correspondem ao caminho microXPath na entidade XML.
     */
    fun microXPath(entidade: Entidade, xPath: String): List<Entidade>

    /**
     * Gera uma representação em string XML a partir de um objeto fornecido.
     *
     * @param obj Objeto a partir do qual será gerada a representação XML.
     * @return String contendo a representação XML do objeto.
     */
    fun gerarXML(obj: Any): String

}