package functions;

public class Main {
    // Константа для сравнения double
    private static final double EPSILON = 1e-9;

    public static void main(String[] args) {
        System.out.println("Начало теста функций");

        testFunctionalEquality();
        testAllExceptions();
        System.out.println("\nТест завершен");
    }

    // Тест полиморфизма
    public static void testFunctionalEquality() {
        System.out.println("1. Проверка Array и List:");

        // Создание функций
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, 3);
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 10, 3);

        // Установка Y
        double newY = 42.5;
        try {
            arrayFunc.setPointY(1, newY);
            listFunc.setPointY(1, newY);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Ошибка: Не удалось установить ординату.");
        }

        // Интерполяция
        double x_test = 2.5;
        double arrayVal = arrayFunc.getFunctionValue(x_test);
        double listVal = listFunc.getFunctionValue(x_test);

        boolean isEqual = Math.abs(arrayVal - listVal) < EPSILON;
        boolean isCorrect = Math.abs(arrayVal - 21.25) < EPSILON;

        System.out.printf("Интерполяция (x=%.1f): Array=%.2f, List=%.2f\n", x_test, arrayVal, listVal);
        System.out.println("Сравнение результатов: " + (isEqual ? "Ок" : "Ошибка"));
        System.out.println("Проверка значения: " + (isCorrect ? "Ок" : "Ошибка"));
    }

    // Тест исключений
    public static void testAllExceptions() {
        System.out.println("2. Проверка исключений:");

        // --- Array Тесты ---
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, new double[]{1, 2, 3});

        // 2.1. Ошибка индекса
        try {
            arrayFunc.getPoint(100);
            System.out.println("Array Ошибка: Индекс не проверен.");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Array Ок: Индекс вне границ пойман.");
        }

        // 2.2. IllegalStateException
        try {
            arrayFunc.deletePoint(1);
            arrayFunc.deletePoint(1);
            System.out.println("Array Ошибка: Удалили последнюю точку.");
        } catch (IllegalStateException e) {
            System.out.println("Array Ок: Пойман IllegalStateException.");
        } catch (Exception e) {
            System.out.println("Array Ошибка: Поймано неправильное исключение: " + e.getClass().getSimpleName());
        }

        // 2.3. Некорректный конструктор
        try {
            new ArrayTabulatedFunction(10, 0, 5);
            System.out.println("Конструктор Ошибка: Не проверен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Конструктор Ок: Пойман IllegalArgumentException.");
        }


        // --- List Тесты ---
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 10, 3);

        // 2.4. Дубликат X
        try {
            double x_to_duplicate = listFunc.getPointX(1);
            listFunc.addPoint(new FunctionPoint(x_to_duplicate, 10));

            System.out.println("List Ошибка: Не пойман дубликат X.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("List Ок: Пойман дубликат X.");
        }

        // 2.5. Нарушение порядка
        try {
            listFunc.setPointX(2, 4.0);
            System.out.println("List Ошибка: Нарушение порядка не проверено.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("List Ок: Пойман InappropriateFunctionPointException.");
        }
    }
}