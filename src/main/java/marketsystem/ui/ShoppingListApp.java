package marketsystem.ui;

import marketsystem.domain.Money;
import marketsystem.domain.Product;
import marketsystem.domain.ProductNotFoundException;
import marketsystem.domain.ShoppingList;
import marketsystem.domain.ShoppingListException;

/**
 * Controla o fluxo da interface de texto: mostra o menu, lê a opção escolhida e
 * conversa com a {@link ShoppingList}. Não contém regra de negócio — só coleta dados
 * e mostra resultados.
 */
public final class ShoppingListApp {

    private static final String DEFAULT_CATEGORY = "Geral";

    private final ShoppingList shoppingList;
    private final ConsoleInput input;
    private final ConsoleView view;

    public ShoppingListApp(ShoppingList shoppingList, ConsoleInput input, ConsoleView view) {
        this.shoppingList = shoppingList;
        this.input = input;
        this.view = view;
    }

    public void run() {
        view.showWelcome();
        try {
            runMenuLoop();
        } catch (InputClosedException inputClosed) {
            view.showGoodbye();
        }
    }

    private void runMenuLoop() {
        MenuOption option;
        do {
            view.showMenu();
            option = input.readUntilValid("Escolha uma opção", MenuOption::fromCode, "Opção inválida.");
            executeSafely(option);
        } while (option != MenuOption.EXIT);
    }

    private void executeSafely(MenuOption option) {
        try {
            execute(option);
        } catch (ShoppingListException businessError) {
            view.showError(businessError.getMessage());
        }
    }

    private void execute(MenuOption option) {
        switch (option) {
            case ADD_PRODUCT -> addProduct();
            case FIND_PRODUCT -> findProduct();
            case REGISTER_PURCHASE -> registerPurchase();
            case LIST_ALL -> listAll();
            case LIST_PENDING -> listPending();
            case LIST_PURCHASED -> listPurchased();
            case SHOW_SUMMARY -> view.showSummary(shoppingList.summary());
            case EDIT_PRODUCT -> editProduct();
            case REMOVE_PRODUCT -> removeProduct();
            case LOAD_SAMPLE -> loadSample();
            case SHOW_INDEX -> view.showIndex(shoppingList.indexSnapshot());
            case EXIT -> view.showGoodbye();
        }
    }

    private void addProduct() {
        String name = input.readRequiredText("Nome do produto");
        shoppingList.requireNameAvailable(name);
        String category = input.readOptionalText("Categoria [Enter = %s]".formatted(DEFAULT_CATEGORY))
                .orElse(DEFAULT_CATEGORY);
        int plannedQuantity = input.readPositiveInt("Quantidade planejada");
        Money estimatedUnitPrice = input.readMoney("Preço estimado por unidade (R$)");

        Product product = new Product(name, category, plannedQuantity, estimatedUnitPrice);
        shoppingList.add(product);
        view.showSuccess("Produto cadastrado.");
        view.showProductDetails(product);
    }

    private void findProduct() {
        view.showProductDetails(askForExistingProduct());
    }

    private void registerPurchase() {
        Product product = askForExistingProduct();
        view.showProductDetails(product);
        if (product.isFullyPurchased()) {
            view.showInfo("Este produto já foi totalmente comprado.");
            return;
        }
        int quantity = askPurchasedQuantity(product);
        Money unitPricePaid = askUnitPricePaid(product);

        shoppingList.registerPurchase(product.name(), quantity, unitPricePaid);
        view.showSuccess("Compra registrada. Novo status: " + product.status().label());
    }

    private int askPurchasedQuantity(Product product) {
        int pending = product.pendingQuantity();
        String prompt = "Quantidade comprada [Enter = todas as %d pendentes]".formatted(pending);
        return input.readOptionalIntInRange(prompt, 1, pending).orElse(pending);
    }

    private Money askUnitPricePaid(Product product) {
        Money estimated = product.estimatedUnitPrice();
        String prompt = "Preço pago por unidade [Enter = %s]".formatted(MoneyText.format(estimated));
        return input.readOptionalMoney(prompt).orElse(estimated);
    }

    private void listAll() {
        view.showProductTable("TODOS OS PRODUTOS", shoppingList.all());
    }

    private void listPending() {
        view.showProductTable("ITENS PENDENTES", shoppingList.pending());
        view.showPendingTotal(shoppingList.summary());
    }

    private void listPurchased() {
        view.showProductTable("ITENS ADQUIRIDOS", shoppingList.purchased());
        view.showPurchasedTotal(shoppingList.summary());
    }

    private void editProduct() {
        Product product = askForExistingProduct();
        view.showProductDetails(product);
        view.showInfo("Pressione Enter para manter o valor atual.");
        String name = product.name();

        input.readOptionalText("Nova categoria")
                .ifPresent(category -> shoppingList.changeCategory(name, category));
        input.readOptionalIntAtLeast("Nova quantidade planejada", minimumPlannedQuantity(product))
                .ifPresent(quantity -> shoppingList.changePlannedQuantity(name, quantity));
        input.readOptionalMoney("Novo preço estimado por unidade")
                .ifPresent(price -> shoppingList.changeEstimatedUnitPrice(name, price));

        view.showSuccess("Produto atualizado.");
        view.showProductDetails(product);
    }

    private static int minimumPlannedQuantity(Product product) {
        return Math.max(1, product.purchasedQuantity());
    }

    private void removeProduct() {
        Product product = askForExistingProduct();
        view.showProductDetails(product);
        if (input.confirm("Confirma a remoção")) {
            shoppingList.remove(product.name());
            view.showSuccess("Produto removido.");
        } else {
            view.showInfo("Remoção cancelada.");
        }
    }

    private void loadSample() {
        if (!shoppingList.isEmpty()) {
            view.showError("A lista de exemplo só pode ser carregada com a lista vazia.");
            return;
        }
        SampleData.loadInto(shoppingList);
        view.showSuccess("Lista de exemplo carregada com %d produtos.".formatted(shoppingList.size()));
    }

    private Product askForExistingProduct() {
        String name = input.readRequiredText("Nome do produto");
        return shoppingList.find(name).orElseThrow(() -> new ProductNotFoundException(name));
    }
}
