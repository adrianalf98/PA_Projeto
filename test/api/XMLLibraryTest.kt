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

    @Test
    fun testeCriarEntidade() {
        val componenteAvaliacao1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2 = ComponenteAvaliacao("Projeto", 80)
        val fuc = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1, componenteAvaliacao2))
        val entidade = lib.criarEntidade(fuc)
        assertEquals(entidade.getNome(), "FUC")
        assertFalse(entidade.getEntidades().isEmpty())
        assert(entidade.getEntidades().size.equals(4))
        assertFalse(entidade.getAtributos().isEmpty())
        assert(entidade.getAtributos().size.equals(1))
        //println(lib.prettyPrint(entidade))
    }

    @Test
    fun testeCriarEntidade2() {
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
        assertEquals(entidade.getNome(), "plano")
        assertFalse(entidade.getEntidades().isEmpty())
        assert(entidade.getEntidades().size.equals(2))
        assert(entidade.getAtributos().isEmpty())
        //println(lib.prettyPrint(entidade))
    }

    @Test
    fun testCriarEntidadeWithInvalidDataClass() {
        class NonDataClass(val attribute: String)

        val obj = NonDataClass(attribute = "value")

        assertThrows<IllegalArgumentException> {
            lib.criarEntidade(obj)
        }
    }

    @Test
    fun testePrettyPrint(){
        val componenteAvaliacao1 = ComponenteAvaliacao("Quizzes", 20)
        val componenteAvaliacao2 = ComponenteAvaliacao("Projeto", 80)
        val fuc = FUC("M4310", "Programação Avançada", 6.0,
            "la la...", listOf(componenteAvaliacao1, componenteAvaliacao2))
        val entidade = lib.criarEntidade(fuc)
        println(lib.prettyPrint(entidade))
        assertEquals(lib.prettyPrint(entidade), "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<FUC codigo=\"M4310\">\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                "\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                "</FUC>")
    }

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

    @Test
    fun testeMicroXPathProfundidadeSimples(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)

        val entidade = lib.criarEntidade(componenteAvaliacao1fuc1)
        val resultados = lib.microXPath(entidade, "componente")

        assert(resultados.size == 1)
        assert(resultados.all { it.getNome() == "componente" })

        val value = StringBuilder("")

        val esperado = "<componente nome=\"Quizzes\" peso=\"20\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

    @Test
    fun testeMicroXPathProfundidadeSimplesVazio(){
        val componenteAvaliacao1fuc1 = ComponenteAvaliacao("Quizzes", 20)

        val entidade = lib.criarEntidade(componenteAvaliacao1fuc1)
        val resultados = lib.microXPath(entidade, "")

        assert(resultados.size == 1)
        assert(resultados.all { it.getNome() == "componente" })

        val value = StringBuilder("")

        val esperado = "<componente nome=\"Quizzes\" peso=\"20\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

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
                        "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

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

        val esperado = "<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                        "<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                        "<componente nome=\"Dissertação\" peso=\"60\"/>\n" +
                        "<componente nome=\"Apresentação\" peso=\"20\"/>\n" +
                        "<componente nome=\"Discussão\" peso=\"20\"/>\n"
        for (resultado in resultados){
            value.append(lib.printEntidade(resultado, 0)).append("\n")
        }

        assertEquals(value.toString(), esperado)
    }

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
                        "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20\"/>\n" +
                        "\t</FUC>\n" +
                        "</plano>"

        assertEquals(resultado, esperado)
    }

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
                        "\t\t<componente nome=\"Quizzes\" peso=\"20\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80\" valorMaximo=\"20\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20\" valorMaximo=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20\" valorMaximo=\"20\"/>\n" +
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
                "\t\t<componenteAvaliacao nome=\"Quizzes\" peso=\"20\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Projeto\" peso=\"80\"/>\n" +
                "\t</FUC>\n" +
                "\t<FUC codigo=\"03782\">\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<componenteAvaliacao nome=\"Dissertação\" peso=\"60\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Apresentação\" peso=\"20\"/>\n" +
                "\t\t<componenteAvaliacao nome=\"Discussão\" peso=\"20\"/>\n" +
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
                        "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC identificador=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20\"/>\n" +
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
                        "\t\t<componente nome=\"Quizzes\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Projeto\" peso=\"80\"/>\n" +
                        "\t\t<regente/>\n" +
                        "\t</FUC>\n" +
                        "\t<FUC codigo=\"03782\">\n" +
                        "\t\t<ects>42.0</ects>\n" +
                        "\t\t<nome>Dissertação</nome>\n" +
                        "\t\t<componente nome=\"Dissertação\" peso=\"60\"/>\n" +
                        "\t\t<componente nome=\"Apresentação\" peso=\"20\"/>\n" +
                        "\t\t<componente nome=\"Discussão\" peso=\"20\"/>\n" +
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
}