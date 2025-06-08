import org.junit.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @Author idea
 * @Date: Created in 22:25 2024/5/1
 * @Description
 */
public class TestByteBufferMain {

    private ByteBuffer byteBuffer;

    @Test
    public void initByteBuffer() throws InterruptedException {
        byteBuffer = ByteBuffer.allocate(102040);
        ByteBuffer readBuffer = byteBuffer.slice();

        for (int i = 100; i < 1000; i++) {
            byteBuffer.put(("this is content " + i).getBytes());
        }
        new Thread(() -> {
            ByteBuffer tempByteBuffer = readBuffer.slice();
            for (int i = 100; i < 1000; i++) {
                printBuffer("task-1", tempByteBuffer);
            }
            System.out.println("task-1测试结束");
        }).start();
        new Thread(() -> {
            ByteBuffer tempByteBuffer = readBuffer.slice();
            for (int i = 100; i < 1000; i++) {
                printBuffer("task-2",tempByteBuffer);
            }
            System.out.println("task-2测试结束");
        }).start();
        Thread.sleep(10000);
    }

    private void printBuffer(String taskName, ByteBuffer byteBuffer) {
        byte[] tempByte = new byte[19];
        byteBuffer.get(tempByte);
        System.out.println(taskName + "-" + new String(tempByte));
    }
}
