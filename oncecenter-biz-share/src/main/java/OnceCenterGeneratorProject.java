
import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;

/**
 * <p> 工程生成 </p>
 * Created by shiyc on 2018/7/18.
 */
public class DemoCenterGeneratorProject {

    public static void main(String[] args) throws IOException {
        /**
         *
         * todo: 1、从gig下载treefintech-b2b-democenter工程到本地。
         * todo: 2、修改demoserverPath为你刚下载的模板工程地址
         */
        String path = "/Users/zhang/IdeaProjects/treefinance-b2b-base/treefintech-b2b-democenter";
        // 需要变更的工程名称
        String srcName = "democenter";
        // 目标工程名称

        /**
         * todo:3、确定你的工程名称。
         */
        String dstName = "oncecenter";

        File file = new File(path);
        /**
         * todo:4、修改destFile路径  为你需要保存新工程的地址。
         */
        File destFile = new File("/Users/zhang/StudySpace/one-peace-oncecenter");
        FileUtils.copyDirectory(file, destFile);
        renameFile(destFile, dstName, srcName);
        renameFile(destFile, "OnceCenter", "DemoCenter");
        renameFile(destFile, "once", "demo");


        /**
         * todo:5、运行该main函数。运行完毕后，目标目录下会生成你需要的工程。用IDEA打开新的工程。
         * todo:6、打开之后，在工程上右键点击，执行Replace in Path(开启Match case),将需要替换的字符设置为：democenter -> halong-ordercenter 的值
         * todo:7、如果$dstName的值中，带有-的。执行Replace in Path(开启Match case),将需要替换的字符设置为：.halong-ordercenter -> .halong.ordercenter
         * todo:8、执行Replace in Path(开启Match case),将需要替换的字符设置为：DemoCenter -> OrderCenter
         * todo:9、执行Replace in Path(开启Match case),将需要替换的字符设置为：demo -> order
         * todo:10、执行Replace in Path(开启Match case),将需要替换的字符设置为：halong-ordercenter_ -> halong_ordercenter_
         * todo:11、执行mvn clean install，打包成功即可
         */
    }


    public static void renameFile(File file, String dstName, String srcName) throws IOException {
        if (file.getName().endsWith(".git") || file.getName().endsWith(".idea")
                || file.getName().endsWith(".iml") || file.getName().endsWith(".DS_Store")
                || file.getName().equals("target")) {
            boolean flag = FileUtils.deleteQuietly(file);
            System.out.println("delete flag = " + flag + " for file " + file.getParent() + File.separator + file.getName());
            return;
        }
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            if (fileList == null || fileList.length <= 0) {
                return;
            }
            for (File file1 : fileList) {
                renameFile(file1, dstName, srcName);
            }
        }

        if (file.getName().contains(srcName)) {

            String repStr = dstName;
            if (file.getParent().indexOf("java") > 0) {
                repStr = StringUtils.replace(repStr, "-", File.separator);
            }
            String destFile = file.getParent() + File.separator + (StringUtils.replace(file.getName(), srcName, repStr));
            System.out.println(file.getParent() + File.separator + file.getName() + "---->" + destFile);

            if (file.isDirectory()) {
                FileUtils.moveDirectory(file, new File(destFile));
            } else {
                FileUtils.moveFile(file, new File(destFile));
            }
        }
    }
}
