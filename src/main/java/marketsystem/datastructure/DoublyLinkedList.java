package marketsystem.datastructure;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Lista duplamente encadeada genérica.
 *
 * <p>Cada nó conhece o nó anterior e o próximo, e a lista guarda referências para
 * o primeiro ({@code head}) e o último ({@code tail}). Assim é possível inserir no
 * fim e remover um nó já conhecido em O(1), sem percorrer a lista.
 *
 * @param <T> tipo dos valores armazenados
 */
public final class DoublyLinkedList<T> implements Iterable<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    /**
     * Insere o valor no fim da lista. Complexidade O(1).
     *
     * @return o nó criado, que pode ser guardado para uma remoção direta depois
     */
    public Node<T> addLast(T value) {
        Node<T> node = new Node<>(this, value);
        if (isEmpty()) {
            head = node;
        } else {
            linkAfterTail(node);
        }
        tail = node;
        size++;
        return node;
    }

    /** Remove um nó que pertence a esta lista. Complexidade O(1). */
    public void remove(Node<T> node) {
        requireOwnedNode(node);
        bypassFromPrevious(node);
        bypassFromNext(node);
        node.detach();
        size--;
    }

    /** Devolve o primeiro nó cujo valor atende ao critério. Complexidade O(n). */
    public Optional<Node<T>> findFirstNode(Predicate<T> criteria) {
        for (Node<T> current = head; current != null; current = current.next) {
            if (criteria.test(current.value)) {
                return Optional.of(current);
            }
        }
        return Optional.empty();
    }

    /** Cria uma nova lista apenas com os valores que atendem ao critério. Complexidade O(n). */
    public DoublyLinkedList<T> filter(Predicate<T> criteria) {
        DoublyLinkedList<T> result = new DoublyLinkedList<>();
        for (T value : this) {
            if (criteria.test(value)) {
                result.addLast(value);
            }
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Percorre a lista do primeiro ao último elemento. */
    @Override
    public Iterator<T> iterator() {
        return new ForwardIterator<>(head);
    }

    private void linkAfterTail(Node<T> node) {
        tail.next = node;
        node.previous = tail;
    }

    private void bypassFromPrevious(Node<T> node) {
        if (node.previous == null) {
            head = node.next;
        } else {
            node.previous.next = node.next;
        }
    }

    private void bypassFromNext(Node<T> node) {
        if (node.next == null) {
            tail = node.previous;
        } else {
            node.next.previous = node.previous;
        }
    }

    private void requireOwnedNode(Node<T> node) {
        Objects.requireNonNull(node, "O nó não pode ser nulo.");
        if (node.owner != this) {
            throw new IllegalArgumentException("O nó informado não pertence a esta lista.");
        }
    }

    /**
     * Nó da lista. Fora desta classe só é possível ler o valor; os encadeamentos
     * ficam protegidos para que ninguém quebre a estrutura por engano.
     */
    public static final class Node<T> {

        private DoublyLinkedList<T> owner;
        private final T value;
        private Node<T> previous;
        private Node<T> next;

        private Node(DoublyLinkedList<T> owner, T value) {
            this.owner = owner;
            this.value = value;
        }

        public T value() {
            return value;
        }

        private void detach() {
            owner = null;
            previous = null;
            next = null;
        }
    }

    private static final class ForwardIterator<T> implements Iterator<T> {

        private Node<T> upcoming;

        private ForwardIterator(Node<T> first) {
            this.upcoming = first;
        }

        @Override
        public boolean hasNext() {
            return upcoming != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T value = upcoming.value;
            upcoming = upcoming.next;
            return value;
        }
    }
}
