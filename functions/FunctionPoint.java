package functions;

public class FunctionPoint {
    private double x; // Координата x 
    private double y; // Координата y
    
    // Конструктор создания точки по заданным координатам
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    // Конструктор копирования
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x; // Копирование координат из точки 
        this.y = point.y; // 
    }
    
    // Конструктор по умолчанию 
    public FunctionPoint() {
        this(0.0, 0.0);
    }
    
    //Геттер для x
    public double getX(){
	return x;
    }
  
    // Геттер для y
    public double getY(){
	return y;
    }

}
