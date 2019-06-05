package exception;

/**
 * @desc: description
 * @author: zhangsong
 * @date: 2019/6/5
 */
public class ExceptionReturn {

    public static void main(String[] args) {
        System.out.println(getReturn());
    }

    //如果同时return,  优先级顺序 finally > catch > try
    static String getReturn() {
        try {
            return "try";
        } catch (RuntimeException e) {
            return "catch";
        } finally {
            return "finally";
        }
    }
}
