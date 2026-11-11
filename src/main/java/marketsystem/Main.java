package marketsystem;

import marketsystem.domain.ShoppingList;
import marketsystem.ui.ConsoleInput;
import marketsystem.ui.ConsoleView;
import marketsystem.ui.ShoppingListApp;

/** Ponto de entrada: monta as dependências e inicia a interface de texto. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        ShoppingListApp app = new ShoppingListApp(
                new ShoppingList(),
                ConsoleInput.fromSystemIn(),
                new ConsoleView(System.out));
        app.run();
    }
}
