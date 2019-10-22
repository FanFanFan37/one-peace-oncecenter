package com.treefintech.b2b.oncecenter.common.datastructure.tree.cluebinarytree;

import com.treefintech.b2b.oncecenter.common.datastructure.tree.binarytree.BinaryNode;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/12 下午4:40
 **/
public class ClueBinaryNode {

    /**
     * 节点存储的数据
     */
    private Integer data;

    /**
     * 左子树
     */
    private ClueBinaryNode leftChild;

    /**
     * 左标志位，0(flase) --> 左孩子节点， 1(true) ---> 前序节点
     */
    private Boolean leftFlag;

    /**
     * 右子树
     */
    private ClueBinaryNode rightChild;

    /**
     * 左标志位，0(flase) --> 右孩子节点， 1(true) ---> 后继结点
     */
    private Boolean rightFlag;


    public ClueBinaryNode() {
    }

    public ClueBinaryNode(Integer data) {
        this.data = data;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public ClueBinaryNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(ClueBinaryNode leftChild) {
        this.leftChild = leftChild;
    }

    public Boolean getLeftFlag() {
        return leftFlag;
    }

    public void setLeftFlag(Boolean leftFlag) {
        this.leftFlag = leftFlag;
    }

    public ClueBinaryNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(ClueBinaryNode rightChild) {
        this.rightChild = rightChild;
    }

    public Boolean getRightFlag() {
        return rightFlag;
    }

    public void setRightFlag(Boolean rightFlag) {
        this.rightFlag = rightFlag;
    }
}
