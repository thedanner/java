package ads.treestuff;

import java.util.Comparator;

/**
 * A class for holding an ordered sequence of unique elements
 * @author  J. Ten Eyck
 * @date    July 4, 2006 -- a day to be celebrated with parades and fireworks
 */

public class BinarySearchTree<AnyType> {
	protected TreeNode<AnyType> root;
	private Comparator<AnyType> cmpr;
	
	/**
	 * Constructor
	 * @param cmp  a  generic Comparator
	 */
	public BinarySearchTree(Comparator<AnyType> cmp) {
		root = null;
		cmpr = cmp;
	}
	
	/**
	 * Copy constructor -- provides a deep copy
	 * @param  tree the source BinarySearchTree
	 * @param  cmp  a generic Comparator
	 */
	public BinarySearchTree(BinarySearchTree<AnyType>  tree, Comparator<AnyType> cmp) {
		cmpr = cmp;
		root = new TreeNode<AnyType>(tree.root, cmpr);
		//calls TreeNode copy constructor that copies entire tree
	}
	
	/**
	 * add a new element of the sequence to the tree
	 * precondition:   object to be added not already present
	 * post-condition:   obj is added inorder to the tree
	 * exception  InvalidArgumentException is thrown to client code if obj present
	 * @param  obj  a generic sequence element to be stored
	 * @throws  InvalidArgumentException if obj already present in TreeNode
	 */
	public void add(AnyType obj) throws IllegalArgumentException{
		if (root == null)
			root = new TreeNode<AnyType>( obj, cmpr );
		else
			root.add(obj);
	}
	
	/**
	 * returns true if obj is in the tree
	 * precondition:     none
	 * post-condition:   tree unchanged, true returned if key is present
	 * @param  obj  a generic object
	 * @return  boolean if obj is present in the tree
	 */
	public boolean contains(AnyType obj) {
		if (root == null) return false;
		return root.contains(obj);
	}
	
	/**
	 * removes the object passed as a parameter from the tree
	 * precondition:     tree is not empty and object is present in tree
	 * post-condition:   obj is removed from tree, tree is inorder
	 * exception:        exit after error message if obj not found
	 * @param  obj  a generic object to be removed
	 */
	public void remove(AnyType obj) {
		try {
			TreeNode<AnyType> hold = root.delete(obj);
			root = hold;
		}catch (ObjectNotFoundException e) {
			System.out.println(e);
			System.exit(1);
		}
	}
	
	/**
	 * returns true if the tree is empty
	 * @return  boolean true if tree is empty
	 */
	public boolean isEmpty( ) {
		return root == null;
	}
	
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
	 * returns a generic TreeIterator
	 * @return  a generic TreeIterator
	 */
	public TreeIterator<AnyType> iterator( ) {
		TreeIterator<AnyType> itr = new TreeIterator<AnyType>(root);
		return itr;
	}
	
	/**
	 * returns an inorder listing of sequence elements separated by spaces
	 * uses toString( ) method of the generic type
	 * @return  a String -- the inorder listing of sequence elements
	 */
	public String toString( ) {
		String str = "";
		TreeIterator<AnyType> itr = iterator( );
		while (itr.hasNext( ) ) {
			str += itr.next( ).toString( );
		}
		return str;
	}
}
