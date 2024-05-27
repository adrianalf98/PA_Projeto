package classes

import adapters.AddPercentage
import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlAtribute
import impl.XMLLibraryImpl.XmlString

@XmlEntity("componente")
data class ComponenteAvaliacao (
    @XmlAtribute("nome")
    val nome: String,

    @XmlAtribute("peso")
    @XmlString(AddPercentage::class)
    val peso: Int
)

