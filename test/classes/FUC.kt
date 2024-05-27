package classes

import adapters.FUCAdapter
import impl.XMLLibraryImpl.XmlHide
import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlAtribute
import impl.XMLLibraryImpl.XmlAdapter

@XmlEntity("FUC")
@XmlAdapter(FUCAdapter::class)
data class FUC (
    @XmlAtribute("codigo")
    val codigo: String,
    @XmlEntity("nome")
    val nome: String,
    @XmlEntity("ects")
    val ects: Double,
    @XmlEntity("observacoes")
    val observacoes: String,
    @XmlEntity("avaliacao")
    @XmlHide
    val avaliacao: List<ComponenteAvaliacao>
    )
