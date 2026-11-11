package marketsystem.testing;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import marketsystem.datastructure.DoublyLinkedListTest;
import marketsystem.datastructure.HashTableTest;
import marketsystem.domain.ProductKeyTest;
import marketsystem.domain.ProductTest;
import marketsystem.domain.ShoppingListTest;
import marketsystem.ui.MoneyTextTest;
import marketsystem.ui.ShoppingListAppTest;

/**
 * Executor de testes mínimo, sem bibliotecas externas: roda todo método anotado com
 * {@link Test} nas classes listadas e encerra com código 1 se algum falhar.
 */
public final class TestRunner {

    private static final List<Class<?>> TEST_CLASSES = List.of(
            DoublyLinkedListTest.class,
            HashTableTest.class,
            ProductKeyTest.class,
            ProductTest.class,
            ShoppingListTest.class,
            MoneyTextTest.class,
            ShoppingListAppTest.class);

    private int passed;
    private int failed;

    public static void main(String[] args) {
        TestRunner runner = new TestRunner();
        TEST_CLASSES.forEach(runner::runTestsOf);
        runner.printReport();
        if (runner.failed > 0) {
            System.exit(1);
        }
    }

    private void runTestsOf(Class<?> testClass) {
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Test.class)) {
                runTest(testClass, method);
            }
        }
    }

    private void runTest(Class<?> testClass, Method method) {
        String testName = testClass.getSimpleName() + "." + method.getName();
        try {
            method.setAccessible(true);
            method.invoke(newInstanceOf(testClass));
            passed++;
            System.out.println("[OK]     " + testName);
        } catch (InvocationTargetException testFailure) {
            reportFailure(testName, testFailure.getCause());
        } catch (ReflectiveOperationException setupFailure) {
            reportFailure(testName, setupFailure);
        }
    }

    private static Object newInstanceOf(Class<?> testClass) throws ReflectiveOperationException {
        Constructor<?> constructor = testClass.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private void reportFailure(String testName, Throwable cause) {
        failed++;
        System.out.println("[FALHOU] " + testName + " -> " + cause);
    }

    private void printReport() {
        System.out.println();
        System.out.printf("%d teste(s) passaram, %d falharam.%n", passed, failed);
    }
}
