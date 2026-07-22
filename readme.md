# Transformação e Validação de Dados da ANS

Projeto em Java responsável por validar, enriquecer, agrupar e gerar um arquivo CSV com informações de despesas de operadoras de planos de saúde.

## Sobre o projeto

Este projeto corresponde à etapa de **Transformação e Validação de Dados** de um teste técnico dividido em repositórios separados.

A aplicação utiliza como entrada:

* o arquivo consolidado de despesas gerado na etapa anterior;
* o arquivo cadastral de operadoras da ANS.

O programa valida os registros de despesas, relaciona os dados pelo CNPJ, adiciona informações cadastrais das operadoras e gera o arquivo `despesas_agregadas.csv`.

As outras etapas do teste não estão implementadas neste repositório.

## Funcionalidades implementadas

* Leitura do arquivo `consolidado.csv`;
* Leitura do arquivo `Relatorio_cadop.csv`;
* Validação do formato e dos dígitos verificadores do CNPJ;
* Validação de Razão Social não vazia;
* Validação de valores de despesas positivos;
* Conversão de valores com vírgula decimal;
* Descarte de registros inválidos;
* Relacionamento dos dois arquivos utilizando o CNPJ;
* Inclusão de Registro ANS, Modalidade e UF nos dados;
* Tratamento de registros sem correspondência no cadastro;
* Agrupamento por Razão Social e UF;
* Soma dos valores por trimestre;
* Cálculo do total de despesas;
* Cálculo da média de despesas por trimestre;
* Cálculo do desvio padrão das despesas trimestrais;
* Ordenação pelo total de despesas, do maior para o menor;
* Geração do arquivo `despesas_agregadas.csv`;
* Criação automática da pasta de saída quando ela não existe.


## Estrutura do projeto

```text
.
├── Main.java
├── RegistroDespesa.java
├── OperadoraCadastro.java
├── RegistroDespesaEnriquecido.java
├── DespesaAgregada.java
├── ValidadorService.java
├── ValidadorUtils.java
├── recursos/
│   ├── consolidado.csv
│   └── Relatorio_cadop.csv
└── downloads/
    └── despesas_agregadas.csv
```

### `Main.java`

Ponto inicial da aplicação. Define os caminhos dos arquivos, chama os métodos de leitura, enriquecimento e geração do resultado.

### `RegistroDespesa.java`

Representa uma linha válida do arquivo consolidado, contendo:

* CNPJ;
* Razão Social;
* trimestre;
* ano;
* valor das despesas.

### `OperadoraCadastro.java`

Representa os dados cadastrais utilizados durante o relacionamento pelo CNPJ:

* CNPJ;
* Registro ANS;
* modalidade;
* UF.

### `RegistroDespesaEnriquecido.java`

Combina um `RegistroDespesa` com as informações obtidas no cadastro da operadora.

### `DespesaAgregada.java`

Representa uma linha do CSV final, contendo os dados cadastrais e os valores calculados.

### `ValidadorUtils.java`

Contém as validações de CNPJ, Razão Social e valor positivo.

### `ValidadorService.java`

Concentra as operações de leitura dos arquivos, validação dos registros, relacionamento dos dados, agrupamento, cálculos e escrita do CSV final.

## Como executar

### Pré-requisitos

* JDK instalado;
* uma IDE com suporte a Java, como IntelliJ IDEA;
* os arquivos de entrada dentro da pasta `recursos`.
* Java 21

### Arquivos necessários

A aplicação espera encontrar:

```text
recursos/consolidado.csv
recursos/Relatorio_cadop.csv
```

O `consolidado.csv` deve possuir as colunas:

```text
CNPJ;RazaoSocial;Trimestre;Ano;ValorDespesas
```

O arquivo `Relatorio_cadop.csv` deve seguir a estrutura do relatório cadastral utilizado no projeto.

### Execução pela IDE

1. Abra o projeto em uma IDE Java.
2. Confirme que os arquivos de entrada estão na pasta `recursos`.
3. Execute o método `main` da classe `Main`.
4. Acompanhe no console os registros rejeitados durante a validação.
5. Consulte o resultado em:

```text
downloads/despesas_agregadas.csv
```

A pasta `downloads` é criada automaticamente caso ainda não exista.

## Fluxo de funcionamento

1. O programa abre o arquivo `consolidado.csv`.
2. Cada linha é lida e separada em colunas.
3. O CNPJ, a Razão Social e o valor da despesa são validados.
4. Registros inválidos são informados no console e ignorados.
5. O arquivo cadastral das operadoras é carregado em um `HashMap`, utilizando o CNPJ como chave.
6. Cada despesa válida é relacionada com seu cadastro pelo CNPJ.
7. Os registros são agrupados por Razão Social e UF.
8. Dentro de cada grupo, os valores são somados por ano e trimestre.
9. O programa calcula total, média trimestral e desvio padrão.
10. Os resultados são ordenados pelo total de despesas em ordem decrescente.
11. O arquivo `despesas_agregadas.csv` é gerado na pasta `downloads`.

