# Sistema de Lista de Compras — A3 de Estruturas de Dados

Sistema em Java (modo texto) para gerenciar uma lista de compras de supermercado:
cadastrar produtos, localizar itens, registrar compras totais ou parciais e acompanhar
o que foi comprado, o que falta e os valores estimados e gastos.

As estruturas de dados (**tabela hash com encadeamento separado + lista duplamente
encadeada**) foram implementadas do zero, sem coleções prontas do Java. A justificativa
completa está em **[docs/descricao-tecnica.md](docs/descricao-tecnica.md)**.

## Como executar

Requisito: **Java 21** (`java -version`). Não precisa de Maven, Gradle nem bibliotecas.

| | Windows | Linux / macOS / Git Bash |
|---|---|---|
| Executar o sistema | `.\run.bat` | `sh run.sh` |
| Executar os testes | `.\test.bat` | `sh test.sh` |

Na IDE (IntelliJ, Eclipse, VS Code): abra a pasta, marque `src/main/java` como pasta de
código e `src/test/java` como pasta de testes, e execute `marketsystem.Main`. Os testes
rodam pela classe `marketsystem.testing.TestRunner`.

> Dica para a apresentação: a opção **10** carrega uma lista de exemplo com 10 produtos
> (alguns já comprados) e a opção **11** mostra a tabela hash por dentro, com colisões.

## Funcionalidades

| Opção | O que faz |
|---|---|
| 1 | Cadastrar produto (nome, categoria, quantidade planejada, preço estimado) |
| 2 | Consultar produto pelo nome (ignora acentos e maiúsculas) |
| 3 | Registrar compra **total** (Enter) ou **parcial** (informa a quantidade), com o preço pago |
| 4 | Listar todos os produtos |
| 5 | Listar itens pendentes + valor estimado que falta |
| 6 | Listar itens adquiridos + total gasto × estimado |
| 7 | Resumo: estimado, gasto, diferença, falta gastar e previsão final |
| 8 | Editar categoria, quantidade planejada ou preço estimado |
| 9 | Remover produto (com confirmação) |
| 10 | Carregar lista de exemplo (só com a lista vazia) |
| 11 | Ver a tabela hash: buckets, fator de carga e colisões |

## Organização do código

```
src/main/java/marketsystem/
├── Main.java                     ponto de entrada: monta as dependências
├── datastructure/                estruturas de dados genéricas (não sabem o que é "produto")
│   ├── DoublyLinkedList.java     lista duplamente encadeada
│   ├── HashTable.java            tabela hash com encadeamento separado + rehash
│   └── HashTableSnapshot.java    retrato da hash para visualização
├── domain/                       regras de negócio
│   ├── ShoppingList.java         a lista de compras: usa a hash + a lista encadeada
│   ├── Product.java              item: planejado × comprado, valores derivados
│   ├── ShoppingSummary.java      totais calculados percorrendo a lista
│   ├── Money.java                dinheiro com BigDecimal (2 casas)
│   ├── PurchaseStatus.java       Pendente / Parcial / Comprado
│   ├── ProductKey.java           normaliza o nome (sem acento, minúsculo)
│   └── *Exception.java           erros de negócio com mensagem para o usuário
└── ui/                           interface de texto (sem regra de negócio)
    ├── ShoppingListApp.java      fluxo do menu
    ├── ConsoleInput.java         leitura com validação e repetição
    ├── ConsoleView.java          tudo que é impresso na tela
    ├── MenuOption.java           opções do menu
    ├── MoneyText.java            "R$ 1.234,56" <-> Money
    └── SampleData.java           lista de exemplo para a demonstração

src/test/java/marketsystem/       34 testes automatizados (executor próprio, sem JUnit)
```

As dependências seguem uma direção só: `ui` → `domain` → `datastructure`.

## Decisões de Clean Code

- **Nomes que explicam a intenção:** `pendingQuantity()`, `estimatedValueOfPending()`,
  `requireNameAvailable()`, `bypassFromPrevious()`. Os nomes dizem o que é feito, e por
  isso quase não há comentários explicando *o que* o código faz. Os Javadocs registram o
  *porquê* e a complexidade.
- **Funções pequenas e com uma responsabilidade:** cada opção do menu é um método curto;
  as validações ficam em métodos `require...` separados.
- **Responsabilidade única por classe (SRP):** estrutura de dados, regra de negócio,
  leitura do teclado e impressão na tela ficam em classes diferentes. Dá para trocar a
  interface de texto por outra sem mexer no domínio.
- **Encapsulamento:** os nós da lista não expõem os ponteiros; os métodos que alteram um
  `Product` só são acessíveis pela `ShoppingList`.
- **Sem `null` como retorno:** buscas devolvem `Optional`.
- **Erros com exceções, não com códigos:** as exceções de negócio carregam a mensagem
  exibida ao usuário, e a interface trata todas num único ponto (`executeSafely`).
- **Sem números mágicos:** `DEFAULT_CAPACITY`, `MAX_LOAD_FACTOR`, `GROWTH_FACTOR` e afins
  são constantes nomeadas.
- **Objetos imutáveis onde possível:** `Money`, `ShoppingSummary` e `HashTableSnapshot`
  são `record`s.
- **Testes:** cada estrutura e cada regra tem testes. O `ShoppingListAppTest` simula um
  usuário digitando no terminal.

## Roteiro sugerido para a demonstração (~5 min)

1. **10** — carregar a lista de exemplo → **4** — listar tudo (mostrar os status).
2. **2** — consultar `feijao preto 1kg` digitado **sem acento** → explicar a normalização
   e a busca O(1) na hash.
3. **3** — comprar `Leite integral 1L`: 4 unidades a R$ 5,10 (parcial) → depois comprar o
   resto com Enter (total).
4. **5** e **6** — pendentes × adquiridos, com os totais.
5. **7** — resumo: gasto × estimado, economia/gasto acima, previsão final.
6. **11** — tabela hash: mostrar a colisão `detergente -> oleo de soja 900ml`. Cadastrar
   `Manteiga 200g` (colide com `arroz 5kg`) e mais 2 produtos até passar de 12: na
   **11** a capacidade sobe de 16 para 32 (**rehash**).
7. **9** — remover um produto e mostrar que a lista e a hash continuam consistentes (**4** e **11**).
8. Mostrar um erro tratado: comprar mais do que o pendente ou cadastrar nome repetido.

## Sugestão de divisão para a apresentação

| Integrante | Parte que apresenta |
|---|---|
| 1 | `DoublyLinkedList`: nós, inserção, remoção O(1), iterador |
| 2 | `HashTable`: função hash, colisões, fator de carga, rehash |
| 3 | `ShoppingList` + `Product`: como as duas estruturas trabalham juntas, compra parcial, consistência |
| 4 | Interface de texto, Clean Code, testes e a demonstração |

Como o professor pode perguntar sobre qualquer parte, **todos devem conseguir explicar a
remoção O(1) (hash → nó → lista) e o que acontece numa colisão**.
