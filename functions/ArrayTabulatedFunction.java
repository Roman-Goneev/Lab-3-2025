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

    // Конструктор(равномерный шаг, y = 0)
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX || pointsCount < 2) {
            throw new IllegalArgumentException("Некорректные параметр");
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

	    if (Math.abs(x - x1)<1e-10) {
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
            throw new FunctionPointIndexOutOfBoundsException();
        }
        if (index > 0 && x <= points[index - 1].getX()){
            throw new InappropriateFunctionPointException("Абсцисса должна быть больше предыдущей");
        };
        if (index < pointsCount - 1 && x >= points[index + 1].getX()){
            throw new InappropriateFunctionPointException("Абсцисса должна быть меньше следующей");
        };
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

    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException{
        if (pointsCount == points.length) {
            FunctionPoint[] newArray = new FunctionPoint[points.length * 2];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            points = newArray;
        }

        int insertIndex = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            if (points[i].getX() > point.getX()) {
                insertIndex = i;
                break;
            }
        }

        if (insertIndex < pointsCount) {
            System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        }

        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }
}

