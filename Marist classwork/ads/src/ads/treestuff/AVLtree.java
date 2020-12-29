package ads.treestuff;

import java.util.Comparator;

/**
 * A class for holding generic sequence elements in a height-balanced AVL tree
 * @author  J. Ten Eyck
 * @date    July 10, 2006
 */

public class AVLtree<AnyType>  {
	private AVLNode<AnyType>  root ;
	private Comparator<AnyType> cmp;
	
	
	/**
	 * Constructor
	 * @param cmp  a  generic Comparator
	 */
	public AVLtree (Comparator<AnyType> cmp) {
		root = null;
		this.cmp = cmp;
	}
	
	/**
	 * Copy constructor -- provides a deep copy
	 * @param  avl the source AVLTree
	 * @param  cmp  a generic Comparator
	 */
	public AVLtree (AVLtree<AnyType> avl)  {
		root.copy(avl.root);
		this.cmp = avl.cmp;
	}
	
	/**
	 * add a new element of the sequence to the tree
	 * precondition:   object to be added not already present
	 * post-condition:   obj is added inorder to the tree and tree is height balanced
	 * exception  InvalidArgumentException is thrown to client code if obj present
	 * @param  item  a generic sequence element to be stored
	 * @throws  InvalidArgumentException if obj already present in AVLNode
	 */
	public void add(AnyType  item) throws IllegalArgumentException {
		if (root == null)
			root = new AVLNode<AnyType> (item, cmp);
		else
			root = root.add (item);
	}
	
	/**
	 * removes the item passed as a parameter from the tree
	 * precondition:     tree is not empty and object is present in tree
	 * post-condition:   item is removed from tree, tree is inorder and balanced
	 * exception:        exit after error message if item not found
	 * @param  item  a generic object to be removed
	 */
	public void remove (AnyType  item) {
		try {
			if (root != null)
				root = root.delete (item);
		}
		catch (ObjectNotFoundException e) {
			System.out.print(e);
			System.exit(1);
		}
	}
	
	/**
	 * returns true if obj is in the tree
	 * precondition:     none
	 * post-condition:   tree unchanged, true returned if key is present
	 * @param  obj  a generic object
	 * @return  boolean if obj is present in the tree
	 */
	public boolean contains (AnyType obj) {
		if (root == null) return false;
		return root.contains( obj );
	}
	
	/**
	 * returns true if the tree is empty
	 * @return  boolean true if tree is empty
	 */
	public boolean isEmpty() {return root == null; }
	
	/**
	 * precondition:    none
	 * post-conditions: tree is unchanged
	 * returns int  the number of distinct sequence elements stored in tree
	 */
	public int size () {return root.size(); }
	
	/**
	 * returns the least element in the sequence
	 * precondition:    tree is not empty
	 * post-condition:  least element returned, tree unchanged
	 * exception:       error message generated and thread terminates
	 * @return  AnyType  least element
	 */
	public AnyType min( ) {
		try {
			return root.min( );
		}
		catch (TreeUnderflowException e) {
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
	
	
	/**
	 * returns the greatest element in the sequence
	 * precondition:    tree is not empty
	 * post-condition:  greatest element returned, tree unchanged
	 * exception:       error message generated and thread terminates
	 * @return  AnyType  greatest element
	 */
	public AnyType max( ) {
		try {
			return root.max( );
		}
		catch (TreeUnderflowException e) {
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * To be used in the breadth-first search
	 * @return reference to an AVLNode
	 */
	public AVLNode<AnyType> root( ) {
		return root;
	}
	
	/**
	 * returns a generic AVLTreeIterator
	 * @return  a generic AVLTreeIterator
	 */
	public AVLTreeIterator<AnyType> iterator( ) {
		AVLTreeIterator<AnyType> itr = new AVLTreeIterator<AnyType>(root);
		return itr;
	}
	
	/**
	 * returns an inorder listing of sequence elements separated by spaces
	 * uses toString( ) method of the generic type
	 * @return  a String -- the inorder listing of sequence elements
	 */
	public String toString( ) {
		String str = "";
		AVLTreeIterator<AnyType> itr = iterator( );
		while (itr.hasNext( ) ) {
			str += itr.next( ).toString( );
		}
		return str;
	}
}
