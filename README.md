# XML API

Esta API permite a geração de ficheiros "XML"-like com base nas anotações dos ficheiros de dados inseridos nos métodos.

## Anotações
As seguintes anotações devem ser utilizadas nas classes enviadas nos métodos pretendidos da API de forma a fazer o correcto mapeamento dos dados numa estrutura XML

### @XmlEntity(name: String)
Identifica que é uma entidade XML

### @XmlAtribute(name: String)
Identifica que é um atributo de uma entidade XML

### @XmlAdapter(classe: KClass<*>)
Permite a alteração do estruturamento do XML, deve receber uma classe que tenha métodos que recebam Entidades e façam as alterações da maneira pretendida.

### @XmlString(classe: KClass<*>)
Permite a alteração do estruturamento de uma String, deve receber uma classe que tenha métodos que recebam uma string e a alterem e devolvam da maneira pretendida.

## Métodos

###  criarDocumento

### criarEntidade