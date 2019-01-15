package exception;

/**
 * @Author： song.zh
 * @Date: 2019/1/10
 */
public interface ExceptionInterfaceB {

    /**
     * method-A
     * @throws InterruptedException
     */
    void methodA() throws InterruptedException;

    /**
     * method-B
     * @throws ExceptionChildB
     */
    void methodB() throws ExceptionChildB;

    /**
     * method-C
     * @throws ExceptionParent
     */
    void methodC() throws ExceptionParent;
}
