package classes

import adapters.AddPercentage
import impl.XMLLibraryImpl.XmlEntity
import impl.XMLLibraryImpl.XmlAtribute
import impl.XMLLibraryImpl.XmlString

// Define a classe ComponenteAvaliacao, que representa um componente de avaliação.
@XmlEntity("componente")
data class ComponenteAvaliacao (
    // Declaração da propriedade nome, que armazena o nome do componente de avaliação.
    @XmlAtribute("nome")
    val nome: String,

    // Declaração da propriedade peso, que armazena o peso do componente de avaliação.
    @XmlAtribute("peso")
    // Usa a anotação @XmlString para especificar que o valor do peso será processado como uma string XML.
    // O adaptador AddPercentage pode ser usado para modificar o valor antes de serializá-lo ou desserializá-lo.
    // Neste caso, parece que está adicionando um símbolo de percentagem ao valor do peso.
    @XmlString(AddPercentage::class)
    val peso: Int
)