O CSV final possui as colunas:

```text
RazaoSocial;RegistroANS;Modalidade;UF;TotalDespesas;MediaDespesasPorTrimestre;DesvioPadrao
```

## Decisões técnicas

### Leitura incremental dos CSVs

**Escolha:** os arquivos são lidos linha por linha com `BufferedReader`.

**Motivo:** essa abordagem evita carregar todo o conteúdo original dos arquivos de uma vez antes da validação.

**Ponto positivo:** utiliza pouca memória durante a leitura e permite validar cada linha separadamente.

**Limitação:** a implementação utiliza `split(";")` e não é um leitor CSV completo.

### Tratamento de CNPJs inválidos

**Escolha:** registros com CNPJ inválido são informados no console e ignorados.

**Motivo:** não seria seguro utilizar um CNPJ inválido como chave para relacionar os dados.

**Ponto positivo:** evita associações incorretas com o cadastro das operadoras.

**Limitação:** os registros descartados não são gravados em um relatório separado.

### Relacionamento pelo CNPJ

**Escolha:** o cadastro das operadoras é armazenado em um `HashMap`, utilizando o CNPJ como chave.

**Motivo:** o mapa permite localizar uma operadora diretamente pelo CNPJ, sem percorrer todo o cadastro para cada registro.

**Ponto positivo:** simplifica e agiliza o relacionamento dos dados.

**Limitação:** o processo depende de os CNPJs dos dois arquivos estarem no mesmo formato após a limpeza.

### Cadastros duplicados

**Escolha:** o carregamento utiliza `putIfAbsent`, mantendo a primeira ocorrência encontrada para cada CNPJ.

**Motivo:** foi escolhida uma regra simples para impedir que um cadastro já armazenado seja substituído.

**Ponto positivo:** garante apenas um cadastro por CNPJ no mapa.

**Limitação:** possíveis diferenças nas ocorrências seguintes não são analisadas ou registradas.

### Registros sem correspondência

**Escolha:** despesas sem cadastro correspondente continuam no processamento com os valores `N/A` e `IGNORADO`.

**Motivo:** preservar a despesa evita excluir um valor apenas porque o cadastro não foi encontrado.

**Ponto positivo:** mantém o registro disponível para a agregação.

**Limitação:** os campos cadastrais desses registros não possuem informações reais.

### Agrupamento em memória

**Escolha:** foi utilizado um `HashMap` em dois níveis.

O primeiro nível agrupa por:

```text
RazaoSocial + UF
```

O segundo nível agrupa por:

```text
Ano + Trimestre
```

**Motivo:** é necessário somar primeiro os registros do mesmo trimestre antes de calcular média e desvio padrão.

**Ponto positivo:** permite realizar os cálculos de forma simples usando as coleções do Java.

**Limitação:** o agrupamento permanece em memória durante o processamento.

### Registro ANS e modalidade no resultado agregado

**Escolha:** para cada combinação de Razão Social e UF, são utilizados o primeiro Registro ANS e a primeira Modalidade encontrados nos dados enriquecidos.

**Motivo:** o agrupamento solicitado é feito por Razão Social e UF, enquanto o CSV final também apresenta informações cadastrais.

**Ponto positivo:** mantém o agrupamento simples e inclui os campos cadastrais na saída.

**Limitação:** caso uma mesma Razão Social e UF possua mais de um Registro ANS ou modalidade, apenas a primeira correspondência será utilizada.

### Média por trimestre

**Escolha:** a média é calculada utilizando somente os trimestres com valores encontrados para cada grupo.

**Motivo:** a ausência de um registro não foi interpretada automaticamente como uma despesa igual a zero.

**Ponto positivo:** evita criar valores que não estavam presentes nos arquivos de entrada.

**Limitação:** grupos com quantidades diferentes de trimestres podem ter médias calculadas sobre períodos diferentes.

### Desvio padrão

**Escolha:** foi utilizado o desvio padrão populacional dos totais trimestrais.

**Motivo:** os trimestres disponíveis foram considerados como o conjunto completo analisado pelo projeto.

**Ponto positivo:** permite observar quanto os valores trimestrais variam em relação à média.

**Limitação:** grupos com apenas um trimestre terão desvio padrão igual a zero.

### Ordenação

**Escolha:** os resultados são armazenados em uma lista e ordenados em memória com `List.sort` e `Comparator`, utilizando o total de despesas como critério.

**Motivo:** a ordenação acontece depois da agregação, quando a quantidade de registros já foi reduzida.

**Ponto positivo:** é uma solução simples, legível e suficiente para o volume atual.

