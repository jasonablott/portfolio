package assignment06;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;

public class BinarySearchTree<T extends Comparable<? super T>> implements SortedSet<T>{


    // Member variables


    BinaryTreeNode<T> root;
    int size;


    // default constructor

    /**
     * Default constructor for BinarySearchTree
     * @return a new binary search tree
     */
    public BinarySearchTree() {
        root = null;
        size = 0;
    }


    // Nested node class


    public class BinaryTreeNode<T> {
        T data;
        BinaryTreeNode<T> left;
        BinaryTreeNode<T> right;

        /**
         * Default constructor for BinarySearchTree
         * @return a new binary tree node
         */
        public BinaryTreeNode(T data) {
            this.data = data;
            left = null;
            right = null;
        }
    }


    // Other methods


    /**
     * Ensures that this set contains the specified item.
     *
     * @param item - the item whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * the input item was actually inserted); otherwise, returns false
     * @throws NullPointerException if the item is null
     */
    @Override
    public boolean add(T item) {
        // check that item is valid to insert first
        if (item == null) {
            throw new NullPointerException();
        }
        // handle empty tree by making new item the root
        if (root == null) {
            root = new BinaryTreeNode<>(item);
            size++;
            return true;
        }
        // utilize recursive helper insert to insert the item.
        return insert(root, item);
    }

    /**
     * Inserts an item into a binary tree
     *
     * @param item - the item to be added to the tree
     * @return true if this set changed as a result of this method call (that is, if
     * the input item was actually inserted); otherwise, returns false
     */
    private boolean insert(BinaryTreeNode<T> node, T item) {
        // if the item is already in the list, return false as we can't insert a duplicate
        if (item.compareTo(node.data) == 0) {
            return false;
        }
        if (item.compareTo(node.data) < 0) {
            if (node.left == null) {
                node.left = new BinaryTreeNode<>(item);
                size++;
                return true;
            } else {
                return insert(node.left, item);
            }
        } else if (item.compareTo(node.data) > 0) {
            if (node.right == null) {
                node.right = new BinaryTreeNode<>(item);
                size++;
                return true;
            } else {
                return insert(node.right, item);
            }
        }
        return false;
    }

    /**
     * Ensures that this set contains all items in the specified collection.
     *
     * @param items - the collection of items whose presence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * any item in the input collection was actually inserted); otherwise,
     * returns false
     * @throws NullPointerException if any of the items is null
     */
    @Override
    public boolean addAll(Collection<? extends T> items) {
        // track if any item was inserted
        boolean changed = false;
        // iterate through the collection
        for (T item : items) {
            //  insert each into the tree. If item is inserted change tracker to true
            if (add(item)) {
                changed = true;
            }
        }
        // return whether or not any item was inserted
        return changed;
    }

    /**
     * Removes all items from this set. The set will be empty after this method
     * call.
     */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Determines if there is an item in this set that is equal to the specified
     * item.
     *
     * @param item - the item sought in this set
     * @return true if there is an item in this set that is equal to the input item;
     * otherwise, returns false
     * @throws NullPointerException if the item is null
     */
    @Override
    public boolean contains(T item) {
        // make sure item is valid to search for
        if (item == null) {
            throw new NullPointerException();
        }
        // start at the root of the tree
        BinaryTreeNode<T> node = root;
        // if the root is null, return false as the tree is empty
        if (node == null) {
            return false;
        }
        // check root first
        if (node.data.equals(item)){
            return true;
        }
        // traverse the nodes of the tree to find target item as deep as possible
        while (!(node.left == null && node.right == null)) {
            // if we find the item, return true
            if (node.data.compareTo(item) == 0) {
                return true;
            }
            // if we don't find the item, either go left or right depending on the comparison of item to node.data
            // if item is less than node.data, go left
            if (item.compareTo(node.data) < 0) {
                if (node.left == null) {
                    return false;
                }
                node = node.left;
            // otherwise item is greater than node.data, so go right
            } else {
                if (node.right == null) {
                    return false;
                }
                node = node.right;
            }
        }
        // if we make it all the way through to a node with null left and right (leaf) item is not in the tree
        return node.data.compareTo(item) == 0;
    }

