package functions;

public class ArrayTabulatedFunction implements TabulatedFunction {
    private FunctionPoint[] points;
    private int pointsCount;

    // Возврат первой абсциссы
    public double leftBound() {
        if (pointsCount == 0) return Double.NaN;
        return points[0].getX();
    }

    // Возврат последней абсциссы
    public double rightBound() {
        if (pointsCount == 0) return Double.NaN;
        return points[pointsCount - 1].getX();
    }

    // Конструктор: с заданными x и y
    public ArrayTabulatedFunction(double[] xValues, double[] yValues) {
        if (xValues.length != yValues.length || xValues.length < 2) {
            throw new IllegalArgumentException("Массивы x и y должны иметь одинаковую длину (минимум 2).");
        }

        // Проверка упорядоченности ординат
        for (int i = 0; i < xValues.length - 1; i++) {
            if (xValues[i] >= xValues[i + 1]) {
                throw new IllegalArgumentException("Абсциссы должны быть строго упорядочены по возрастанию.");
            }
        }

        this.pointsCount = xValues.length;
        // Инициализация массива с запасом
        this.points = new FunctionPoint[pointsCount + 10];

        // Копирование точек
        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(xValues[i], yValues[i]);
        }
    }

    // Конструктор(равномерный шаг, y = 0)
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные параметры");
        }
        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + i * step, 0);
        }
    }

    // Конструктор: с заданными y
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX || values.length < 2) {
            throw new IllegalArgumentException();
        }
        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];
        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            points[i] = new FunctionPoint(leftX + i * step, values[i]);
        }
    }

    // Пустой - для addPoint
    public ArrayTabulatedFunction() {
        points = new FunctionPoint[10];
        pointsCount = 0;
    }

    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    // Линейная интерполяция
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = points[i].getX();
            double x2 = points[i + 1].getX();

	    if (Math.abs(x - x1) < 1e-10) {
		return points[i].getY();
	    }

	    if (Math.abs(x - x2) < 1e-10){
		return points[i + 1].getY();
	    }


            if (x >= x1 && x <= x2) {
                double y1 = points[i].getY();
                double y2 = points[i + 1].getY();

		if (Math.abs(x1 - x2) < 1e-10){
		    return (y1 + y2)/2;
		}

                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    public int getPointsCount() {
        return pointsCount;
    }

    public FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return new FunctionPoint(points[index]);
    }

    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if (index > 0 && point.getX() <= points[index - 1].getX()){
            throw new InappropriateFunctionPointException("Абсцисса должна быть больше предыдущей");
        }
        if (index < pointsCount - 1 && point.getX() >= points[index + 1].getX()){
            throw new InappropriateFunctionPointException("Абсцисса должна быть меньше следующей");
        }
        points[index] = new FunctionPoint(point);
    }

    public double getPointX(int index) throws FunctionPointIndexOutOfBoundsException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getX();
    }

    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне границ");
        }

        if (index > 0){
            double xPrev = points[index - 1].getX();
            if (x < xPrev || Math.abs(x - xPrev) < 1e-9) {
                throw new InappropriateFunctionPointException("Абсцисса должна быть больше предыдущей");
            }
        }

        if (index < pointsCount - 1){
            double xNext = points[index + 1].getX();
            if (x > xNext || Math.abs(x - xNext) < 1e-9) {
                throw new InappropriateFunctionPointException("Абсцисса должна быть меньше следующей");
            }
        }

        points[index] = new FunctionPoint(x, points[index].getY());
    }

    public double getPointY(int index) throws FunctionPointIndexOutOfBoundsException{
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException();
        }
        return points[index].getY();
    }

    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами");
        }

        points[index] = new FunctionPoint(points[index].getX(), y);
    }

    public void deletePoint(int index) throws FunctionPointIndexOutOfBoundsException,IllegalStateException{
        if (index < 0 || index >= pointsCount){
            throw new FunctionPointIndexOutOfBoundsException("Индекс вне границ");
        }

        if (pointsCount < 3){
            throw new IllegalStateException("Нельзя удалить точку, должно быть минимум 2");
        }
        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
        points[pointsCount] = null;
    }

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        double x = point.getX();

        // Проверка исключения- совпадение абсцисс
        // Ищет, есть ли уже точка с такой же абсциссой
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - x) < 1e-9) {
                // Если найдена точка с совпадающей абсциссой, выбрасывает исключение
                throw new InappropriateFunctionPointException("Невозможно добавить точку: абсцисса x = " + x + " уже существует");
            }
        }

        // Поиск места - определяет индекс для вставки, чтобы сохранить массив упорядоченным по x
        int insertionIndex = 0;
        while (insertionIndex < pointsCount && points[insertionIndex].getX() < x) {
            insertionIndex++;
        }

        // Проверка размера массива
        if (pointsCount == points.length) {
            FunctionPoint[] newPoints = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newPoints, 0, points.length);
            points = newPoints;
        }

        // Сдвиг элементов - освобождает место для новой точки
        // Сдвигает все элементы, начиная с insertionIndex, на одну позицию вправо
        for (int i = pointsCount; i > insertionIndex; i--) {
            points[i] = points[i - 1];
        }

        // Вставка
        points[insertionIndex] = point;
        pointsCount++;
    }
}

