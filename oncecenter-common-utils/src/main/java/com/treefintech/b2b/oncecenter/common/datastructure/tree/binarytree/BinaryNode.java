package com.treefintech.b2b.oncecenter.common.datastructure.tree.binarytree;

/**
 * 二叉树节点---数据结构
 *
 * @author zhangfan
 * @description
 * @date 2019/10/10 上午10:27
 **/
public class BinaryNode {

    /**
     * 节点存储的数据
     */
    private Integer data;

    /**
     * 左子树
     */
    private BinaryNode leftChild;

    /**
     * 右子树
     */
    private BinaryNode rightChild;

    public BinaryNode() {

    }

    BinaryNode(Integer data) {
        this.data = data;
        this.leftChild = null;
        this.rightChild = null;
    }

    public Integer getData() {
        return data;
    }

    public void setData(Integer data) {
        this.data = data;
    }

    public BinaryNode getLeftChild() {
        return leftChild;
    }

    public void setLeftChild(BinaryNode leftChild) {
        this.leftChild = leftChild;
    }

    public BinaryNode getRightChild() {
        return rightChild;
    }

    public void setRightChild(BinaryNode rightChild) {
        this.rightChild = rightChild;
    }
}
