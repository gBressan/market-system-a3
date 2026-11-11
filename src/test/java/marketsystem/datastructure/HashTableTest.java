package marketsystem.datastructure;

import static marketsystem.testing.Assertions.assertEquals;
import static marketsystem.testing.Assertions.assertFalse;
import static marketsystem.testing.Assertions.assertTrue;

import java.util.Optional;

import marketsystem.testing.Test;

public final class HashTableTest {

    @Test
    void storesAndFindsValues() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("arroz", 1);
        table.put("feijao", 2);

        assertEquals(Optional.of(1), table.get("arroz"));
        assertEquals(Optional.of(2), table.get("feijao"));
        assertEquals(Optional.empty(), table.get("cafe"));
        assertEquals(2, table.size());
    }

    @Test
    void replacesValueOfExistingKeyWithoutGrowing() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("arroz", 1);
        table.put("arroz", 5);

        assertEquals(Optional.of(5), table.get("arroz"));
        assertEquals(1, table.size());
    }

    @Test
    void removesKeys() {
        HashTable<String, Integer> table = new HashTable<>();
        table.put("arroz", 1);

        assertTrue(table.remove("arroz"), "remove chave existente");
        assertFalse(table.remove("arroz"), "não remove chave inexistente");
        assertFalse(table.containsKey("arroz"), "chave removida não é encontrada");
        assertTrue(table.isEmpty(), "tabela vazia");
    }

    @Test
    void keepsCollidingKeysInTheSameBucket() {
        HashTable<CollidingKey, String> table = new HashTable<>(4);
        CollidingKey first = new CollidingKey("primeira");
        CollidingKey second = new CollidingKey("segunda");
        table.put(first, "A");
        table.put(second, "B");

        assertEquals(Optional.of("A"), table.get(first));
        assertEquals(Optional.of("B"), table.get(second));
        assertEquals(1, table.snapshot().collisionBucketCount());

        table.remove(first);
        assertEquals(Optional.of("B"), table.get(second));
    }

    @Test
    void doublesCapacityWhenLoadFactorIsExceeded() {
        HashTable<Integer, Integer> table = new HashTable<>(4);
        for (int number = 1; number <= 4; number++) {
            table.put(number, number * 10);
        }

        assertEquals(8, table.capacity());
        for (int number = 1; number <= 4; number++) {
            assertEquals(Optional.of(number * 10), table.get(number));
        }
    }

    @Test
    void handlesManyKeys() {
        HashTable<String, Integer> table = new HashTable<>();
        int total = 1_000;
        for (int number = 0; number < total; number++) {
            table.put("produto-" + number, number);
        }

        assertEquals(total, table.size());
        assertEquals(Optional.of(777), table.get("produto-777"));
        assertTrue(table.snapshot().loadFactor() <= 0.75, "fator de carga controlado pelo rehash");
    }

    /** Chave com hash fixo para forçar colisões. */
    private record CollidingKey(String name) {

        @Override
        public int hashCode() {
            return 42;
        }
    }
}
