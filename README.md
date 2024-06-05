# XML API

## Visão Geral
Esta API fornece funcionalidades para converter data classes do Kotlin em representações XML, permitindo a impressão bonita de XML, consultas semelhantes a XPath. Para efetuar esta conversão a biblioteca baseia-se nas anotações dos ficheiros de dados inseridos nos métodos.

## Anotações
As seguintes anotações devem ser utilizadas nas classes enviadas nos métodos pretendidos da API de forma a fazer o correto mapeamento dos dados numa estrutura XML

* <strong>@XmlEntity(name: String)</strong>:
  Marca uma classe ou propriedade para ser tratada como uma entidade XML. O parâmetro name especifica o nome da entidade XML. Caso não seja dado um valor e esta anotação esteja associada a uma classe será assumido o nome da classe, caso seja associada a uma propriedade de uma classe será assumido o nome Entidade.

* <strong>@XmlAtribute(name: String)</strong>:
  Marca uma propriedade para ser tratada como um atributo XML. O parâmetro name especifica o nome do atributo XML. Caso não seja dado um valor será assumido o nome Atributo.

* <strong>@XmlAdapter(classe: KClass<*>)</strong>:
  Marca uma classe para usar um adaptador específico para conversão XML. Permite a alteração da estruturação do XML, deve receber uma classe que tenha métodos que recebam Entidades e façam as alterações da maneira pretendida.

* <strong>@XmlString(classe: KClass<*>)</strong>:
  Marca uma propriedade para ser tratada como uma string XML, deve receber uma classe que tenha métodos que recebam uma string e a alterem e devolvam da maneira pretendida.

* <strong>@XmlHide</strong>:
Permite ocultar a entidade mãe de uma lista de entidades. Quando é uma entidade simples(sem subentidades) ou um atributo este comportamento pode ser replicado não utilizando as anotações XmlEntity e XmlAtribute, respetivamente.

## Métodos

A API oferece várias funcionalidades publicamente acessíveis, estando mais funcionalidades preparadas para 

### criarEntidade(obj: Any): Entidade

Cria um objeto <strong>Entidade</strong> a partir do objeto da data class fornecido.

#### Parâmetros

* <strong>obj</strong>: O objeto da data class a ser convertido numa <strong>Entidade</strong>.

#### Retorna

* Uma <strong>Entidade</strong> representando a data class.

### prettyPrint(entidade: Entidade): String

Gera uma string XML formatada a partir da Entidade fornecida.

#### Parâmetros
* <strong>entidade</strong>: A <strong>Entidade</strong> a ser convertida numa string XML.
#### Retorna
* Uma string XML formatada representando a <strong>Entidade</strong>.

### microXPath(entidade: Entidade, xPath: String): List<Entidade>

Executa uma consulta XPath simplificada na Entidade fornecida.

#### Parâmetros
* <strong>entidade</strong>: A <strong>Entidade</strong> raiz para pesquisar.
* <strong>xPath</strong>: A string de consulta XPath.
#### Retorna

* Uma lista de objetos <strong>Entidade</strong> que correspondem à consulta XPath.

### gerarXML(obj: Any): String

Gera uma string XML a partir do objeto da data class fornecido.

#### Parâmetros

* <strong>obj</strong>: O objeto da data class a ser convertido em XML.

#### Retorna

* Uma string XML formatada representando o objeto da data class.

## Funções Adicionais

Além dos métodos referidos anteriormente disponibilizados pela API existem também as seguintes funções não disponibilizadas nesta.

### adicionarAtributoGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorAtributo: String):
Adiciona um atributo globalmente a todas as entidades correspondentes.

* <strong>renomearEntidadesGlobalmente(entidade: Entidade, nomeAntigo: String, nomeNovo: String)</strong>:
Renomeia todas as entidades com um nome dado para um novo valor.
* <strong>removerEntidadesGlobalmente(entidade: Entidade, nome: String)</strong>:
Remove todas as entidades com um nome dado.
* <strong>renomearAtributosGlobalmente(entidade: Entidade, nomeAntigo: String, nomeNovo: String)</strong>:
Renomeia todos os atributos com um nome dado para um novo valor.
* <strong>removerAtributosGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String)</strong>:
Remove todos os atributos com um nome dado das entidades especificadas.
* <strong>alterarAtributosGlobalmente(entidade: Entidade, nomeEntidade: String, nomeAtributo: String, valorNovo: String)</strong>:
Altera o valor de todos os atributos com um nome dado nas entidades especificadas.
* <strong>adicionarSubEntidade(entidade: Entidade, nomeSubEntidade: String, nomeEntidadeMae: String)</strong>:
Adiciona uma subentidade a todas as entidades mae correspondentes.

## Uso

Para usar esta biblioteca, é preciso anotar as data classes com as anotações fornecidas e depois usar os métodos da API para gerar e manipular representações XML das suas data classes.