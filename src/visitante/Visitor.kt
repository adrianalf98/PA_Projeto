package visitante

import classes.Atributo
import classes.Entidade

interface Visitor {

    fun visit(c: Entidade): Boolean = true

    fun endVisit(c: Entidade) {}

    fun visit(c: Atributo): Boolean = true

    fun endVisit(c: Atributo) {}

}