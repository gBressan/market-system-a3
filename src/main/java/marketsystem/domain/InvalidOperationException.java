package marketsystem.domain;

/** Dado inválido ou operação que deixaria a lista inconsistente. */
public final class InvalidOperationException extends ShoppingListException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
