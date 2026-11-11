package marketsystem.domain;

import static marketsystem.testing.Assertions.assertEquals;
import static marketsystem.testing.Assertions.assertThrows;
import static marketsystem.testing.Assertions.assertTrue;

import marketsystem.testing.Test;

public final class ShoppingListTest {

    @Test
    void findsProductIgnoringAccentsAndCase() {
        ShoppingList list = new ShoppingList();
        list.add(product("Feijão Preto", 2, "8.00"));

        assertEquals("Feijão Preto", list.find("FEIJAO preto").orElseThrow().name());
        assertTrue(list.find("Arroz").isEmpty(), "produto não cadastrado não é encontrado");
    }

    @Test
    void rejectsDuplicateNames() {
        ShoppingList list = new ShoppingList();
        list.add(product("Café", 1, "20.00"));

        assertThrows(DuplicateProductException.class, () -> list.add(product("cafe", 2, "18.00")));
        assertEquals(1, list.size());
    }

    @Test
    void removesProductFromListAndIndex() {
        ShoppingList list = listWithArrozFeijaoCafe();

        list.remove("feijão");

        assertEquals("Arroz,Café", names(list.all()));
        assertTrue(list.find("Feijão").isEmpty(), "produto removido não é encontrado");
        assertEquals(2, list.indexSnapshot().size());
        assertThrows(ProductNotFoundException.class, () -> list.remove("Feijão"));
    }

    @Test
    void separatesPendingAndPurchasedProducts() {
        ShoppingList list = listWithArrozFeijaoCafe();
        list.registerPurchase("Arroz", 1, Money.of("25.00"));
        list.registerPurchase("Feijão", 1, Money.of("8.00"));

        assertEquals("Feijão,Café", names(list.pending()));
        assertEquals("Arroz,Feijão", names(list.purchased()));
    }

    @Test
    void summaryStaysConsistentAfterEveryOperation() {
        ShoppingList list = listWithArrozFeijaoCafe();
        list.registerPurchase("Arroz", 1, Money.of("25.00"));
        list.registerPurchase("Feijão", 1, Money.of("9.00"));

        ShoppingSummary summary = list.summary();
        assertEquals(3, summary.productCount());
        assertEquals(1, summary.pendingCount());
        assertEquals(1, summary.partialCount());
        assertEquals(1, summary.purchasedCount());
        assertEquals(Money.of("64.00"), summary.estimatedTotal());
        assertEquals(Money.of("34.00"), summary.amountSpent());
        assertEquals(Money.of("36.00"), summary.estimatedPurchased());
        assertEquals(Money.of("28.00"), summary.estimatedPending());
        assertEquals(Money.of("-2.00"), summary.differenceFromEstimate());
        assertEquals(Money.of("62.00"), summary.projectedTotal());

        list.remove("Arroz");
        assertEquals(Money.of("9.00"), list.summary().amountSpent());
        assertEquals(Money.of("36.00"), list.summary().estimatedTotal());
    }

    @Test
    void operationsOnUnknownProductFail() {
        ShoppingList list = new ShoppingList();

        assertThrows(ProductNotFoundException.class, () -> list.registerPurchase("Arroz", 1, Money.ZERO));
        assertThrows(ProductNotFoundException.class, () -> list.changeCategory("Arroz", "Mercearia"));
    }

    private static ShoppingList listWithArrozFeijaoCafe() {
        ShoppingList list = new ShoppingList();
        list.add(product("Arroz", 1, "28.00"));
        list.add(product("Feijão", 2, "8.00"));
        list.add(product("Café", 1, "20.00"));
        return list;
    }

    private static Product product(String name, int plannedQuantity, String estimatedUnitPrice) {
        return new Product(name, "Mercearia", plannedQuantity, Money.of(estimatedUnitPrice));
    }

    private static String names(Iterable<Product> products) {
        StringBuilder names = new StringBuilder();
        for (Product product : products) {
            if (!names.isEmpty()) {
                names.append(',');
            }
            names.append(product.name());
        }
        return names.toString();
    }
}
