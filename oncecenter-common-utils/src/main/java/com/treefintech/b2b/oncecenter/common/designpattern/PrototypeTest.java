package com.treefintech.b2b.oncecenter.common.designpattern;


import com.treefinance.b2b.common.exceptions.ServiceException;
import com.treefinance.b2b.common.utils.lang.StringUtils;

/**
 * 原型模式  ---  创建型模式
 * 功能：创建相同的对象
 *
 * 结论：clone得到的对象与原对象比较，值相同，不是指针传递。但是该对象内的Attachment类是指针传递。
 */

public class PrototypeTest {

    public static void main(String[] args) {
        User user = new User();
        user.setUsername("测试名称");

        Attachment attachment = new Attachment();
        attachment.setAttachmentId("attachmentId");
        attachment.setFileByteArray(new byte[]{1, 2});
        user.setAttachment(attachment);
        User user1 = (User)user.clone();

        System.out.println(user == user1);
        System.out.println(StringUtils.equals(user.getUsername(),user1.getUsername()));
        System.out.println(user.getAttachment() == user1.getAttachment());
        System.out.println(StringUtils.equals(user.getAttachment().getAttachmentId(), user1.getAttachment().getAttachmentId()));
        System.out.println(user.getAttachment().getFileByteArray() == user1.getAttachment().getFileByteArray());
    }
}


class User implements Cloneable {
    private String username;
    private Attachment attachment;

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ServiceException(-1, "" + e);
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public String getUsername() {
        return username;
    }

    public Attachment getAttachment() {
        return attachment;
    }
}

class Attachment implements Cloneable {
    private String attachmentId;
    private byte[] fileByteArray;

    public void setAttachmentId(String attachmentId) {
        this.attachmentId = attachmentId;
    }

    public void setFileByteArray(byte[] fileByteArray) {
        this.fileByteArray = fileByteArray;
    }

    public String getAttachmentId() {
        return attachmentId;
    }

    public byte[] getFileByteArray() {
        return fileByteArray;
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new ServiceException(-1, "" + e);
        }
    }
}






