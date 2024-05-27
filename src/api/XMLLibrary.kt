package api

import classes.Entidade

interface XMLLibrary {

    fun criarEntidade(obj: Any): Entidade

    fun prettyPrint(entidade: Entidade): String

    fun microXPath(entidade: Entidade, xPath: String): List<Entidade>

    fun gerarXML(obj: Any): String

}