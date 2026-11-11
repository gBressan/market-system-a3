package marketsystem.ui;

import static marketsystem.testing.Assertions.assertContains;
import static marketsystem.testing.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import marketsystem.domain.Money;
import marketsystem.domain.ShoppingList;
import marketsystem.testing.Test;

/** Simula um usuário digitando no terminal e confere o que aparece na tela. */
public final class ShoppingListAppTest {

    @Test
    void registersProductAndPartialPurchase() {
        ShoppingList shoppingList = new ShoppingList();
        String output = runSession(shoppingList,
                "1", "Leite integral", "", "12", "5,29",
                "3", "leite INTEGRAL", "5", "5,49",
                "7",
                "0");

        assertContains(output, "Produto cadastrado.");
        assertContains(output, "Novo status: Parcial");
        assertContains(output, "Já gasto ..................: R$ 27,45");
        assertEquals(Money.of("27.45"), shoppingList.summary().amountSpent());
    }

    @Test
    void enterOnPurchaseBuysEverythingAtEstimatedPrice() {
        ShoppingList shoppingList = new ShoppingList();
        runSession(shoppingList,
                "1", "Café", "Mercearia", "2", "19,90",
                "3", "cafe", "", "",
                "0");

        assertEquals(Money.of("39.80"), shoppingList.summary().amountSpent());
        assertEquals(0, shoppingList.summary().pendingCount());
    }

    @Test
    void showsErrorsAndKeepsRunning() {
        String output = runSession(new ShoppingList(),
                "99",
                "2", "Inexistente",
                "1", "Arroz", "", "abc", "1", "10",
                "1", "ARROZ",
                "0");

        assertContains(output, "Opção inválida.");
        assertContains(output, "Produto \"Inexistente\" não encontrado na lista.");
        assertContains(output, "Digite um número inteiro maior que zero.");
        assertContains(output, "Já existe um produto chamado \"ARROZ\" na lista.");
    }

    @Test
    void loadsSampleAndShowsHashTable() {
        ShoppingList shoppingList = new ShoppingList();
        String output = runSession(shoppingList, "10", "11", "0");

        assertEquals(10, shoppingList.size());
        assertContains(output, "Capacidade: 16 buckets | Chaves: 10");
        assertContains(output, "Buckets com colisão: 1");
        assertContains(output, "detergente -> oleo de soja 900ml   <- colisão");
    }

    @Test
    void endsGracefullyWhenInputIsClosed() {
        String output = runSession(new ShoppingList(), "4");

        assertContains(output, "Até a próxima!");
    }

    private static String runSession(ShoppingList shoppingList, String... typedLines) {
        byte[] typed = (String.join("\n", typedLines) + "\n").getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream screen = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(screen, true, StandardCharsets.UTF_8);

        ConsoleInput input = new ConsoleInput(new ByteArrayInputStream(typed), StandardCharsets.UTF_8, out);
        new ShoppingListApp(shoppingList, input, new ConsoleView(out)).run();

        return screen.toString(StandardCharsets.UTF_8);
    }
}
