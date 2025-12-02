package functions;


public class LinkedListTabulatedFunction implements TabulatedFunction{
    private static class FunctionNode{
        FunctionPoint data; // Точка функции
        FunctionNode prev; // Ссылка предыдущий узел
        FunctionNode next; // Ссылка на следующий узел

        FunctionNode(FunctionPoint data, FunctionNode prev, FunctionNode next){
            this.data = data;
            this.prev = prev;
            this.next = next;
        }
    }

    private FunctionNode head; // голова списка (фиктивная)
    private int size; // Количество точек не считая головы
    private FunctionNode lastAccessedNode; // Кэш последнего доступного узла
    private int lastAccessedIndex; // Кэш индекса последнего узла

    // Инициализация пустого списка
    private void initializeList(){
        head = new FunctionNode(null, null, null);
        head.next = head;
        head.prev = head;
        size = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }

    // Поиск узла по индексу
    private FunctionNode getNodeByIndex(int index) {
        // Проверка границ
        if (index < 0 || index >= size) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне границ");
        }

        FunctionNode current;

        // Определяет расстояние до узла
        int diffFromCache = (lastAccessedIndex != -1) ? Math.abs(index - lastAccessedIndex) : Integer.MAX_VALUE;
        int diffFromHead = index;
        int diffFromTail = size - 1 - index;

        //
        if (diffFromCache <= diffFromHead && diffFromCache <= diffFromTail){
            current = lastAccessedNode;

            if (index > lastAccessedIndex){
                for (int i = lastAccessedIndex; i < index; i++) current = current.next;
            }
            else if (index < lastAccessedIndex){
                for (int i = lastAccessedIndex; i > index; i--) current = current.prev;
            }
        }

        // Путь от головы
        else if (diffFromHead <= diffFromTail){
            current = head.next;
            for (int i = 0; i < index; i++) current = current.next;
        }

        else{ // Путь от хвоста
            current = head.prev;
            for (int i = size - 1; i > index; i--) current = current.prev;
        }

        lastAccessedNode = current;
        lastAccessedIndex = index;

