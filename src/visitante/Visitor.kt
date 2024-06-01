package visitante

import classes.Atributo
import classes.Entidade

/**
 * Interface que define o comportamento de um visitante.
 */
interface Visitor {

    /**
     * Visita uma entidade.
     *
     * @param c a entidade a ser visitada
     * @return um booleano indicando se a visita deve continuar
     */
    fun visit(c: Entidade): Boolean = true

    /**
     * Finaliza a visita a uma entidade.
     *
     * @param c a entidade cuja visita está a ser finalizada
     */
    fun endVisit(c: Entidade) {}

    /**
     * Visita um atributo.
     *
     * @param c o atributo a ser visitado
     * @return um booleano indicando se a visita deve continuar
     */
    fun visit(c: Atributo): Boolean = true

    /**
     * Finaliza a visita a um atributo.
     *
     * @param c o atributo cuja visita está a ser finalizada
     */
    fun endVisit(c: Atributo) {}

}