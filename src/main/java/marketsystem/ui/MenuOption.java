package marketsystem.ui;

import java.util.Optional;

/** Opções do menu principal, na ordem em que aparecem na tela. */
public enum MenuOption {

    ADD_PRODUCT("1", "Cadastrar produto"),
    FIND_PRODUCT("2", "Consultar produto"),
    REGISTER_PURCHASE("3", "Registrar compra (total ou parcial)"),
    LIST_ALL("4", "Listar todos os produtos"),
    LIST_PENDING("5", "Listar itens pendentes"),
    LIST_PURCHASED("6", "Listar itens adquiridos"),
    SHOW_SUMMARY("7", "Resumo de valores"),
    EDIT_PRODUCT("8", "Editar produto"),
    REMOVE_PRODUCT("9", "Remover produto"),
    LOAD_SAMPLE("10", "Carregar lista de exemplo"),
    SHOW_INDEX("11", "Ver tabela hash (estrutura interna)"),
    EXIT("0", "Sair");

    private final String code;
    private final String description;

    MenuOption(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Optional<MenuOption> fromCode(String code) {
        for (MenuOption option : values()) {
            if (option.code.equals(code)) {
                return Optional.of(option);
            }
        }
        return Optional.empty();
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }
}
