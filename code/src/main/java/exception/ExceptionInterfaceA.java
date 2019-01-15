package exception;

/**
 * @Author： song.zh
 * @Date: 2019/1/10
 */
public interface ExceptionInterfaceA {

    /**
     * method-A
     * @throws CloneNotSupportedException
     */
    void methodA() throws CloneNotSupportedException;

    /**
     * method-B
     * @throws ExceptionChildA
     */
    void methodB() throws ExceptionChildA;

    /**
     * method-C
     * @throws ExceptionChildA
     */
    void methodC() throws ExceptionChildA;
}
