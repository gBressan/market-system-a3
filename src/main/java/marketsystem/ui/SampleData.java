package marketsystem.ui;

import marketsystem.domain.Money;
import marketsystem.domain.Product;
import marketsystem.domain.ShoppingList;

/** Lista de exemplo para agilizar a demonstração na apresentação. */
final class SampleData {

    private SampleData() {
    }

    static void loadInto(ShoppingList shoppingList) {
        addProducts(shoppingList);
        registerSomePurchases(shoppingList);
    }

    private static void addProducts(ShoppingList shoppingList) {
        shoppingList.add(product("Arroz 5kg", "Mercearia", 1, "28.90"));
        shoppingList.add(product("Feijão preto 1kg", "Mercearia", 2, "8.49"));
        shoppingList.add(product("Leite integral 1L", "Laticínios", 12, "5.29"));
        shoppingList.add(product("Café 500g", "Mercearia", 1, "19.90"));
        shoppingList.add(product("Banana prata (kg)", "Hortifruti", 2, "6.99"));
        shoppingList.add(product("Peito de frango (kg)", "Açougue", 2, "21.90"));
        shoppingList.add(product("Detergente", "Limpeza", 3, "2.79"));
        shoppingList.add(product("Papel higiênico 12un", "Higiene", 1, "22.50"));
        shoppingList.add(product("Óleo de soja 900ml", "Mercearia", 1, "7.99"));
        shoppingList.add(product("Macarrão 500g", "Mercearia", 3, "4.59"));
    }

    private static void registerSomePurchases(ShoppingList shoppingList) {
        shoppingList.registerPurchase("Arroz 5kg", 1, Money.of("27.50"));
        shoppingList.registerPurchase("Leite integral 1L", 6, Money.of("5.49"));
        shoppingList.registerPurchase("Detergente", 3, Money.of("2.79"));
        shoppingList.registerPurchase("Café 500g", 1, Money.of("21.40"));
    }

    private static Product product(String name, String category, int quantity, String estimatedUnitPrice) {
        return new Product(name, category, quantity, Money.of(estimatedUnitPrice));
    }
}
