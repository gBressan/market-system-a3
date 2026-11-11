package marketsystem.testing;

import java.util.Objects;

/** Verificações usadas pelos testes. Uma falha lança {@link AssertionError}. */
public final class Assertions {

    private Assertions() {
    }

    public static void assertEquals(Object expected, Object actual) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError("esperado <%s>, mas foi <%s>".formatted(expected, actual));
        }
    }

    public static void assertTrue(boolean condition, String description) {
        if (!condition) {
            throw new AssertionError("esperado verdadeiro: " + description);
        }
    }

    public static void assertFalse(boolean condition, String description) {
        assertTrue(!condition, "esperado falso: " + description);
    }

    public static void assertContains(String text, String expectedFragment) {
        if (!text.contains(expectedFragment)) {
            throw new AssertionError("o texto não contém <%s>".formatted(expectedFragment));
        }
    }

    public static <T extends Throwable> T assertThrows(Class<T> expectedType, ThrowingAction action) {
        try {
            action.run();
        } catch (Throwable thrown) {
            if (expectedType.isInstance(thrown)) {
                return expectedType.cast(thrown);
            }
            throw new AssertionError("esperado %s, mas foi lançado %s"
                    .formatted(expectedType.getSimpleName(), thrown), thrown);
        }
        throw new AssertionError("esperado %s, mas nada foi lançado".formatted(expectedType.getSimpleName()));
    }

    @FunctionalInterface
    public interface ThrowingAction {
        void run() throws Throwable;
    }
}
