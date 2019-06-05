package exception;

/**
 * @Author： song.zh
 * @Date: 2019/1/10
 */
public class ExceptionImpl implements ExceptionInterfaceAB {

    /**
     * 1， 这里throws的异常不能比父接口的异常高
     * 2， 父接口里同时有个声明式异常，实现类里最多只能取其交集
     * 3， 这里没有交集，所以只能不抛
     */
    @Override
    public void methodA() {
        System.out.println("我没有跑出任何声明异常");
    }

    /**
     * 这里也没有交集，也不能抛
     */
    @Override
    public void methodB() {

    }

    /**
     * 这里有一个交集：ExceptionChildA，所以可以抛出此交集
     * 当然也可以不抛任何异常
     * @throws ExceptionChildA
     */
    @Override
    public void methodC() throws ExceptionChildA {
        throw new ExceptionChildA();
    }

    public static int getInt() {
        int a = 1;
        try {
            a = Integer.valueOf("2l");
            return a;
        } catch (NumberFormatException e) {
            a = 3;
            return a;
        } finally {
            a = 4;
        }
    }

    public static void main(String[] args) {
        System.out.println(getInt());
    }

}
