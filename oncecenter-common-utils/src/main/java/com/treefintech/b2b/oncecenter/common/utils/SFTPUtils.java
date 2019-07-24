package com.treefintech.b2b.oncecenter.common.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.treefinance.b2b.common.exceptions.ServiceException;
import com.treefinance.b2b.common.utils.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * sftp工具类
 */
public class SFTPUtils {

    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://api.loan.test.hljzxxd.com/gateway/openapi/consume/file/uploadSmallFile");
        String result = "";
        try {
            FileBody bin = new FileBody(new File("/Users/zhang/Downloads/项目资料/消费贷/唯网接入消费贷/新传入的风控字段.xlsx"));
            MultipartEntityBuilder reqEntity = MultipartEntityBuilder.create();
            reqEntity.addPart("file", bin);
            reqEntity.addPart("attachmentCode", new StringBody("idcardFile", ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("businessType", new StringBody("Order", ContentType.MULTIPART_FORM_DATA));
            HttpEntity httpEntity = reqEntity.build();
            httpPost.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                result = EntityUtils.toString(responseEntity, Charset.forName("UTF-8"));
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpPost.releaseConnection();
        }
    }


    private transient Logger log = LoggerFactory.getLogger(this.getClass());

    private ChannelSftp sftp;

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String host;
    /**
     * SFTP 端口
     */
    private int port;

    private boolean login;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SFTPUtils(String username, String password, String host, int port) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.login = false;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SFTPUtils(String username, String host, int port, String privateKey) {
        this.username = username;
        this.host = host;
        this.port = port;
        this.privateKey = privateKey;
        this.login = false;
    }

    public SFTPUtils() {
        this.login = false;
    }


    /**
     * 连接sftp服务器
     */
    private void login() {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                // 设置私钥
                jsch.addIdentity(privateKey);
            }

            session = jsch.getSession(username, host, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            session.setConfig(config);
            session.connect();

            Channel channel = session.openChannel("sftp");
            channel.connect();

            sftp = (ChannelSftp) channel;
            this.login = false;
        } catch (JSchException e) {
            throw new ServiceException(-1, "连接失败", e);
        }
    }

    /**
     * 关闭连接 server
     */
    private void logout() {
        this.login = false;
        if (sftp != null) {
            if (sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

    /**
     * 将输入流的数据上传到sftp作为文件。文件完整路径=basePath+directory
     *
     * @param basePath     服务器的基础路径
     * @param directory    上传到该目录
     * @param sftpFileName sftp端文件名
     * @param input        输入流
     */
    public void upload(String basePath, String directory, String sftpFileName, InputStream input) {
        try {
            cd(basePath);
            cd(directory, false);
            log.error("try uploadn file " + sftpFileName + " to directory " + sftp.pwd());
            //上传文件
            sftp.put(input, sftpFileName);
        } catch (Exception e) {
            throw new ServiceException(-1, "上传失败", e);
        } finally {
            logout();
        }
    }

    public void cd(String directory) {
        cd(directory, true);
    }

    public void cd(String directory, boolean needLogin) {
        if (needLogin) {
            login();
        }
        if (StringUtils.isEmpty(directory)) {
            return;
        }
        try {
            sftp.cd(directory);
        } catch (SftpException e) {
            String[] dirs = directory.split("/");
            for (String dir : dirs) {
                if (null == dir || "".equals(dir)) {
                    continue;
                }
                String tempPath = "";
                try {
                    tempPath = sftp.pwd() + "/" + dir;
                    sftp.cd(dir);
                } catch (SftpException ex) {
                    try {
                        sftp.mkdir(dir);
                        sftp.cd(dir);
                    } catch (Exception ex1) {
                        throw new ServiceException(-1, "cd to " + tempPath + " error for path:" + directory, ex1);
                    }
                }
            }
        }
    }


}

