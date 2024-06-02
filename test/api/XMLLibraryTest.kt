package api

import classes.ComponenteAvaliacao
import classes.FUC
import classes.Plano
import impl.XMLLibraryImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class XMLLibraryTest {

    private val lib = XMLLibraryImpl()

    /*
    * Testa se a biblioteca consegue criar uma entidade
    * a partir de um determinado objeto (como FUC ou Plano) corretamente
     */
    @Test
    fun testeCriarEntidade() {
        // Cria um objeto ComponenteAvaliacao
        val componenteAvaliacao1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2 = ComponenteAvaliacao("Projeto", 80)
        // Cria um objeto FUC
        val fuc = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1, componenteAvaliacao2))
        // Cria uma entidade a partir do objeto FUC usando XMLLibraryImpl
        val entidade = lib.criarEntidade(fuc)
        // Asserts para testar se a entidade foi criada corretamente
        assertEquals(entidade.getNome(), "FUC")
        assertFalse(entidade.getEntidades().isEmpty())
        assert(entidade.getEntidades().size.equals(4))
        assertFalse(entidade.getAtributos().isEmpty())
        assert(entidade.getAtributos().size.equals(1))
        //println(lib.prettyPrint(entidade))
    }

    /*
    * Tem uma estrutura diferente a do anterior
     */
    @Test
    fun testeCriarEntidade2() {
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        // Cria outro conjunto de objetos ComponenteAvaliacao e um objeto FUC
        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        //Cria um objeto Plano
        val plano = Plano(listOf(fuc1, fuc2))
        //Cria uma entidade a partir do objeto Plano usando XMLLibraryImpl
        val entidade = lib.criarEntidade(plano)

        assertEquals(entidade.getNome(), "plano")
        assertFalse(entidade.getEntidades().isEmpty())
        assert(entidade.getEntidades().size.equals(2))
        assert(entidade.getAtributos().isEmpty())
        //println(lib.prettyPrint(entidade))
    }

    /*
    * Testa se a biblioteca lança uma exceção ao tentar criar uma entidade
    * a partir de um objeto que não é uma classe de dados
     */
    @Test
    fun testCriarEntidadeWithInvalidDataClass() {
        // Define uma classe não de dados para teste
        class NonDataClass(val attribute: String)
        //Cria uma instância da classe não de dados
        val obj = NonDataClass(attribute = "value")
        //Asserts para testar se uma IllegalArgumentException é lançada ao criar uma entidade
        assertThrows<IllegalArgumentException> {
            lib.criarEntidade(obj)
        }
    }

    /*
    * Testa se a biblioteca consegue fazer o prettyprint
    * corretamente de uma entidade num formato XML.
    */
    @Test
    fun testePrettyPrint(){
        val componenteAvaliacao1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2 = ComponenteAvaliacao("Projeto", 80)
        val fuc = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1, componenteAvaliacao2))
        val entidade = lib.criarEntidade(fuc)
        assertEquals(lib.prettyPrint(entidade), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<FUC codigo=\"M4310\">\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                "</FUC>")
    }

    /*
    *Igual ao anterior, mas se há um caso de falha
     */
    @Test
    fun testePrettyPrintFail(){
        val componenteAvaliacao1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2 = ComponenteAvaliacao("Projeto", 80)
        val fuc = FUC("", "", 6.0,
            "", listOf(componenteAvaliacao1, componenteAvaliacao2))
        val entidade = lib.criarEntidade(fuc)
        //println(lib.prettyPrint(entidade))
        assertNotEquals(lib.prettyPrint(entidade), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<FUC codigo=\"\">\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<nome/>\n" +
                "\t<componente nome=\"Quizzes\" peso=\"10\"/>\n" +
                "\t<componente nome=\"Projeto\" peso=\"90\"/>\n" +
                "</FUC>")
    }

    /*
    *Testa a função micro XPath
     */
    @Test
    fun testeMicroXPathProfundidadeSimples(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)

        val entidade = lib.criarEntidade(componenteAvaliacao1fuc1)
        val resultados = lib.microXPath(entidade, "componente")

        assert(resultados.size == 1)
        assert(resultados.all { it.getNome() == "componente" })

        val value = StringBuilder("")

        val esperado = "<componente nome=\"Quizzes\" peso=\"20%\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

    /*
    *Testa um caso em que a string de consulta está vazia
    */
    @Test
    fun testeMicroXPathProfundidadeSimplesVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)

        val entidade = lib.criarEntidade(componenteAvaliacao1fuc1)
        val resultados = lib.microXPath(entidade, "")

        assert(resultados.size == 1)
        assert(resultados.all { it.getNome() == "componente" })

        val value = StringBuilder("")

        val esperado = "<componente nome=\"Quizzes\" peso=\"20%\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

    /*
    *Testa a função micro XPath com uma profundidade de 1.
     */
    @Test
    fun testeMicroXPathProfundidade1(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)
        val resultados = lib.microXPath(entidade, "plano")

        assert(resultados.size == 1)
        assert(resultados.all { it.getNome() == "plano" })

        val value = StringBuilder("")

        val esperado = "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

    /*
    *Testa a função micro XPath com uma profundidade de 3.
     */
    @Test
    fun testeMicroXPathProfundidade3(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)
        val resultados = lib.microXPath(entidade, "plano/FUC/componente")

        assert(resultados.size == 5)
        assert(resultados.all { it.getNome() == "componente" })

        val value = StringBuilder("")

        val esperado = "<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "<componente nome=\"Discussão\" peso=\"20%\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

    /*
    * Testa se a biblioteca consegue gerar XML
    * de um determinado objeto (como Plano) corretamente
     */
    @Test
    fun testeGerarXML(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val plano = Plano(listOf(fuc1, fuc2))
        val resultado = lib.gerarXML(plano)

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        assertEquals(resultado, esperado)
    }

    @Test
    fun testeGerarXmlWithInvalidDataClass() {
        class NonDataClass(val attribute: String)

        val obj = NonDataClass(attribute = "value")

        assertThrows<IllegalArgumentException> {
            lib.gerarXML(obj)
        }
    }

    /*
    *Testa se a biblioteca consegue adicionar atributos globalmente
    * a todas as entidades de um determinado tipo.
     */
    @Test
    fun testeAdiconarAtributoGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        lib.adicionarAtributoGlobalmente(entidade, "componente", "valorMaximo", "20")

        val resultado1 = lib.prettyPrint(entidade)
        val esperado1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\" valorMaximo=\"20\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\" valorMaximo=\"20\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        assertEquals(resultado1, esperado1)

        lib.adicionarAtributoGlobalmente(entidade, "componente", "valor Maximo", "20")

        val resultado2 = lib.prettyPrint(entidade)
        val esperado2 = esperado1

        assertEquals(resultado2, esperado2)

        lib.adicionarAtributoGlobalmente(entidade, "componente", "valorMaximo", "20")

        val resultado3 = lib.prettyPrint(entidade)
        val esperado3 = esperado1
        assertEquals(resultado3, esperado3)
    }

    @Test
    fun testeAdiconarAtributoGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        lib.adicionarAtributoGlobalmente(entidade, "", "valorMaximo", "20")

        val resultado1 = lib.prettyPrint(entidade)
        val esperado1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<FUC codigo=\"M4310\">\n" +
                "\t\t<ects>6.0</ects>\n" +
                "\t\t<nome>Programação Avançada</nome>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                "\t</FUC>\n" +
                "\t<FUC codigo=\"03782\">\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                "\t</FUC>\n" +
                "</plano>"

        assertEquals(resultado1, esperado1)

        lib.adicionarAtributoGlobalmente(entidade, "componente", "", "20")

        val resultado2 = lib.prettyPrint(entidade)
        val esperado2 = esperado1

        assertEquals(resultado2, esperado2)

        lib.adicionarAtributoGlobalmente(entidade, "componente", "valorMaximo", "")

        val resultado3 = lib.prettyPrint(entidade)
        val esperado3 = esperado1
        assertEquals(resultado3, esperado3)
    }

    /*
    * Testa se a biblioteca pode renomear entidades globalmente.
     */
    @Test
    fun testeRenomearEntidadesGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<FUC codigo=\"M4310\">\n" +
                "\t\t<ects>6.0</ects>\n" +
                "\t\t<nome>Programação Avançada</nome>\n" +
                "\t\t<componenteAvaliacao nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Projeto\" peso=\"80%\"/>\n" +
                "\t</FUC>\n" +
                "\t<FUC codigo=\"03782\">\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<componenteAvaliacao nome=\"Dissertação\" peso=\"60%\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Apresentação\" peso=\"20%\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Discussão\" peso=\"20%\"/>\n" +
                "\t</FUC>\n" +
                "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)
        lib.renomearEntidadesGlobalmente(entidade, "componente", "componenteAvaliacao")

        val resultado2 = lib.prettyPrint(entidade)


        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeRenomearEntidadesGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<FUC codigo=\"M4310\">\n" +
                "\t\t<ects>6.0</ects>\n" +
                "\t\t<nome>Programação Avançada</nome>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                "\t</FUC>\n" +
                "\t<FUC codigo=\"03782\">\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                "\t</FUC>\n" +
                "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)
        lib.renomearEntidadesGlobalmente(entidade, "", "componenteAvaliacao")

        val resultado2 = lib.prettyPrint(entidade)


        assertEquals(resultado2, esperado)
    }

    /*
    * Testa se a biblioteca pode remover entidades globalmente.
    */
    @Test
    fun testeRemoverEntidadesGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)

        lib.removerEntidadesGlobalmente(entidade, "componente")

        val resultado2 = lib.prettyPrint(entidade)


        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeRemoverEntidadesGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)

        lib.removerEntidadesGlobalmente(entidade, "")

        val resultado2 = lib.prettyPrint(entidade)


        assertEquals(resultado2, esperado)
    }

    /*
    * Testa se a biblioteca pode renomear atributos globalmente.
     */
    @Test
    fun testeRenomearAtributosGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC identificador=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC identificador=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)

        lib.renomearAtributosGlobalmente(entidade, "codigo", "identificador")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeRenomearAtributosGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)

        lib.renomearAtributosGlobalmente(entidade, "", "identificador")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }
    
    /**
     * Testa se a biblioteca consegue remover atributos globalmente.
     */
    @Test
    fun testeRemoverAtributosGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\"/>\n" +
                        "\t\t<componente nome=\"Projeto\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\"/>\n" +
                        "\t\t<componente nome=\"Discussão\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)

        lib.removerAtributosGlobalmente(entidade, "componente", "peso")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeRemoverAtributosGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)

        lib.removerAtributosGlobalmente(entidade, "", "peso")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    /*
    * Testa se a biblioteca consegue alterar valores de atributos globalmente.
     */
    @Test
    fun testeAlterarAtributosGlobalmente(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"10\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"10\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"10\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"10\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"10\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)

        lib.alterarAtributosGlobalmente(entidade, "componente", "peso", "10")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeAlterarAtributosGlobalmenteVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)

        lib.alterarAtributosGlobalmente(entidade, "componente", "", "10")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    /*
    * Testa se a biblioteca consegue adicionar sub-entidades
    * a uma entidade existente.
     */
    @Test
    fun testeAdicionarSubEntidade(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t\t<regente/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t\t<regente/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertNotEquals(resultado1, esperado)

        lib.adicionarSubEntidade(entidade, "regente","FUC")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }

    @Test
    fun testeAdicionarSubEntidadeVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2fuc1 = ComponenteAvaliacao("Projeto", 80)
        val fuc1 = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1fuc1, componenteAvaliacao2fuc1))

        val componenteAvaliacao1fuc2 = ComponenteAvaliacao("Dissertação", 60)
        val componenteAvaliacao2fuc2 = ComponenteAvaliacao("Apresentação", 20)
        val componenteAvaliacao3fuc2 = ComponenteAvaliacao("Discussão", 20)
        val fuc2 = FUC("03782", "Dissertação", 42.0,
            "la la...", listOf(componenteAvaliacao1fuc2, componenteAvaliacao2fuc2, componenteAvaliacao3fuc2))

        val esperado = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<plano>\n" +
                        "\t<FUC codigo=\"M4310\">\n" +
                        "\t\t<ects>6.0</ects>\n" +
                        "\t\t<nome>Programação Avançada</nome>\n" +
                        "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        val plano = Plano(listOf(fuc1, fuc2))
        val entidade = lib.criarEntidade(plano)

        val resultado1 = lib.prettyPrint(entidade)
        assertEquals(resultado1, esperado)

        lib.adicionarSubEntidade(entidade, "","FUC")

        val resultado2 = lib.prettyPrint(entidade)
        assertEquals(resultado2, esperado)
    }
}