        return current;

    }

    // Добавляет узел в конец списка
    private void addNodeToEnd(FunctionPoint point){
        FunctionNode newNode = new FunctionNode(point, null, null);
        FunctionNode lastNode = head.prev;

        lastNode.next = newNode;
        newNode.prev = lastNode;

        // Связывает новый узел с головой
        newNode.next = head;
        head.prev = newNode;

        size++;
    }

    // Пустой список
    public LinkedListTabulatedFunction(){
        initializeList();
    }

    // Конструктор: с заданными x и y (для совместимости с тестами)
    public LinkedListTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length || xValues.length < 2) {
            throw new IllegalArgumentException("Массивы x и y должны иметь одинаковую длину (минимум 2).");
        }

        // Проверка упорядоченности абсцисс
        for (int i = 0; i < xValues.length - 1; i++) {
            if (xValues[i] >= xValues[i + 1]) {
                throw new IllegalArgumentException("Абсциссы должны быть строго упорядочены по возрастанию.");
            }
        }

        initializeList();

        // Добавление точек в список
        for (int i = 0; i < xValues.length; i++) {
            FunctionPoint point = new FunctionPoint(xValues[i], yValues[i]);
            addNodeToEnd(point);
        }
    }

    // Конструктор с равномерным шагом
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount){
        if (leftX >= rightX || pointsCount < 2){
            throw new IllegalArgumentException("Некорректные параметры ");
        }

        initializeList();

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++){
            double x = leftX + i * step;
            FunctionPoint point = new FunctionPoint(x, 0);
            addNodeToEnd(point);
        }
    }

    // Контруктор с заданным y
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values){
        if (leftX >= rightX || values.length < 2){
            throw new IllegalArgumentException("Некорректные параметры");
        }

        initializeList();

        double step = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++){
            double x = leftX + i*step;
            FunctionPoint point = new FunctionPoint(x, values[i]);
            addNodeToEnd(point);
        }
    }

    // Количество точек
    public int getPointsCount(){
        return size;
    }

    // Возврат копии точки
    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException{
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.data);
    }

    // Возвращает абсциссу по индесу
    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException{
        FunctionNode node = getNodeByIndex(index);
        return node.data.getX();
    }

    // Возвращает ординату по индексу
    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException{
        FunctionNode node = getNodeByIndex(index);
        return node.data.getY();
    }

    // Устанавливает абсциссу
    public void setPointX(int index, double x) throws InappropriateFunctionPointException{
        if (index < 0 || index >= size){
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами");
        }

        FunctionNode current = getNodeByIndex(index);

        if (size > 1){
            double xPrev = current.prev.data.getX();
            double xNext = current.next.data.getX();

            if (x <= xPrev || x >= xNext){
                throw new InappropriateFunctionPointException("Новая абсцисса нарушает упорядоченность");
            }
        }

        current.data = new FunctionPoint(x, current.data.getY());
    }

    // Устанавливает ординату
    public void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException{
        FunctionNode node = getNodeByIndex(index);
        node.data = new FunctionPoint(node.data.getX(), y);
    }

    // Проверяет что абсцисса между соседними точками
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);

        // Проверяет порядок абсцисс
        if (index > 0) {
            double xPrev = node.prev.data.getX();

            if (point.getX() <= xPrev) {
                throw new InappropriateFunctionPointException("Абсцисса должна быть больше предыдущей");
            }
        }

        if (index < size - 1) {
            double xNext = node.next.data.getX();
            if (point.getX() >= xNext) {
                throw new InappropriateFunctionPointException("Абсцисса должна быть меньше следующей");
            }
        }

        // Заменяем точку в узле
        node.data = new FunctionPoint(point);
    }

    // Удаляет точку
    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException, IllegalStateException{
        if (size <= 2){
            throw new IllegalStateException("Нельзя удалить точку, должно быть минимум 2");
        }

        FunctionNode node = getNodeByIndex(index);

        node.prev.next  = node.next;
        node.next.prev = node.prev;
        size--;

        if (lastAccessedNode == node){
            lastAccessedNode = head;
            lastAccessedIndex = -1;
        }
    }

    // Проверяет что нет точки с такой же абсциссой
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException{
        // Проверяем нет ли точки с таким же X
       double newX = point.getX();
        FunctionNode current = head.next;

        while (current != head) {
            if (Math.abs(current.data.getX() - point.getX()) < 1e-9) {
                throw new InappropriateFunctionPointException("Точка с абсциссой уже существует");
            }
            current = current.next;
        }

        // Ищет  куда вставить
        FunctionNode insertAfter = head;  // начинаем от головы
        current = head.next; // Ничинает с реального узла

        while (current != head && current.data.getX() < newX) {
            insertAfter = current; // Узел до меств вставки
            current = current.next;
        }

        // Создаем новый узел
        FunctionNode nextNode = current;
        FunctionNode newNode = new FunctionNode(point, null, null);

        // Вставляем после insertAfter
        newNode.prev = insertAfter;
        newNode.next = insertAfter.next;
        insertAfter.next.prev = newNode;
        insertAfter.next = newNode;

        size++;
    }

    // Возврат первой абсциссы
    public double leftBound(){
        if (size ==0) return Double.NaN;
        return head.next.data.getX();
    }

    // Возврат последний абсциссы
    public double rightBound(){
        if (size == 0) return Double.NaN;
        return head.prev.data.getX();
    }

    // Линейная интерполяция
    public double getFunctionValue(double x) {
        // Если x вне области определения
        if (x < leftBound() || x > rightBound()) {
            return Double.NaN;
        }

        // Ищет между какими точками находится x
        FunctionNode current = head.next;
        while (current.next != head) {

            double x1 = current.data.getX();
            double x2 = current.next.data.getX();

            // Проверка на совпадение с х1
            if (Math.abs(x - x1) < 1e-9) {
                return current.data.getY();
            }

            // Проверка на свопадение с х2
            if (Math.abs(x - x2) < 1e-9) {
                return current.next.data.getY();
            }

            // Если x находится строго между x1 и x2
            if (x > x1 && x < x2) {
                // Найден интервал - линейная интерполяция
                double y1 = current.data.getY();
                double y2 = current.next.data.getY();

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }

            current = current.next;
        }


        // Для надежности проверяет последнюю точку, если цикл завершился:
        if (Math.abs(x - rightBound()) < 1e-9) {
            return head.prev.data.getY();
        }


        return Double.NaN; // не должно случиться, если x в границах
    }

}