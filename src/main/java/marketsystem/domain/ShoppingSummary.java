package marketsystem.domain;

/**
 * Totais da lista de compras em um determinado momento.
 *
 * @param pendingCount       produtos sem nenhuma unidade comprada
 * @param partialCount       produtos com parte da quantidade comprada
 * @param purchasedCount     produtos totalmente comprados
 * @param estimatedTotal     valor estimado de toda a lista
 * @param estimatedPurchased valor estimado da parte já comprada
 * @param estimatedPending   valor estimado da parte que ainda falta comprar
 * @param amountSpent        valor realmente gasto até agora
 */
public record ShoppingSummary(
        int pendingCount,
        int partialCount,
        int purchasedCount,
        Money estimatedTotal,
        Money estimatedPurchased,
        Money estimatedPending,
        Money amountSpent) {

    /** Calcula os totais percorrendo os produtos uma única vez. O(n). */
    public static ShoppingSummary of(Iterable<Product> products) {
        Accumulator accumulator = new Accumulator();
        products.forEach(accumulator::add);
        return accumulator.toSummary();
    }

    public int productCount() {
        return pendingCount + partialCount + purchasedCount;
    }

    /** Previsão de gasto final: o que já foi gasto mais o estimado do que falta. */
    public Money projectedTotal() {
        return amountSpent.plus(estimatedPending);
    }

    /**
     * Diferença entre o gasto real e o estimado dos itens já comprados.
     * Positivo: gastou acima do estimado. Negativo: economizou.
     */
    public Money differenceFromEstimate() {
        return amountSpent.minus(estimatedPurchased);
    }

    private static final class Accumulator {

        private int pendingCount;
        private int partialCount;
        private int purchasedCount;
        private Money estimatedTotal = Money.ZERO;
        private Money estimatedPurchased = Money.ZERO;
        private Money estimatedPending = Money.ZERO;
        private Money amountSpent = Money.ZERO;

        private void add(Product product) {
            countStatus(product.status());
            estimatedTotal = estimatedTotal.plus(product.estimatedTotal());
            estimatedPurchased = estimatedPurchased.plus(product.estimatedValueOfPurchased());
            estimatedPending = estimatedPending.plus(product.estimatedValueOfPending());
            amountSpent = amountSpent.plus(product.amountSpent());
        }

        private void countStatus(PurchaseStatus status) {
            switch (status) {
                case PENDING -> pendingCount++;
                case PARTIAL -> partialCount++;
                case PURCHASED -> purchasedCount++;
            }
        }

        private ShoppingSummary toSummary() {
            return new ShoppingSummary(pendingCount, partialCount, purchasedCount,
                    estimatedTotal, estimatedPurchased, estimatedPending, amountSpent);
        }
    }
}
