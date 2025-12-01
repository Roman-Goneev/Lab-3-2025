package functions;

public class Main {
    private static final double EPSILON = 1e-9;

    double[] initialX = {0, 5, 10}; //
    double[] initialY = {0, 50, 10};

    public static void main(String[] args) {
        System.out.println("****** НАЧАЛО ТЕСТИРОВАНИЯ ******");

        testFunctionalEquality();

        System.out.println("\n---------------------------------------------------------");
        testOperationsAndState(); // Подробное тестирование операций и состояния

        System.out.println("\n---------------------------------------------------------");
        testAllExceptions();      // Тестирование крайних случаев и исключений

        System.out.println("\n****** ТЕСТ ЗАВЕРШЕН ******");
    }

    // Вспомогательный метод для печати состояния
    public static void printFunction(TabulatedFunction func, String name) {
        System.out.println("  Состояние " + name + " (Size: " + func.getPointsCount() + "):");
        for (int i = 0; i < func.getPointsCount(); i++) {
            try {
                System.out.printf("    [%d]: X=%.2f, Y=%.2f\n", i, func.getPointX(i), func.getPointY(i));
            } catch (FunctionPointIndexOutOfBoundsException e) {
                // Должно быть невозможно в этом тесте
            }
        }
    }

    // 1. Тест полиморфизма и функциональной корректности
    public static void testFunctionalEquality() {
        System.out.println("\n[1] Тест полиморфизма:");

        // Создание функций
        // Создает с одинаковыми точками: (0, 0), (5, 50), (10, 10)
        double[] initialX = {0, 5, 10};
        double[] initialY = {0, 50, 10};
        TabulatedFunction arrayFunc = new ArrayTabulatedFunction(initialX, initialY);
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(initialX, initialY);

        // Изменение ординаты через интерфейс TabulatedFunction
        double newY = 42.5;
        try {
            arrayFunc.setPointY(1, newY);
            listFunc.setPointY(1, newY);
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.err.println("Ошибка при setPointY: " + e.getMessage());
        }

        // Интерполяция в X=2.5.
        // Точки (0, 0) и (5, 42.5). Уравнение: y = 8.5 * x
        double x_test = 2.5;
        double arrayVal = arrayFunc.getFunctionValue(x_test);
        double listVal = listFunc.getFunctionValue(x_test);
        double expectedVal = 21.25;

        boolean isEqual = Math.abs(arrayVal - listVal) < EPSILON;
        boolean isCorrect = Math.abs(arrayVal - expectedVal) < EPSILON;

        System.out.printf("  Интерполяция (x=%.1f): Array=%.2f, List=%.2f\n", x_test, arrayVal, listVal);
        System.out.println("  Сравнение результатов Array и List: " + (isEqual ? "Ок" : "Ошибка"));
        System.out.println("  Проверка значения (Ожидается 21.25): " + (isCorrect ? "Ок" : "Ошибка"));
    }

    // 2. Подробный тест операций с демонстрацией состояния
    public static void testOperationsAndState() {
        System.out.println("\n[2] Тест операций: добавление, удаление, изменение");

        // Создаем функцию для операций
        double[] initialX = {10, 20, 30, 40};
        double[] initialY = {100, 200, 300, 400};
        TabulatedFunction func = new ArrayTabulatedFunction(initialX, initialY);

        // 2.1. Операция: setPointY
        try {
            System.out.println("\n-- 2.1. setPointY (Индекс 2: X=30) --");
            printFunction(func, "До setPointY");
            func.setPointY(2, 333.33);
            printFunction(func, "После setPointY (Y=333.33)");
        } catch (Exception e) {}

        // 2.2. Операция: addPoint
        try {
            System.out.println("\n-- 2.2. addPoint (X=25.0, Y=250.0) --");
            printFunction(func, "До addPoint");
            func.addPoint(new FunctionPoint(25.0, 250.0));
            printFunction(func, "После addPoint (X=25.0 вставлен)");
        } catch (Exception e) {}

        // 2.3. Операция: deletePoint
        try {
            System.out.println("\n-- 2.3. deletePoint (Индекс 0: X=10.0) --");
            printFunction(func, "До deletePoint");
            func.deletePoint(0);
            printFunction(func, "После deletePoint (X=10.0 удален)");
        } catch (Exception e) {}
    }

    // 3. Тест исключений
    public static void testAllExceptions() {
        System.out.println("\n[3] Тест исключений:");

        // 3.1. IllegalArgumentException (Некорректный конструктор)
        System.out.println("\n-- 3.1. IllegalArgumentException --");
        try {
            // Тест 1: Левая граница >= Правой
            new ArrayTabulatedFunction(10, 0, 5);
            System.out.println("Ошибка: Не пойман X_начало >= X_конец.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ок: Пойман IllegalArgumentException (Границы).");
        } catch (Exception e) {
            System.out.println("Ошибка: Поймано неверное исключение: " + e.getClass().getSimpleName());
        }

        // 3.2. FunctionPointIndexOutOfBoundsException
        System.out.println("\n-- 3.2. FunctionPointIndexOutOfBoundsException --");
        TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 10, 3);
        try {
            listFunc.getPoint(100); // 3 точки, индекс 100 - вне границ
            System.out.println("Ошибка: Индекс не проверен.");
        } catch (FunctionPointIndexOutOfBoundsException e) {
            System.out.println("Ок: Пойман FunctionPointIndexOutOfBoundsException (Индекс).");
        } catch (Exception e) {
            System.out.println("Ошибка: Поймано неверное исключение: " + e.getClass().getSimpleName());
        }

        // 3.3. InappropriateFunctionPointException (Дубликат X)
        System.out.println("\n-- 3.3. InappropriateFunctionPointException (Дубликат X) --");
        try {
            double x_to_duplicate = listFunc.getPointX(1); // Берем X средней точки
            listFunc.addPoint(new FunctionPoint(x_to_duplicate, 99));

            System.out.println("Ошибка: Не пойман дубликат X.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Ок: Пойман InappropriateFunctionPointException (Дубликат X).");
        } catch (Exception e) {
            System.out.println("Ошибка: Поймано неверное исключение: " + e.getClass().getSimpleName());
        }

        // 3.4. InappropriateFunctionPointException (Нарушение порядка)
        System.out.println("\n-- 3.4. InappropriateFunctionPointException (Нарушение порядка) --");
        try {
            // У ListFunc точки 0, 5, 10. Устанавливаем X[1] = 1.0 (X_prev=0.0). X=1.0 не > X_prev.
            listFunc.setPointX(1, 0.0);
            System.out.println("Ошибка: Нарушение порядка не проверено.");
        } catch (InappropriateFunctionPointException e) {
            System.out.println("Ок: Пойман InappropriateFunctionPointException (Нарушение порядка).");
        } catch (Exception e) {
            System.out.println("Ошибка: Поймано неверное исключение: " + e.getClass().getSimpleName());
        }

        // 3.5. IllegalStateException (Удаление при size < 3)
        System.out.println("\n-- 3.5. IllegalStateException (Удаление при size < 3) --");
        try {
            // Удаляем точки до размера 2
            while (listFunc.getPointsCount() > 2) {
                listFunc.deletePoint(0);
            }
            listFunc.deletePoint(0); // Попытка удаления при size=2
            System.out.println("Ошибка: Не пойман IllegalStateException при size=2.");
        } catch (IllegalStateException e) {
            System.out.println("Ок: Пойман IllegalStateException (Size < 3).");
        } catch (Exception e) {
            System.out.println("Ошибка: Поймано неверное исключение: " + e.getClass().getSimpleName());
        }
    }
}