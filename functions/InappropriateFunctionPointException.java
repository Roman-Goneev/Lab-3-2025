package functions;

public class InappropriateFunctionPointException extends RuntimeException{
    public InappropriateFunctionPointException(){
        super();
    }

    public  InappropriateFunctionPointException(String message){
        super(message);
    }
}