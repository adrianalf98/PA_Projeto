package adapters

/**
 * Classe responsável por adicionar percentagem a uma string.
 */
class AddPercentage {

    /**
     * Adiciona um símbolo de percentagem (%) ao texto fornecido.
     *
     * @param text Texto ao qual será adicionado o símbolo de percentagem.
     * @return Texto com o símbolo de percentagem adicionado.
     */
    fun addPercentages(text: String): String {
        return "$text%"
    }
}