package com.treefintech.b2b.oncecenter.common.utils;

import com.alibaba.fastjson.JSONObject;
import com.treefinance.b2b.common.utils.file.CSVUtils;
import com.treefinance.tool.test.common.vo.GongFuRepayInstMntDetailNotify;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class Test {

    public static void main(String[] args) {

//        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://hljzxxd.loan.test.hljzxxd.com/gateway/openapi/consume/file/uploadOrderSmallFile");
//        String result = "";
//        try {
//            byte[] fileArrayByte = fileConvertToByteArray(new File("/Users/zhang/Downloads/项目资料/消费贷/掌众接入消费贷/掌众还款方式默认字段确认.xlsx"));
//
//            MultipartFile uploadFile = new MockMultipartFile("掌众还款方式默认字段确认.xlsx", fileArrayByte);
//            File file = multipartFileToFile(uploadFile, "掌众还款方式默认字段确认.xlsx");
//            System.out.println("file = " + file);
//            FileBody fileBody = new FileBody(file);
//
//            MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
//            reqEntity.addPart("file", fileBody);
//            reqEntity.addPart("attachmentCode", new StringBody("idcardFile", ContentType.MULTIPART_FORM_DATA));
////            reqEntity.addPart("businessType", new StringBody("Order", ContentType.MULTIPART_FORM_DATA));
//            HttpEntity httpEntity = reqEntity.build();
//            httpPost.setEntity(httpEntity);
//            HttpResponse response = httpClient.execute(httpPost);
//            HttpEntity responseEntity = response.getEntity();
//            if (responseEntity != null) {
//                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
//                System.out.println(result);
//            }
//        } catch (Exception e) {
//            throw new ServiceException(-1, "" + e);
//        } finally {
//            httpPost.releaseConnection();
//        }

//        List<People> peopleList = new ArrayList<>();
//        peopleList.add(new People("name", "1"));
//        peopleList.add(new People("nameBbbb", "2"));
//
//        System.out.println(JSONObject.toJSONString(peopleList));
//
//
        /**
         * test
         */

        try {
            byte[] fileByteArray = fileConvertToByteArray(new File("/Users/zhang/Downloads/BJZZJF190523001_repay_instmnt_detail_50.txt"));
            List<GongFuRepayInstMntDetailNotify> recordList = CSVUtils.read(new ByteArrayInputStream(fileByteArray), GongFuRepayInstMntDetailNotify.class);
            recordList.forEach(o -> System.out.println(JSONObject.toJSONString(o)));

        } catch (Exception e) {
            System.out.println("error is " + e);
        }



    }

    private static byte[] fileConvertToByteArray(File file) {
        byte[] data = null;

        try {
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            int len;
            byte[] buffer = new byte[1024];
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }

            data = baos.toByteArray();

            fis.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }


    /**
     * MultipartFile 转 File
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file, String originalFileName) throws Exception {

        File toFile = null;
        if(file.equals("")||file.getSize()<=0){
            file = null;
        }else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(originalFileName);
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }


    public static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}

class People {
    private String name;
    private String age;

    People(String name, String age) {
        this.name = name;
        this.age = age;
    }

    public String getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }
}

