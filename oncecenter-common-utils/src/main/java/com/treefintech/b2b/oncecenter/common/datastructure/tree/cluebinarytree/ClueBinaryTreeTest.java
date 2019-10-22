package com.treefintech.b2b.oncecenter.common.datastructure.tree.cluebinarytree;

/**
 * @author zhangfan
 * @description
 * @date 2019/10/12 下午4:44
 **/
public class ClueBinaryTreeTest {

    public static void main(String[] args) {
        //创建二叉树
        ClueBinaryTree clueBinaryTree = buildBinaryTree(new Integer[]{2, 8, 7, 4, 9, 3, 1, 6, 5});
        traversalAndSetDefault(clueBinaryTree);

    }

    private static ClueBinaryNode preNode;

    /**
     * 创建线索二叉树
     *
     * 二叉树的空链域指向  前驱/后继  节点
     *
     * @param clueBinaryTree   二叉树
     */
    private static void traversalAndSetDefault(ClueBinaryTree clueBinaryTree) {
        ClueBinaryNode baseNode = clueBinaryTree.getNode();
        if (baseNode == null) {
            return;
        }
        preNode = baseNode;
        traversalAndSetDefault(baseNode);
    }



    private static void traversalAndSetDefault(ClueBinaryNode node) {
        if (node == null) {
            return;
        }
        traversalAndSetDefault(node.getLeftChild());
        if (node.getLeftChild() == null) {
            node.setLeftFlag(Boolean.TRUE);
            node.setLeftChild(preNode);
        }

        if (preNode != null && node.getRightChild() == null) {
            node.setRightFlag(Boolean.TRUE);
            node.setRightChild(node);
        }
        preNode = node;
        traversalAndSetDefault(node.getRightChild());
    }


    /**
     * 创建二叉树
     *
     * @param dataArray   数组
     * @return
     */
    public static ClueBinaryTree buildBinaryTree(Integer[] dataArray) {
        ClueBinaryTree clueBinaryTree = new ClueBinaryTree();
        for (Integer data : dataArray) {
            insertIntoBinaryTree(data, clueBinaryTree);
        }
        return clueBinaryTree;
    }

    /**
     * 插入数据
     * 已根节点数据为 基点，小于则在 左子树，大于则在右子树。
     *
     * @param data
     * @param clueBinaryTree
     */
    @SuppressWarnings("all")
    public static void insertIntoBinaryTree(Integer data, ClueBinaryTree clueBinaryTree) {
        ClueBinaryNode node = new ClueBinaryNode(data);

        if (clueBinaryTree.getNode() == null) {
            clueBinaryTree.setNode(node);
        } else {
            ClueBinaryNode currentNode = clueBinaryTree.getNode();
            ClueBinaryNode parent;
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
