package marketsystem.datastructure;

import java.util.Objects;
import java.util.Optional;

/**
 * Tabela hash com encadeamento separado (<i>separate chaining</i>).
 *
 * <p>Cada posição da tabela (bucket) guarda uma {@link DoublyLinkedList} com os pares
 * chave/valor cujo hash caiu naquela posição. Quando duas chaves caem no mesmo bucket
 * (colisão), elas simplesmente ficam na mesma lista.
 *
 * <p>Quando o fator de carga (elementos / buckets) passa de {@value #MAX_LOAD_FACTOR},
 * a capacidade dobra e todas as entradas são redistribuídas (rehash). Isso mantém as
 * listas curtas e as operações rápidas.
 *
 * <p>Complexidade: busca, inserção e remoção O(1) em média e O(n) no pior caso
 * (todas as chaves no mesmo bucket).
 *
 * @param <K> tipo da chave
 * @param <V> tipo do valor
 */
public final class HashTable<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;
    private static final int GROWTH_FACTOR = 2;

    private DoublyLinkedList<Entry<K, V>>[] buckets;
    private int size;

    public HashTable() {
        this(DEFAULT_CAPACITY);
    }

    public HashTable(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("A capacidade inicial deve ser maior que zero.");
        }
        buckets = createBuckets(initialCapacity);
    }

    /** Insere o par chave/valor ou substitui o valor se a chave já existir. O(1) em média. */
    public void put(K key, V value) {
        Optional<Entry<K, V>> existing = findEntry(key);
        if (existing.isPresent()) {
            existing.get().value = value;
            return;
        }
        bucketOf(key).addLast(new Entry<>(key, value));
        size++;
        growIfOverloaded();
    }

    /** Busca o valor associado à chave. O(1) em média. */
    public Optional<V> get(K key) {
        return findEntry(key).map(entry -> entry.value);
    }

    public boolean containsKey(K key) {
        return findEntry(key).isPresent();
    }

    /**
     * Remove a chave da tabela. O(1) em média.
     *
     * @return {@code true} se a chave existia e foi removida
     */
    public boolean remove(K key) {
        DoublyLinkedList<Entry<K, V>> bucket = bucketOf(key);
        Optional<DoublyLinkedList.Node<Entry<K, V>>> node = bucket.findFirstNode(entry -> entry.hasKey(key));
        if (node.isEmpty()) {
            return false;
        }
        bucket.remove(node.get());
        size--;
        return true;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int capacity() {
        return buckets.length;
    }

    /** Retrato da distribuição atual das chaves, usado para visualizar a estrutura. O(capacidade + n). */
    public HashTableSnapshot<K> snapshot() {
        DoublyLinkedList<HashTableSnapshot.Bucket<K>> occupiedBuckets = new DoublyLinkedList<>();
        for (int index = 0; index < buckets.length; index++) {
            if (!buckets[index].isEmpty()) {
                occupiedBuckets.addLast(new HashTableSnapshot.Bucket<>(index, keysOf(buckets[index])));
            }
        }
        return new HashTableSnapshot<>(buckets.length, size, occupiedBuckets);
    }

    private Optional<Entry<K, V>> findEntry(K key) {
        return bucketOf(key)
                .findFirstNode(entry -> entry.hasKey(key))
                .map(DoublyLinkedList.Node::value);
    }

    private DoublyLinkedList<Entry<K, V>> bucketOf(K key) {
        Objects.requireNonNull(key, "A chave não pode ser nula.");
        return buckets[indexFor(key, buckets.length)];
    }

    private static int indexFor(Object key, int capacity) {
        return Math.floorMod(key.hashCode(), capacity);
    }

    private void growIfOverloaded() {
        double loadFactor = (double) size / buckets.length;
        if (loadFactor > MAX_LOAD_FACTOR) {
            rehash(buckets.length * GROWTH_FACTOR);
        }
    }

    private void rehash(int newCapacity) {
        DoublyLinkedList<Entry<K, V>>[] oldBuckets = buckets;
        buckets = createBuckets(newCapacity);
        for (DoublyLinkedList<Entry<K, V>> bucket : oldBuckets) {
            for (Entry<K, V> entry : bucket) {
                bucketOf(entry.key).addLast(entry);
            }
        }
    }

    private static <K, V> DoublyLinkedList<K> keysOf(DoublyLinkedList<Entry<K, V>> bucket) {
        DoublyLinkedList<K> keys = new DoublyLinkedList<>();
        for (Entry<K, V> entry : bucket) {
            keys.addLast(entry.key);
        }
        return keys;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <K, V> DoublyLinkedList<Entry<K, V>>[] createBuckets(int capacity) {
        DoublyLinkedList<Entry<K, V>>[] newBuckets = new DoublyLinkedList[capacity];
        for (int index = 0; index < capacity; index++) {
            newBuckets[index] = new DoublyLinkedList<>();
        }
        return newBuckets;
    }

    private static final class Entry<K, V> {

        private final K key;
        private V value;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        private boolean hasKey(K otherKey) {
            return key.equals(otherKey);
        }
    }
}
