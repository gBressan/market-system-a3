package marketsystem.datastructure;

import static marketsystem.testing.Assertions.assertEquals;
import static marketsystem.testing.Assertions.assertThrows;
import static marketsystem.testing.Assertions.assertTrue;

import marketsystem.datastructure.DoublyLinkedList.Node;
import marketsystem.testing.Test;

public final class DoublyLinkedListTest {

    @Test
    void keepsInsertionOrder() {
        DoublyLinkedList<String> list = listOf("a", "b", "c");

        assertEquals("a,b,c", joined(list));
        assertEquals(3, list.size());
    }

    @Test
    void removesFirstMiddleAndLastNodes() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        Node<String> first = list.addLast("a");
        Node<String> middle = list.addLast("b");
        Node<String> last = list.addLast("c");
        list.addLast("d");

        list.remove(middle);
        assertEquals("a,c,d", joined(list));
        list.remove(first);
        assertEquals("c,d", joined(list));
        list.remove(last);
        assertEquals("d", joined(list));
        assertEquals(1, list.size());
    }

    @Test
    void becomesEmptyAndReusableAfterRemovingOnlyNode() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        Node<String> only = list.addLast("a");

        list.remove(only);
        assertTrue(list.isEmpty(), "lista vazia após remover o único nó");

        list.addLast("b");
        assertEquals("b", joined(list));
    }

    @Test
    void rejectsNodeFromAnotherListOrAlreadyRemoved() {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        DoublyLinkedList<String> otherList = new DoublyLinkedList<>();
        Node<String> foreignNode = otherList.addLast("x");
        Node<String> node = list.addLast("a");
        list.remove(node);

        assertThrows(IllegalArgumentException.class, () -> list.remove(foreignNode));
        assertThrows(IllegalArgumentException.class, () -> list.remove(node));
    }

    @Test
    void findsFirstNodeMatchingCriteria() {
        DoublyLinkedList<String> list = listOf("maçã", "banana", "batata");

        assertEquals("banana", list.findFirstNode(value -> value.startsWith("ba")).orElseThrow().value());
        assertTrue(list.findFirstNode(value -> value.startsWith("z")).isEmpty(), "nenhum valor começa com z");
    }

    @Test
    void filterReturnsNewListWithoutChangingOriginal() {
        DoublyLinkedList<String> list = listOf("a", "bb", "c", "dd");

        DoublyLinkedList<String> filtered = list.filter(value -> value.length() == 2);

        assertEquals("bb,dd", joined(filtered));
        assertEquals("a,bb,c,dd", joined(list));
    }

    private static DoublyLinkedList<String> listOf(String... values) {
        DoublyLinkedList<String> list = new DoublyLinkedList<>();
        for (String value : values) {
            list.addLast(value);
        }
        return list;
    }

    private static String joined(DoublyLinkedList<String> list) {
        return String.join(",", list);
    }
}
