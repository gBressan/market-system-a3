package marketsystem.ui;

import static marketsystem.testing.Assertions.assertEquals;

import java.util.Optional;

import marketsystem.domain.Money;
import marketsystem.testing.Test;

public final class MoneyTextTest {

    @Test
    void formatsInBrazilianStyle() {
        assertEquals("R$ 1.234,50", MoneyText.format(Money.of("1234.5")));
        assertEquals("R$ 0,00", MoneyText.format(Money.ZERO));
    }

    @Test
    void parsesCommonUserInputs() {
        assertEquals(Optional.of(Money.of("4.50")), MoneyText.parse("4,50"));
        assertEquals(Optional.of(Money.of("4.50")), MoneyText.parse("4.50"));
        assertEquals(Optional.of(Money.of("4.50")), MoneyText.parse("R$ 4,50"));
        assertEquals(Optional.of(Money.of("1234.56")), MoneyText.parse("1.234,56"));
        assertEquals(Optional.of(Money.of("10.00")), MoneyText.parse("10"));
    }

    @Test
    void rejectsInvalidOrNegativeInputs() {
        assertEquals(Optional.empty(), MoneyText.parse("abc"));
        assertEquals(Optional.empty(), MoneyText.parse(""));
        assertEquals(Optional.empty(), MoneyText.parse("-3,00"));
    }
}
