package classes

import adapters.FUCAdapter
import impl.XMLLibraryImpl.XmlHide
import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlAtribute
import impl.XMLLibraryImpl.XmlAdapter

@XmlEntity("FUC") // Anotação para especificar que esta classe será mapeada para uma entidade XML com o nome "FUC"
@XmlAdapter(FUCAdapter::class) // Anotação para especificar o adaptador XML a ser usado para esta classe
data class FUC (
    // Propriedade que armazena o código da unidade curricular
    @XmlAtribute("codigo")
    val codigo: String,
    // Propriedade que armazena o nome da unidade curricular
    @XmlEntity("nome") // Anotação que especifica a propriedade como uma entidade de nome "nome"
    val nome: String,
    // Propriedade que armazena o número de ECTS da unidade curricular
    @XmlEntity("ects")
    val ects: Double,
    // Propriedade que armazena as observações sobre a unidade curricular
    @XmlEntity("observacoes")
    val observacoes: String,
    // Propriedade que armazena a avaliação da unidade curricular
    @XmlEntity("avaliacao")
    // A anotação @XmlHide especifica que esta propriedade não será incluída na representação XML
   @XmlHide
    val avaliacao: List<ComponenteAvaliacao>
    )
