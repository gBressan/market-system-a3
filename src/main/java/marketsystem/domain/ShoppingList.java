package marketsystem.domain;

import java.util.Optional;

import marketsystem.datastructure.DoublyLinkedList;
import marketsystem.datastructure.DoublyLinkedList.Node;
import marketsystem.datastructure.HashTable;
import marketsystem.datastructure.HashTableSnapshot;

/**
 * A lista de compras: única porta de entrada para cadastrar, localizar, comprar,
 * alterar e remover produtos.
 *
 * <p>Combina duas estruturas:
 * <ul>
 *   <li>{@link DoublyLinkedList} de produtos — guarda os itens na ordem em que foram
 *       cadastrados e permite percorrê-los para listagens e totais;</li>
 *   <li>{@link HashTable} nome → nó da lista — localiza um produto pelo nome em O(1)
 *       em média e entrega o nó, o que permite removê-lo da lista também em O(1).</li>
 * </ul>
 */
public final class ShoppingList {

    private final DoublyLinkedList<Product> products = new DoublyLinkedList<>();
    private final HashTable<String, Node<Product>> nodesByKey = new HashTable<>();

    /** Cadastra um produto. O(1) em média. */
    public void add(Product product) {
        requireNameAvailable(product.name());
        Node<Product> node = products.addLast(product);
        nodesByKey.put(product.key(), node);
    }

    /** Garante que ainda não existe produto com esse nome (ignorando acentos e maiúsculas). */
    public void requireNameAvailable(String productName) {
        if (nodesByKey.containsKey(ProductKey.from(productName))) {
            throw new DuplicateProductException(productName);
        }
    }

    /** Localiza um produto pelo nome. O(1) em média. */
    public Optional<Product> find(String productName) {
        return nodesByKey.get(ProductKey.from(productName)).map(Node::value);
    }

    /** Remove um produto pelo nome. O(1) em média. */
    public Product remove(String productName) {
        String key = ProductKey.from(productName);
        Node<Product> node = nodesByKey.get(key)
                .orElseThrow(() -> new ProductNotFoundException(productName));
        products.remove(node);
        nodesByKey.remove(key);
        return node.value();
    }

    /** Registra a compra de parte ou de toda a quantidade pendente de um produto. */
    public Product registerPurchase(String productName, int quantity, Money unitPricePaid) {
        Product product = findExisting(productName);
        product.registerPurchase(quantity, unitPricePaid);
        return product;
    }

    public Product changePlannedQuantity(String productName, int newPlannedQuantity) {
        Product product = findExisting(productName);
        product.changePlannedQuantity(newPlannedQuantity);
        return product;
    }

    public Product changeEstimatedUnitPrice(String productName, Money newEstimatedUnitPrice) {
        Product product = findExisting(productName);
        product.changeEstimatedUnitPrice(newEstimatedUnitPrice);
        return product;
    }

    public Product changeCategory(String productName, String newCategory) {
        Product product = findExisting(productName);
        product.changeCategory(newCategory);
        return product;
    }

    /** Todos os produtos, na ordem de cadastro. */
    public Iterable<Product> all() {
        return products;
    }

    /** Produtos que ainda têm alguma quantidade a comprar (pendentes e parciais). O(n). */
    public DoublyLinkedList<Product> pending() {
        return products.filter(Product::hasPendingQuantity);
    }

    /** Produtos com alguma quantidade já comprada (parciais e comprados). O(n). */
    public DoublyLinkedList<Product> purchased() {
        return products.filter(Product::hasPurchasedQuantity);
    }

    /** Totais calculados na hora a partir dos produtos, para nunca ficarem desatualizados. O(n). */
    public ShoppingSummary summary() {
        return ShoppingSummary.of(products);
    }

    /** Distribuição atual dos nomes na tabela hash, para visualização. */
    public HashTableSnapshot<String> indexSnapshot() {
        return nodesByKey.snapshot();
    }

    public int size() {
        return products.size();
    }

    public boolean isEmpty() {
        return products.isEmpty();
    }

    private Product findExisting(String productName) {
        return find(productName).orElseThrow(() -> new ProductNotFoundException(productName));
    }
}
