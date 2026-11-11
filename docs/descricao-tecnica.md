# Descrição técnica das estruturas de dados

**Projeto:** Sistema de Lista de Compras de Supermercado
**Disciplina:** Estruturas de Dados e Análise de Algoritmos — Avaliação A3

## 1. Visão geral

O sistema gerencia uma única lista de compras em memória. Para atender aos requisitos
(cadastrar, localizar, comprar total ou parcialmente, listar adquiridos e pendentes e
calcular valores), combinamos **duas estruturas** implementadas pela equipe, sem usar as
coleções prontas do Java:

| Estrutura | Classe | Papel no sistema |
|---|---|---|
| Lista duplamente encadeada | `DoublyLinkedList<T>` | Guarda os produtos na ordem de cadastro; base para listagens, filtros e totais |
| Tabela hash com encadeamento separado | `HashTable<K, V>` | Índice *nome → nó da lista*; localiza qualquer produto em O(1) em média |

A classe `ShoppingList` é a única que conhece as duas e as mantém sincronizadas.

```
HashTable (índice: chave normalizada -> NÓ da lista)
  [ 0]  "leite integral 1l"                       -> nó de Leite
  [ 1]  "detergente" -> "oleo de soja 900ml"       -> nó de Detergente, nó de Óleo   (colisão)
  [13]  "arroz 5kg"                               -> nó de Arroz
  ...

DoublyLinkedList (produtos na ordem de cadastro)
  head -> [Arroz] <-> [Feijão] <-> [Leite] <-> ... <-> [Detergente] <-> ... <-> [Macarrão] <- tail
```

## 2. Lista duplamente encadeada (`DoublyLinkedList`)

**Como funciona:** cada nó guarda o valor, uma referência para o nó anterior e outra
para o próximo. A lista guarda o primeiro (`head`), o último (`tail`) e o tamanho.

**Por que escolhemos:**

- A lista de compras **cresce e diminui o tempo todo**. Uma estrutura encadeada não tem
  tamanho fixo nem precisa realocar/deslocar elementos (o que seria necessário num vetor,
  que também é proibido pelo enunciado).
- Ser **duplamente** encadeada permite **remover um nó em O(1)** quando já temos a
  referência para ele: basta ligar o anterior ao próximo. Numa lista simples seria
  preciso percorrer a lista para achar o anterior (O(n)).
- Mantém a **ordem de cadastro**, que é a ordem natural de uma lista de compras.
- Percorrer todos os itens (listagens, filtros de pendentes/adquiridos, resumo de
  valores) é naturalmente O(n), que é o mínimo possível para operações que precisam
  olhar todos os produtos.

**Proteção da estrutura:** de fora da classe só se consegue ler o valor do nó. Os
ponteiros `previous`/`next` são privados, e `remove` recusa um nó que não pertence à lista
(ou que já foi removido), evitando corromper os encadeamentos.

## 3. Tabela hash com encadeamento separado (`HashTable`)

**Como funciona:**

1. A chave (nome normalizado do produto) gera um número com `hashCode()`.
2. O índice do bucket é `hash mod capacidade` (`Math.floorMod`, que nunca dá negativo).
3. Cada bucket é uma **`DoublyLinkedList`** de pares chave/valor. Se duas chaves caírem
   no mesmo bucket (**colisão**), elas ficam na mesma lista — isso é o *encadeamento
   separado*.
4. **Fator de carga** = itens / buckets. Quando passa de **0,75**, a capacidade **dobra**
   e todas as entradas são redistribuídas (**rehash**). Assim as listas dos buckets
   continuam curtas e a busca continua O(1) em média.

**Por que escolhemos:**

- O requisito "consultar e localizar itens" é a operação mais frequente: em toda compra,
  edição ou remoção o usuário digita o nome do produto. Com a tabela hash a localização é
  **O(1) em média**, contra O(n) se fosse preciso percorrer a lista.
