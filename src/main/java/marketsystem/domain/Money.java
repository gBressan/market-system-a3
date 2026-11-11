package marketsystem.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Valor monetário imutável, sempre com duas casas decimais.
 *
 * <p>Usa {@link BigDecimal} em vez de {@code double} para que somas de preços não
 * acumulem erros de arredondamento (ex.: 0.1 + 0.2 != 0.3 em {@code double}).
 */
public record Money(BigDecimal amount) implements Comparable<Money> {

    private static final int CENTS_SCALE = 2;

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    public Money {
        Objects.requireNonNull(amount, "O valor não pode ser nulo.");
        amount = amount.setScale(CENTS_SCALE, RoundingMode.HALF_EVEN);
    }

    /** Cria um valor a partir de texto no formato {@code 1234.56}. */
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    public Money absolute() {
        return new Money(amount.abs());
    }

    public boolean isNegative() {
        return amount.signum() < 0;
    }

    public boolean isPositive() {
        return amount.signum() > 0;
    }

    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
