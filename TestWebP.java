import org.bytedeco.opencv.opencv_core.*;
import org.bytedeco.javacpp.*;
import static org.bytedeco.opencv.global.opencv_core.*;
import static org.bytedeco.opencv.global.opencv_imgcodecs.*;
import java.io.File;

public class TestWebP {
    public static void main(String[] args) {
        Mat test = new Mat(100, 100, CV_8UC3);
        IntPointer p = new IntPointer(new int[]{ IMWRITE_WEBP_QUALITY, 75 });
        boolean ok = imwrite("test.webp", test, p);
        System.out.println("WebP writing supported: " + ok);
    }
}
