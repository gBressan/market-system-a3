package marketsystem.domain;

public final class DuplicateProductException extends ShoppingListException {

    public DuplicateProductException(String productName) {
        super("Já existe um produto chamado \"%s\" na lista.".formatted(productName));
    }
}
