package classes

import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlHide

@XmlEntity("plano")
data class Plano (

    @XmlEntity("fucs")
    @XmlHide
    val fucs: List<FUC>
)