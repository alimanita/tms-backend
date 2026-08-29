import org.bytedeco.opencv.global.opencv_core;
import java.lang.reflect.Method;
public class Test {
    public static void main(String[] args) {
        for(Method m : opencv_core.class.getMethods()) {
            if(m.getName().equals("divide")) {
                System.out.println(m);
            }
        }
    }
}
