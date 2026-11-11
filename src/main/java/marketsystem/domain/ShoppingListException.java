package marketsystem.domain;

/**
 * Erro de regra de negócio da lista de compras. A mensagem é escrita para ser
 * mostrada diretamente ao usuário.
 */
public abstract class ShoppingListException extends RuntimeException {

    protected ShoppingListException(String message) {
        super(message);
    }
}