**Limitação:** todos os resultados agregados precisam permanecer em memória até a conclusão da ordenação.

## Tratamento de inconsistências

### CNPJ inválido

O programa remove caracteres que não sejam números e valida:

* quantidade de dígitos;
* sequências formadas pelo mesmo número;
* dois dígitos verificadores.

Quando o CNPJ é inválido, o registro é informado no console e não é adicionado à lista de dados válidos.

Exemplo de mensagem:

```text
CNPJ invalido: valor encontrado
```

Essa abordagem foi escolhida porque o CNPJ é utilizado como chave para relacionar as despesas com o cadastro das operadoras.

### Razão Social vazia

O programa verifica se a Razão Social:

* não é `null`;
* não fica vazia depois da remoção dos espaços externos.

Quando a informação é inválida, o registro é informado no console e ignorado.

### Valores zerados, negativos ou inválidos

O valor textual é preparado antes da conversão:

* as aspas são removidas;
* a vírgula decimal é substituída por ponto;
* campos vazios ou com `-` são convertidos para zero;
* valores que não podem ser convertidos também resultam em zero.

Depois da conversão, o programa aceita somente valores maiores que zero.

Valores iguais a zero, negativos ou inválidos são informados no console e descartados.

Essa estratégia simplifica o processamento, mas não diferencia no resultado final um valor realmente zerado de um valor que não pôde ser convertido.

### Linhas incompletas

Linhas do arquivo consolidado com menos de cinco colunas são ignoradas.

Linhas do cadastro com menos de onze colunas também são ignoradas.

Esses casos não geram um relatório separado.

### CNPJ duplicado no cadastro

O cadastro das operadoras utiliza:

```java
putIfAbsent
```

Quando o mesmo CNPJ aparece mais de uma vez, apenas a primeira ocorrência encontrada é mantida.

As possíveis diferenças entre os registros duplicados não são comparadas.

### Registro sem correspondência no cadastro

Quando um CNPJ válido do consolidado não é encontrado no cadastro, o registro continua no processamento com:

```text
RegistroANS = N/A
Modalidade = IGNORADO
UF = IGNORADO
```

A despesa não é descartada, mas fica identificada como um registro sem informações cadastrais correspondentes.

### Formatos de trimestre

O ano e o trimestre são utilizados conforme foram lidos do arquivo consolidado.

Não foi identificado no código um processo específico de normalização para diferentes formatos de trimestre.

## Limitações atuais

* A leitura utiliza `split(";")`, que pode separar incorretamente campos que contenham ponto e vírgula dentro de aspas;
* Os valores financeiros são armazenados como `double`, que pode apresentar pequenas diferenças de precisão;
* Os caminhos dos arquivos de entrada e saída estão definidos diretamente na classe `Main`;
* O programa depende de os arquivos `consolidado.csv` e `Relatorio_cadop.csv` já estarem disponíveis na pasta `recursos`;
* O download automático do cadastro das operadoras não foi identificado neste repositório;
* Não foram identificados testes automatizados;
* Os registros descartados são exibidos no console, mas não são salvos em um relatório;
* Linhas com quantidade insuficiente de colunas são ignoradas sem uma mensagem específica;
* Valores vazios, mal formatados ou representados por `-` são tratados como zero antes da validação;
* Não existe normalização específica dos diferentes formatos de trimestre;
* O relacionamento depende da igualdade do CNPJ após a remoção dos caracteres não numéricos;
* Quando existem cadastros duplicados para o mesmo CNPJ, apenas o primeiro é utilizado;
* Para preencher Registro ANS e Modalidade no resultado agregado, é utilizada a primeira correspondência encontrada para a mesma Razão Social e UF;
* O agrupamento e a ordenação são realizados em memória;
* O arquivo final não é compactado automaticamente pelo código atual;
* A criação do arquivo ZIP final solicitado pelo teste não foi identificada no estado atual do projeto.

## Possíveis melhorias

Uma possível melhoria seria utilizar uma biblioteca própria para leitura e escrita de CSV, evitando problemas com campos que contenham o caractere separador.

O projeto poderia futuramente utilizar `BigDecimal` para os valores financeiros, reduzindo possíveis diferenças de precisão.

Uma possível melhoria seria criar um relatório separado com os registros descartados e os motivos de cada rejeição.

O programa poderia receber os caminhos dos arquivos por argumentos da linha de comando, evitando que eles fiquem definidos diretamente na classe `Main`.

Também seria possível adicionar uma normalização para os formatos de ano e trimestre antes do agrupamento.

Para volumes maiores, o enriquecimento e a recuperação das informações cadastrais poderiam utilizar estruturas adicionais de consulta ou um banco de dados.

O projeto poderia incluir testes automatizados para as validações de CNPJ, conversão de valores, enriquecimento e cálculos agregados.

## Autor

Ronaldo Dutra Filho

