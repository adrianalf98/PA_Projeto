package adapters

import classes.Atributo
import classes.Componente
import classes.Entidade
import java.util.*

internal class FUCAdapter {
    /*fun hideFucs(entidade: Entidade): Entidade {
        if (entidade.getNome().equals("FUC")){
            val entidades = entidade.getEntidades().toMutableList()
            for (subEntidade in entidades){
                if(subEntidade.getNome().equals("avaliacao")){
                    for (componente in subEntidade.getEntidades()){
                        entidade.addSubEntidade(componente)
                    }
                    entidade.removeSubEntidade(subEntidade)
                }

            }
        }
        return entidade
    }*/

    fun sort(entidade: Entidade): Entidade {
        val ordemSubEntidades = listOf<String>("ects", "nome", "componente")
        val ordemAtributos = listOf<String>("codigo")

        var novaListaEntidades: MutableList<Entidade> = ArrayList()
        var novaListaAtributos: MutableList<Atributo> = ArrayList()

        for (caso in ordemSubEntidades){
            for (subEntidade in entidade.getEntidades()){
                if (subEntidade.getNome().equals(caso)){
                    novaListaEntidades.add(subEntidade)
                }
            }
        }

        for (caso in ordemAtributos){
            for (atributo in entidade.getAtributos()){
                if (atributo.getNome().equals(caso)){
                    novaListaAtributos.add(atributo)
                }
            }
        }

        entidade.setEntidades(novaListaEntidades)
        entidade.setAtributos(novaListaAtributos)
        return entidade
    }
}
