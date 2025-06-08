import com.alibaba.fastjson.JSON;
import org.idea.eaglemq.broker.model.ConsumeQueueDetailModel;
import org.idea.eaglemq.broker.utils.MMapUtil;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;


/**
 * @Author idea
 * @Date: Created in 20:18 2024/3/24
 * @Description
 */
public class TestMMapUtil {

    private MMapUtil mmapUtil;
    private static final String filePath = "/Users/linhao/IdeaProjects-new/eaglemq/broker/commitlog/order_cancel_topic/00000000";

    private static final String consumeQueuePath = "/Users/linhao/IdeaProjects-new/eaglemq/broker/consumequeue/order_cancel_topic/0/00000000";


    @Before
    public void setUp() throws IOException {
        mmapUtil = new MMapUtil();
        mmapUtil.loadFileInMMap(consumeQueuePath,0,6 * 1024 * 1024);
        System.out.println("文件映射内存成功：0.1m");
    }

    @Test
    public void testLoadFile() throws IOException {
//        mmapUtil.loadFileInMMap(filePath,0,100 * 1024 * 1024);
    }

    @Test
    public void testWriteAndReadFile() {
//        String str = "this is a test content";
//        byte[] content = str.getBytes();
//        mmapUtil.writeContent(content);
        //consumeQueue
        int offset = 0;
        for(int i =0;i<200;i++) {
            byte[] readContent = mmapUtil.readContent(offset,12);
            offset +=12;
            ConsumeQueueDetailModel consumeQueueDetailModel = new ConsumeQueueDetailModel();
            consumeQueueDetailModel.buildFromBytes(readContent);
            System.out.println(JSON.toJSONString(consumeQueueDetailModel));
        }
    }

}
