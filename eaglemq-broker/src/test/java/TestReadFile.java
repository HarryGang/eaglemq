import java.io.*;

public class TestReadFile {

    public static void main(String[] args) throws IOException, InterruptedException {
        String filePath = "/data/java_demo/test.txt";
        FileInputStream fileInputStream = new FileInputStream(new File(filePath));
        byte[] content = new byte[1024];
        fileInputStream.read(content);
        System.out.println(new String(content));
        Thread.sleep(10000);
    }
}
