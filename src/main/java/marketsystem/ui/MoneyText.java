package marketsystem.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Optional;

import marketsystem.domain.Money;

/** Converte valores monetários de/para texto no padrão brasileiro (R$ 1.234,56). */
public final class MoneyText {

    private static final Locale BRAZIL = Locale.of("pt", "BR");
    private static final String CURRENCY_SYMBOL = "R$";
    private static final String DECIMAL_COMMA = ",";
    private static final String THOUSANDS_DOT = ".";
    private static final String DECIMAL_POINT = ".";

    private MoneyText() {
    }

    public static String format(Money money) {
        DecimalFormat format = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(BRAZIL));
        return CURRENCY_SYMBOL + " " + format.format(money.amount());
    }

    /**
     * Lê um valor digitado pelo usuário. Aceita "4,50", "4.50", "R$ 4,50" e "1.234,56".
     *
     * @return vazio se o texto não for um valor válido e não negativo
     */
    public static Optional<Money> parse(String text) {
        try {
            Money money = new Money(new BigDecimal(toPlainDecimal(text)));
            return money.isNegative() ? Optional.empty() : Optional.of(money);
        } catch (NumberFormatException invalidNumber) {
            return Optional.empty();
        }
    }

    private static String toPlainDecimal(String text) {
        String value = text.replace(CURRENCY_SYMBOL, "").trim();
        if (value.contains(DECIMAL_COMMA)) {
            return value.replace(THOUSANDS_DOT, "").replace(DECIMAL_COMMA, DECIMAL_POINT);
        }
        return value;
    }
}
