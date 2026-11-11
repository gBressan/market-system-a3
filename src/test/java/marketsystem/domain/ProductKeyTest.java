package marketsystem.domain;

import static marketsystem.testing.Assertions.assertEquals;

import marketsystem.testing.Test;

public final class ProductKeyTest {

    @Test
    void ignoresCaseAccentsAndExtraSpaces() {
        assertEquals("feijao preto", ProductKey.from("  Feijão   PRETO "));
        assertEquals("acucar refinado", ProductKey.from("Açúcar Refinado"));
    }
}
