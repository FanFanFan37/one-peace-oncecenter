

import com.treefinance.b2b.common.generator.mybatis.MyMapperCodeGenerator;

import java.io.IOException;

/**
 * <p> Mapper文件生成工具 </p>
 * Created by zhuangmh on 2015/6/30.
 */
public class DemoCenterMyBatisCodeGenerator {

    public static void main(String[] args) throws IOException {
        String path = System.class.getResource("/MybatisConfig.properties").getPath();
        System.out.println(path);
        String[] paths = {path};
        MyMapperCodeGenerator.main(paths);
    }

}
