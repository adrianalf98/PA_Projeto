package adapters

import classes.Atributo
import classes.Entidade
import java.util.*

/**
 * Classe responsável por adaptar uma entidade FUC.
 */
internal class FUCAdapter {

    /**
     * Ordena as subentidades e atributos de uma entidade FUC.
     *
     * @param entidade Entidade FUC a ser ordenada.
     * @return Entidade FUC com subentidades e atributos ordenados.
     */
    fun sort(entidade: Entidade): Entidade {
        // Define a ordem desejada para as subentidades
        val ordemSubEntidades = listOf<String>("ects", "nome", "componente")
        // Define a ordem desejada para os atributos
        val ordemAtributos = listOf<String>("codigo")

        // Listas para armazenar as subentidades e atributos ordenados
        var novaListaEntidades: MutableList<Entidade> = ArrayList()
        var novaListaAtributos: MutableList<Atributo> = ArrayList()

        // Ordena as subentidades de acordo com a ordem desejada
        for (caso in ordemSubEntidades){
            for (subEntidade in entidade.getEntidades()){
                if (subEntidade.getNome().equals(caso)){
                    novaListaEntidades.add(subEntidade)
                }
            }
        }

        // Ordena os atributos de acordo com a ordem desejada
        for (caso in ordemAtributos){
            for (atributo in entidade.getAtributos()){
                if (atributo.getNome().equals(caso)){
                    novaListaAtributos.add(atributo)
                }
            }
        }

        // Define as novas subentidades e atributos ordenados na entidade
        entidade.setEntidades(novaListaEntidades)
        entidade.setAtributos(novaListaAtributos)
        return entidade
    }
}