    /**
     * Determines if for each item in the specified collection, there is an item in
     * this set that is equal to it.
     *
     * @param items - the collection of items sought in this set
     * @return true if for each item in the specified collection, there is an item
     * in this set that is equal to it; otherwise, returns false
     * @throws NullPointerException if any of the items is null
     */
    @Override
    public boolean containsAll(Collection<? extends T> items) {
        for (T item : items) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the first (i.e., smallest) item in this set.
     *
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public T first() throws NoSuchElementException {
        // check that tree is not empty first
        if (size == 0) {
            throw new NoSuchElementException("Empty tree");
        }
        // start traversing at the root
        BinaryTreeNode<T> node = root;
        // while there is a left node, continue traversing deeper to find lower values
        // the first node that does not have a left branch is the minimum value
        while (node.left != null) {
            node = node.left;
        }
        // return the data of the "first" or minimum node
        return node.data;
    }

    /**
     * Returns true if this set contains no items.
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns the last (i.e., largest) item in this set.
     *
     * @throws NoSuchElementException if the set is empty
     */
    @Override
    public T last() throws NoSuchElementException {
        if (size == 0) {
            throw new NoSuchElementException("Empty tree");
        }
        // start traversing at the root
        BinaryTreeNode<T> node = root;
        // while there is a right node, continue traversing deeper to find higher/larger values
        // the first node that does not have a right branch is the maximum value
        while (node.right != null) {
            node = node.right;
        }
        // return the data of the "last" or maximum node
        return node.data;
    }

    /**
     * Ensures that this set does not contain the specified item.
     *
     * @param item - the item whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * the input item was actually removed); otherwise, returns false
     * @throws NullPointerException if the item is null
     */
    @Override
    public boolean remove(T item) {
        // grab size to track if any nodes are deleted
        int originalSize = size;
        // use recursive deleter to remove the node that item belongs to, start from root
        BinaryTreeNode<T> newRoot = nodeDeleter(root, item);
        // if size has changed, an element has been removed
        if (size != originalSize) {
            // reset root
            root = newRoot;
            return true;
        }
        // if node has not been removed return false
        return false;
    }

    /**
     * Deletes a node while ensuring BST properties are maintained
     *
     * @throws NullPointerException if the item is null
     */
    private BinaryTreeNode<T> nodeDeleter(BinaryTreeNode<T> node, T item) {
        // handle null node (it's not in the tree)
        if (node == null) {
            return null;
        }
        // continue traversing left if necessary based on comparison of item to node data
        if (item.compareTo(node.data) < 0) {
            node.left = nodeDeleter(node.left, item);
        }
        // or continue traversing right if necessary based on comparison of item to node data
        else if (item.compareTo(node.data) > 0) {
            node.right = nodeDeleter(node.right, item);
        }
        // otherwise we have found the node to remove (item = node.data)
        else {
            // if node has no children, return null to remove it as no other nodes are affected
            if (node.left == null && node.right == null) {
                size--;
                return null;
                // if node has one right child, set parent's pointer to node to node's right child
            } else if (node.left == null) {
                size--;
                return node.right;
                // if node has one left child, set parent's pointer to node to node's left child
            } else if (node.right == null) {
                size--;
                return node.left;
                // if node has two children, find successor, replace, and delete successor
            } else {
                // find successor
                BinaryTreeNode<T> successorNode = node.right;
                while (successorNode.left != null) {
                    successorNode = successorNode.left;
                }
                // once located, replace "deleted" node data with successor data
                node.data = successorNode.data;
                // now delete the successor in the node subtree
                node.right = nodeDeleter(node.right, successorNode.data);
            }
        }
        // if successor has been deleted the node can now be returned with the successors data in it
        return node;
    }

    /**
     * Ensures that this set does not contain any of the items in the specified
     * collection.
     *
     * @param items - the collection of items whose absence is ensured in this set
     * @return true if this set changed as a result of this method call (that is, if
     * any item in the input collection was actually removed); otherwise,
     * returns false
     * @throws NullPointerException if any of the items is null
     */
    @Override
    public boolean removeAll(Collection<? extends T> items) {
        // track if any item was removed
        boolean changed = false;
        // iterate through the collection
        for (T item : items) {
            //  insert each into the tree. If item is inserted change tracker to true
            if (remove(item)) {
                changed = true;
            }
        }
        // return whether or not any item was removed
        return changed;
    }

    /**
     * Returns the number of items in this set.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns an ArrayList containing all of the items in this set, in sorted
     * order.
     */
    @Override
    public ArrayList<T> toArrayList() {
        // make new arrayList to return
        ArrayList<T> listToReturn = new ArrayList<>();
        // check that we actually have data in the tree to traverse before calling recursive helper
        if (size == 0 || root == null) {
            return listToReturn;
        }
        // assuming there is data in the tree, pass the root and empty list to the helper to copy data
        inOrderTraverser(root, listToReturn);
        // once the recursive method is done the tree is fully traversed and all data is now copied into listToReturn
        return listToReturn;
    }

    /**
     * Traverses a binary search tree in order and adds items to an arraylist
     *
     * @param node - the root node of the binary search tree to traverse
     * @param list - the list to populate with the data from the tree
     *
     * void function populates the arrayList passed in using an in-order traversal of the tree
     */
    private void inOrderTraverser(BinaryTreeNode<T> node, ArrayList<T> list) {
        // base case, when there is no left or right node
        if (node == null) {
            return;
        }
        // recursively visit left node
        inOrderTraverser(node.left, list);
        // add that node's data to the list
        list.add(node.data);
        // recursively visit the right node
        inOrderTraverser(node.right, list);
    }
}