- A hash também garante rapidamente que **não existam nomes duplicados** no cadastro.
- O valor guardado na hash é o **nó** da lista, não o produto. Assim, ao remover, a
  hash entrega o nó em O(1) e a lista o remove em O(1): a remoção inteira fica O(1) em
  média.
- Reaproveitamos a nossa própria `DoublyLinkedList` como bucket, então a equipe domina
  todas as partes da tabela.

**Normalização da chave:** antes de ir para a hash, o nome passa por `ProductKey`, que
remove acentos, ignora maiúsculas e espaços repetidos. Por isso "Feijão Preto",
"feijao preto" e "FEIJÃO  PRETO" encontram o mesmo produto.

**Observação sobre a regra "exceto vetor e matriz":** a tabela hash, por definição, tem
uma tabela de buckets acessada pelo índice calculado pelo hash. Essa tabela é parte
interna da estrutura hash — nenhum dado da lista de compras é guardado ou percorrido como
vetor. Os produtos ficam na lista encadeada, e cada bucket também é uma lista encadeada.

## 4. Complexidade das operações

| Operação do sistema | Como é feita | Complexidade |
|---|---|---|
| Cadastrar produto | verifica duplicado na hash + `addLast` na lista + `put` na hash | O(1) médio (amortizado) |
| Consultar / localizar | `get` na hash pelo nome normalizado | O(1) médio |
| Registrar compra (total/parcial) | localiza na hash e atualiza o produto | O(1) médio |
| Editar produto | localiza na hash e altera os campos | O(1) médio |
| Remover produto | hash devolve o nó → `remove` na lista + `remove` na hash | O(1) médio |
| Listar todos | percorre a lista | O(n) |
| Listar pendentes / adquiridos | `filter` percorre a lista | O(n) |
| Resumo de valores | percorre a lista uma vez acumulando os totais | O(n) |
| Rehash (eventual) | redistribui todas as entradas | O(n), raro — amortizado O(1) por inserção |

**Pior caso da hash:** se todas as chaves caíssem no mesmo bucket, as operações virariam
O(n). O rehash com fator de carga 0,75 torna isso muito improvável na prática.

## 5. Consistência dos dados e dos cálculos

- **Valores em `Money` (BigDecimal com 2 casas)**: evita erros de arredondamento de
  `double` (ex.: `0.1 + 0.2 = 0.30000000000000004`).
- **Nada derivado é guardado**: quantidade pendente, status e valores estimados são
  calculados a partir de *quantidade planejada*, *quantidade comprada*, *preço estimado*
  e *valor gasto*. Não existe um "total" guardado que possa ficar desatualizado.
- **Resumo calculado na hora** (`ShoppingSummary.of`): percorre a lista uma vez (O(n)).
  Escolhemos isso em vez de manter totais acumulados porque é impossível o resumo divergir
  dos produtos — mesmo depois de edições e remoções. Para o tamanho de uma lista de
  compras, O(n) é instantâneo.
- **Uma única porta de entrada**: os métodos que alteram um `Product` só são visíveis
  dentro do pacote `domain`. A interface só consegue alterar dados pela `ShoppingList`,
  que mantém lista e hash sincronizadas.
- **Regras validadas no domínio**: quantidade planejada > 0; preço não negativo; não é
  possível comprar mais do que o pendente; a quantidade planejada não pode ficar menor que
  a já comprada; nomes duplicados são recusados.

## 6. Compra total e parcial

Cada produto guarda quanto foi planejado e quanto já foi comprado. Uma compra informa a
**quantidade** e o **preço realmente pago por unidade** (que pode ser diferente do
estimado):

- **Parcial:** comprou 6 de 12 litros de leite → status *Parcial*, faltam 6.
- **Total:** pressionar Enter na quantidade compra todo o pendente → status *Comprado*.
- Várias compras parciais se acumulam: o valor gasto é a soma de cada compra.

Com isso o sistema mostra, para os itens adquiridos, **quanto foi gasto × quanto estava
estimado** (economia ou gasto acima), e para os pendentes, **quanto ainda deve ser gasto**,
além da **previsão de gasto final** (gasto + estimado do que falta).
