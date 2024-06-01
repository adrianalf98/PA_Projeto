package classes

import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlHide
// Define a classe Plano, que representa um plano de estudos
@XmlEntity("plano")
data class Plano (

    // Propriedade que armazena a lista de FUCs do plano de estudos.
    // A anotação @XmlHide especifica que esta propriedade não será incluída na representação XML.
    @XmlEntity("fucs")
    @XmlHide
    val fucs: List<FUC>
)