package functions;

public interface TabulatedFunction {
    int getPointsCount();
    double getPointX(int index);
    double getPointY(int index);
    void setPointX(int index, double x) throws InappropriateFunctionPointException;  // ← ДОБАВЬ throws!
    void setPointY(int index, double y) throws InappropriateFunctionPointException;
    void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException;  // ← ДОБАВЬ throws!
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;  // ← ДОБАВЬ throws!
    void deletePoint(int index);
    double leftBound();
    double rightBound();
    double getFunctionValue(double x);
}