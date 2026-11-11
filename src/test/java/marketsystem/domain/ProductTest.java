package marketsystem.domain;

import static marketsystem.testing.Assertions.assertEquals;
import static marketsystem.testing.Assertions.assertThrows;

import marketsystem.testing.Test;

public final class ProductTest {

    @Test
    void startsPendingWithNothingSpent() {
        Product milk = milk(12, "5.00");

        assertEquals(PurchaseStatus.PENDING, milk.status());
        assertEquals(12, milk.pendingQuantity());
        assertEquals(Money.ZERO, milk.amountSpent());
        assertEquals(Money.of("60.00"), milk.estimatedTotal());
    }

    @Test
    void partialPurchaseUpdatesQuantitiesAndValues() {
        Product milk = milk(12, "5.00");

        milk.registerPurchase(5, Money.of("5.50"));

        assertEquals(PurchaseStatus.PARTIAL, milk.status());
        assertEquals(7, milk.pendingQuantity());
        assertEquals(Money.of("27.50"), milk.amountSpent());
        assertEquals(Money.of("25.00"), milk.estimatedValueOfPurchased());
        assertEquals(Money.of("35.00"), milk.estimatedValueOfPending());
    }

    @Test
    void severalPurchasesCompleteTheProduct() {
        Product milk = milk(12, "5.00");

        milk.registerPurchase(5, Money.of("5.50"));
        milk.registerPurchase(7, Money.of("4.90"));

        assertEquals(PurchaseStatus.PURCHASED, milk.status());
        assertEquals(0, milk.pendingQuantity());
        assertEquals(Money.of("61.80"), milk.amountSpent());
    }

    @Test
    void rejectsBuyingMoreThanPending() {
        Product milk = milk(2, "5.00");

        assertThrows(InvalidOperationException.class, () -> milk.registerPurchase(3, Money.of("5.00")));
        assertThrows(InvalidOperationException.class, () -> milk.registerPurchase(0, Money.of("5.00")));
        assertEquals(0, milk.purchasedQuantity());
    }

    @Test
    void rejectsPurchaseOfFullyPurchasedProduct() {
        Product milk = milk(1, "5.00");
        milk.registerPurchase(1, Money.of("5.00"));

        assertThrows(InvalidOperationException.class, () -> milk.registerPurchase(1, Money.of("5.00")));
    }

    @Test
    void rejectsInvalidData() {
        assertThrows(InvalidOperationException.class, () -> new Product(" ", "Geral", 1, Money.of("1.00")));
        assertThrows(InvalidOperationException.class, () -> new Product("Leite", "Geral", 0, Money.of("1.00")));
        assertThrows(InvalidOperationException.class, () -> new Product("Leite", "Geral", 1, Money.of("-1.00")));
    }

    @Test
    void plannedQuantityCannotDropBelowPurchased() {
        Product milk = milk(12, "5.00");
        milk.registerPurchase(5, Money.of("5.00"));

        assertThrows(InvalidOperationException.class, () -> milk.changePlannedQuantity(4));
        milk.changePlannedQuantity(5);

        assertEquals(PurchaseStatus.PURCHASED, milk.status());
    }

    private static Product milk(int plannedQuantity, String estimatedUnitPrice) {
        return new Product("Leite", "Laticínios", plannedQuantity, Money.of(estimatedUnitPrice));
    }
}
