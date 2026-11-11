package marketsystem.domain;

import java.util.Objects;

/**
 * Item da lista de compras.
 *
 * <p>Guarda o que foi planejado (quantidade e preço estimado) e o que já foi comprado
 * (quantidade e valor realmente gasto). Os valores derivados — quantidade pendente,
 * status, valores estimados — são calculados a partir desses campos, nunca guardados
 * separadamente, para que não fiquem inconsistentes.
 *
 * <p>Os métodos que alteram o produto são visíveis só dentro do pacote: toda alteração
 * passa pela {@link ShoppingList}, que é a dona dos produtos.
 */
public final class Product {

    private final String name;
    private final String key;
    private String category;
    private int plannedQuantity;
    private int purchasedQuantity;
    private Money estimatedUnitPrice;
    private Money amountSpent;

    public Product(String name, String category, int plannedQuantity, Money estimatedUnitPrice) {
        this.name = requireText(name, "O nome do produto");
        this.key = ProductKey.from(this.name);
        this.category = requireText(category, "A categoria");
        this.plannedQuantity = requirePositive(plannedQuantity, "A quantidade planejada");
        this.estimatedUnitPrice = requireNonNegative(estimatedUnitPrice, "O preço estimado");
        this.purchasedQuantity = 0;
        this.amountSpent = Money.ZERO;
    }

    void registerPurchase(int quantity, Money unitPricePaid) {
        requireNotFullyPurchased();
        requireValidPurchaseQuantity(quantity);
        requireNonNegative(unitPricePaid, "O preço pago");
        purchasedQuantity += quantity;
        amountSpent = amountSpent.plus(unitPricePaid.times(quantity));
    }

    void changePlannedQuantity(int newPlannedQuantity) {
        requirePositive(newPlannedQuantity, "A quantidade planejada");
        if (newPlannedQuantity < purchasedQuantity) {
            throw new InvalidOperationException(
                    "A quantidade planejada não pode ser menor que a já comprada (%d)."
                            .formatted(purchasedQuantity));
        }
        plannedQuantity = newPlannedQuantity;
    }

    void changeEstimatedUnitPrice(Money newEstimatedUnitPrice) {
        estimatedUnitPrice = requireNonNegative(newEstimatedUnitPrice, "O preço estimado");
    }

    void changeCategory(String newCategory) {
        category = requireText(newCategory, "A categoria");
    }

    public String name() {
        return name;
    }

    String key() {
        return key;
    }

    public String category() {
        return category;
    }

    public int plannedQuantity() {
        return plannedQuantity;
    }

    public int purchasedQuantity() {
        return purchasedQuantity;
    }

    public int pendingQuantity() {
        return plannedQuantity - purchasedQuantity;
    }

    public Money estimatedUnitPrice() {
        return estimatedUnitPrice;
    }

    public Money amountSpent() {
        return amountSpent;
    }

    /** Valor estimado para comprar toda a quantidade planejada. */
    public Money estimatedTotal() {
        return estimatedUnitPrice.times(plannedQuantity);
    }

    /** Quanto a parte já comprada deveria ter custado, pelo preço estimado. */
    public Money estimatedValueOfPurchased() {
        return estimatedUnitPrice.times(purchasedQuantity);
    }

    /** Quanto ainda falta gastar, pelo preço estimado. */
    public Money estimatedValueOfPending() {
        return estimatedUnitPrice.times(pendingQuantity());
    }

    public boolean hasPendingQuantity() {
        return pendingQuantity() > 0;
    }

    public boolean hasPurchasedQuantity() {
        return purchasedQuantity > 0;
    }

    public boolean isFullyPurchased() {
        return !hasPendingQuantity();
    }

    public PurchaseStatus status() {
        if (!hasPurchasedQuantity()) {
            return PurchaseStatus.PENDING;
        }
        return isFullyPurchased() ? PurchaseStatus.PURCHASED : PurchaseStatus.PARTIAL;
    }

    private void requireNotFullyPurchased() {
        if (isFullyPurchased()) {
            throw new InvalidOperationException("\"%s\" já foi totalmente comprado.".formatted(name));
        }
    }

    private void requireValidPurchaseQuantity(int quantity) {
        if (quantity <= 0 || quantity > pendingQuantity()) {
            throw new InvalidOperationException(
                    "A quantidade comprada deve estar entre 1 e %d.".formatted(pendingQuantity()));
        }
    }

    private static String requireText(String value, String fieldDescription) {
        if (value == null || value.isBlank()) {
            throw new InvalidOperationException(fieldDescription + " é obrigatório(a).");
        }
        return value.trim();
    }

    private static int requirePositive(int value, String fieldDescription) {
        if (value <= 0) {
            throw new InvalidOperationException(fieldDescription + " deve ser maior que zero.");
        }
        return value;
    }

    private static Money requireNonNegative(Money value, String fieldDescription) {
        Objects.requireNonNull(value, fieldDescription);
        if (value.isNegative()) {
            throw new InvalidOperationException(fieldDescription + " não pode ser negativo.");
        }
        return value;
    }
}
