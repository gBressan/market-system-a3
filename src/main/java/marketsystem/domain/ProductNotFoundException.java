package marketsystem.domain;

public final class ProductNotFoundException extends ShoppingListException {

    public ProductNotFoundException(String productName) {
        super("Produto \"%s\" não encontrado na lista.".formatted(productName));
    }
}
