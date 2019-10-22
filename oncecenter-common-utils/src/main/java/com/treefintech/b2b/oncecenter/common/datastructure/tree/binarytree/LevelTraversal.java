package com.treefintech.b2b.oncecenter.common.datastructure.tree.binarytree;

import java.util.LinkedList;

/**
 * 二叉树---遍历法
 * 前序遍历，中序遍历，后序遍历，层序遍历
 * <p>
 * 功能：实现二叉树的遍历法
 *
 * @author zhangfan
 * @description
 * @date 2019/10/10 上午10:25
 **/
public class LevelTraversal {

    public static void main(String[] args) {

        BinaryTree binaryTree = buildBinaryTree(new Integer[]{2, 8, 7, 4, 9, 3, 1, 6, 5});
        System.out.println("层序遍历");
        levelTraversalProcess(binaryTree);
        System.out.println();


        traversalProcess(binaryTree);
        System.out.println();
    }

    /**
     * 遍历
     *
     * @param binaryTree
     */
    public static void traversalProcess(BinaryTree binaryTree) {
        BinaryNode baseNode = binaryTree.getNode();
        if (baseNode == null) {
            return;
        }
        System.out.println("前序遍历");
        preTraversal(baseNode);
        System.out.println();
        System.out.println("中序遍历");
        middleTraversal(baseNode);
        System.out.println();
        System.out.println("后序遍历");
        postTraversal(baseNode);
    }

    /**
     * 前序遍历，先根节点，然后左子树，最后右子树
     *
     * @param node
     */
    public static void preTraversal(BinaryNode node) {
        if (node == null) {
            return;
        }
        System.out.print(node.getData() + "     ");

        preTraversal(node.getLeftChild());
        preTraversal(node.getRightChild());
    }

    /**
     * 中序遍历，左子树---根节点---右子树
     *
     * @param node
     */
    public static void middleTraversal(BinaryNode node) {
        if (node == null) {
            return;
        }
        preTraversal(node.getLeftChild());
        System.out.print(node.getData() + "     ");

        preTraversal(node.getRightChild());
    }

    /**
     * 后序遍历，左子树---右子树---根节点
     *
     * @param node
     */
    public static void postTraversal(BinaryNode node) {
        if (node == null) {
            return;
        }
        preTraversal(node.getLeftChild());
        preTraversal(node.getRightChild());
        System.out.print(node.getData() + "     ");
    }

    /**
     * 层序遍历
     *
     * @param binaryTree
     */
    public static void levelTraversalProcess(BinaryTree binaryTree) {
        BinaryNode baseNode = binaryTree.getNode();
        if (baseNode == null) {
            return;
        }

        LinkedList<BinaryNode> list = new LinkedList<>();
        list.add(baseNode);
        while (!list.isEmpty()) {
            BinaryNode node = list.poll();
            System.out.print(node.getData() + "     ");
            if (node.getLeftChild() != null) {
                list.add(node.getLeftChild());
            }
            if (node.getRightChild() != null) {
                list.add(node.getRightChild());
            }
        }
    }

    /**
     * 创建二叉树
     *
     * @param dataArray
     * @return
     */
    public static BinaryTree buildBinaryTree(Integer[] dataArray) {
        BinaryTree binaryTree = new BinaryTree();
        for (Integer data : dataArray) {
            insertIntoBinaryTree(data, binaryTree);
        }
        return binaryTree;
    }

    /**
     * 插入数据
     * 已根节点数据为 基点，小于则在 左子树，大于则在右子树。
     *
     * @param data
     * @param binaryTree
     */
    @SuppressWarnings("all")
    public static void insertIntoBinaryTree(Integer data, BinaryTree binaryTree) {
        BinaryNode node = new BinaryNode(data);

        if (binaryTree.getNode() == null) {
            binaryTree.setNode(node);
        } else {
            BinaryNode currentNode = binaryTree.getNode();
            BinaryNode parent;
            while (true) {
                parent = currentNode;
                if (data < currentNode.getData()) {
                    currentNode = parent.getLeftChild();
                    if (currentNode == null) {
                        parent.setLeftChild(node);
                        return;
                    }
                } else {
                    currentNode = parent.getRightChild();
                    if (currentNode == null) {
                        parent.setRightChild(node);
                        return;
                    }
                }
            }
        }
    }
}
