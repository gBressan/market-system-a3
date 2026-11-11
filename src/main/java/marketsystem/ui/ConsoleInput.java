package marketsystem.ui;

import java.io.Console;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Function;

import marketsystem.domain.Money;

/**
 * Lê dados digitados no terminal, repetindo a pergunta até receber um valor válido.
 *
 * <p>Os métodos {@code readOptional...} aceitam Enter vazio e devolvem
 * {@link Optional#empty()}, o que a interface usa como "manter o valor atual" ou
 * "usar o valor sugerido".
 */
public final class ConsoleInput {

    private static final String INVALID_PREFIX = "  ! ";

    private final Scanner scanner;
    private final PrintStream out;

    public ConsoleInput(InputStream in, Charset charset, PrintStream out) {
        this.scanner = new Scanner(in, charset);
        this.out = out;
    }

    /** Lê do teclado usando a codificação do terminal, para que acentos funcionem no Windows. */
    public static ConsoleInput fromSystemIn() {
        Console console = System.console();
        Charset charset = console != null ? console.charset() : Charset.defaultCharset();
        return new ConsoleInput(System.in, charset, System.out);
    }

    public String readRequiredText(String prompt) {
        return readUntilValid(prompt, ConsoleInput::nonBlank, "Este campo é obrigatório.");
    }

    public Optional<String> readOptionalText(String prompt) {
        return readOptional(prompt, Optional::of, "");
    }

    public int readPositiveInt(String prompt) {
        return readUntilValid(prompt, text -> parseIntAtLeast(text, 1),
                "Digite um número inteiro maior que zero.");
    }

    public Optional<Integer> readOptionalIntAtLeast(String prompt, int minimum) {
        return readOptional(prompt, text -> parseIntAtLeast(text, minimum),
                "Digite um número inteiro maior ou igual a %d.".formatted(minimum));
    }

    public Optional<Integer> readOptionalIntInRange(String prompt, int minimum, int maximum) {
        return readOptional(prompt, text -> parseIntAtLeast(text, minimum).filter(number -> number <= maximum),
                "Digite um número inteiro entre %d e %d.".formatted(minimum, maximum));
    }

    public Money readMoney(String prompt) {
        return readUntilValid(prompt, MoneyText::parse, "Digite um valor válido, por exemplo 4,50.");
    }

    public Optional<Money> readOptionalMoney(String prompt) {
        return readOptional(prompt, MoneyText::parse, "Digite um valor válido, por exemplo 4,50.");
    }

    public boolean confirm(String question) {
        return readUntilValid(question + " (s/n)", ConsoleInput::parseYesNo, "Responda com s ou n.");
    }

    /** Pergunta até que o {@code parser} consiga interpretar o texto digitado. */
    public <T> T readUntilValid(String prompt, Function<String, Optional<T>> parser, String errorMessage) {
        while (true) {
            Optional<T> parsed = parser.apply(readLine(prompt));
            if (parsed.isPresent()) {
                return parsed.get();
            }
            out.println(INVALID_PREFIX + errorMessage);
        }
    }

    private <T> Optional<T> readOptional(String prompt, Function<String, Optional<T>> parser, String errorMessage) {
        while (true) {
            String text = readLine(prompt);
            if (text.isEmpty()) {
                return Optional.empty();
            }
            Optional<T> parsed = parser.apply(text);
            if (parsed.isPresent()) {
                return parsed;
            }
            out.println(INVALID_PREFIX + errorMessage);
        }
    }

    private String readLine(String prompt) {
        out.print(prompt + ": ");
        out.flush();
        if (!scanner.hasNextLine()) {
            throw new InputClosedException();
        }
        return scanner.nextLine().trim();
    }

    private static Optional<String> nonBlank(String text) {
        return text.isBlank() ? Optional.empty() : Optional.of(text);
    }

    private static Optional<Integer> parseIntAtLeast(String text, int minimum) {
        try {
            int number = Integer.parseInt(text);
            return number >= minimum ? Optional.of(number) : Optional.empty();
        } catch (NumberFormatException notANumber) {
            return Optional.empty();
        }
    }

    private static Optional<Boolean> parseYesNo(String text) {
        return switch (text.toLowerCase(Locale.ROOT)) {
            case "s", "sim" -> Optional.of(true);
            case "n", "nao", "não" -> Optional.of(false);
            default -> Optional.empty();
        };
    }
}
