package marketsystem.domain;

import java.text.Normalizer;
import java.util.Locale;

/**
 * Gera a chave usada para localizar um produto pelo nome.
 *
 * <p>A chave ignora maiúsculas, acentos e espaços repetidos, de modo que
 * "Feijão  Preto", "feijao preto" e "FEIJÃO PRETO" apontam para o mesmo produto.
 */
final class ProductKey {

    private static final String COMBINING_MARKS = "\\p{M}";
    private static final String REPEATED_WHITESPACE = "\\s+";
    private static final String SINGLE_SPACE = " ";

    private ProductKey() {
    }

    static String from(String productName) {
        String withoutAccents = Normalizer.normalize(productName, Normalizer.Form.NFD)
                .replaceAll(COMBINING_MARKS, "");
        return withoutAccents.trim()
                .replaceAll(REPEATED_WHITESPACE, SINGLE_SPACE)
                .toLowerCase(Locale.ROOT);
    }
}
