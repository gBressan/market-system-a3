package marketsystem.ui;

import java.io.PrintStream;
import java.util.Locale;

import marketsystem.datastructure.HashTableSnapshot;
import marketsystem.domain.Money;
import marketsystem.domain.Product;
import marketsystem.domain.ShoppingSummary;

/** Tudo o que é escrito na tela: menu, tabelas, detalhes, resumo e mensagens. */
public final class ConsoleView {

    private static final Locale BRAZIL = Locale.of("pt", "BR");
    private static final String TABLE_ROW = "%-3s %-22s %-13s %9s %6s %12s %14s %12s  %s%n";
    private static final int NAME_WIDTH = 22;
    private static final int CATEGORY_WIDTH = 13;
    private static final String ELLIPSIS = "...";
    private static final String SECTION_LINE = "=".repeat(60);
    private static final String TABLE_LINE = "-".repeat(108);
    private static final String CHAIN_ARROW = " -> ";

    private final PrintStream out;

    public ConsoleView(PrintStream out) {
        this.out = out;
    }

    public void showWelcome() {
        showTitle("SISTEMA DE LISTA DE COMPRAS");
        out.println("Os dados ficam só na memória e são perdidos ao sair.");
    }

    public void showMenu() {
        showTitle("MENU");
        for (MenuOption option : MenuOption.values()) {
            out.printf("%3s - %s%n", option.code(), option.description());
        }
    }

    public void showProductDetails(Product product) {
        out.println();
        out.println("Produto ..............: " + product.name());
        out.println("Categoria ............: " + product.category());
        out.println("Status ...............: " + product.status().label());
        out.printf("Quantidade ...........: %d comprada(s) de %d planejada(s), faltam %d%n",
                product.purchasedQuantity(), product.plannedQuantity(), product.pendingQuantity());
        out.println("Preço estimado (un.) .: " + MoneyText.format(product.estimatedUnitPrice()));
        out.println("Valor estimado total .: " + MoneyText.format(product.estimatedTotal()));
        out.println("Estimado p/ pendentes : " + MoneyText.format(product.estimatedValueOfPending()));
        out.println("Valor já gasto .......: " + MoneyText.format(product.amountSpent()));
    }

    public void showProductTable(String title, Iterable<Product> products) {
        showTitle(title);
        out.printf(TABLE_ROW, "#", "Produto", "Categoria", "Comprado", "Falta",
                "Preço est.", "Est. pendente", "Gasto", "Status");
        out.println(TABLE_LINE);
        int position = 0;
        for (Product product : products) {
            position++;
            showProductRow(position, product);
        }
        if (position == 0) {
            out.println("(nenhum produto)");
        }
    }

    public void showPendingTotal(ShoppingSummary summary) {
        out.println(TABLE_LINE);
        out.println("Valor estimado para os itens pendentes: " + MoneyText.format(summary.estimatedPending()));
    }

    public void showPurchasedTotal(ShoppingSummary summary) {
        out.println(TABLE_LINE);
        out.printf("Total gasto: %s (estimado para esses itens: %s)%n",
                MoneyText.format(summary.amountSpent()), MoneyText.format(summary.estimatedPurchased()));
    }

    public void showSummary(ShoppingSummary summary) {
        showTitle("RESUMO DA LISTA");
        out.printf("Produtos cadastrados ......: %d (%d pendente(s), %d parcial(is), %d comprado(s))%n",
                summary.productCount(), summary.pendingCount(), summary.partialCount(), summary.purchasedCount());
        out.println("Valor estimado da lista ...: " + MoneyText.format(summary.estimatedTotal()));
        out.println("Já gasto ..................: " + MoneyText.format(summary.amountSpent()));
        out.println("Estimado do que foi pego ..: " + MoneyText.format(summary.estimatedPurchased()));
        out.println("Diferença .................: " + describeDifference(summary.differenceFromEstimate()));
        out.println("Estimado do que falta .....: " + MoneyText.format(summary.estimatedPending()));
        out.println("Previsão de gasto final ...: " + MoneyText.format(summary.projectedTotal()));
    }

    public void showIndex(HashTableSnapshot<String> snapshot) {
        showTitle("TABELA HASH (índice nome -> produto)");
        out.printf(BRAZIL, "Capacidade: %d buckets | Chaves: %d | Fator de carga: %.2f%n",
                snapshot.capacity(), snapshot.size(), snapshot.loadFactor());
        out.printf("Buckets ocupados: %d | Buckets com colisão: %d%n",
                snapshot.occupiedBuckets().size(), snapshot.collisionBucketCount());
        out.println();
        for (HashTableSnapshot.Bucket<String> bucket : snapshot.occupiedBuckets()) {
            showBucket(bucket);
        }
        if (snapshot.occupiedBuckets().isEmpty()) {
            out.println("(tabela vazia)");
        }
    }

    public void showSuccess(String message) {
        out.println("[OK] " + message);
    }

    public void showInfo(String message) {
        out.println("[i] " + message);
    }

    public void showError(String message) {
        out.println("[ERRO] " + message);
    }

    public void showGoodbye() {
        out.println("Até a próxima!");
    }

    private void showProductRow(int position, Product product) {
        out.printf(TABLE_ROW,
                position,
                truncate(product.name(), NAME_WIDTH),
                truncate(product.category(), CATEGORY_WIDTH),
                product.purchasedQuantity() + "/" + product.plannedQuantity(),
                product.pendingQuantity(),
                MoneyText.format(product.estimatedUnitPrice()),
                MoneyText.format(product.estimatedValueOfPending()),
                MoneyText.format(product.amountSpent()),
                product.status().label());
    }

    private void showBucket(HashTableSnapshot.Bucket<String> bucket) {
        String chain = String.join(CHAIN_ARROW, bucket.keys());
        String collisionNote = bucket.hasCollision() ? "   <- colisão" : "";
        out.printf("[%3d] %s%s%n", bucket.index(), chain, collisionNote);
    }

    private void showTitle(String title) {
        out.println();
        out.println(SECTION_LINE);
        out.println(" " + title);
        out.println(SECTION_LINE);
    }

    private static String describeDifference(Money difference) {
        if (difference.isPositive()) {
            return MoneyText.format(difference) + " acima do estimado";
        }
        if (difference.isNegative()) {
            return MoneyText.format(difference.absolute()) + " de economia";
        }
        return "igual ao estimado";
    }

    private static String truncate(String text, int maxWidth) {
        if (text.length() <= maxWidth) {
            return text;
        }
        return text.substring(0, maxWidth - ELLIPSIS.length()) + ELLIPSIS;
    }
}
