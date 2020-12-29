package ads.treestuff;
/**
 * A class for holding generic sequence elements in a tree
 * @author  J. Ten Eyck
 * @date    July 5, 2006
 */

import java.util.Comparator;

class TreeNode<AnyType> {
	protected TreeNode<AnyType> left, right;
	protected AnyType data;
	private Comparator<AnyType> cmpr;
	
	/**
	 * Constructor
	 * @param    obj   a generic sequence element
	 * @param    cmp   a generic Comparator
	 */
	public TreeNode(AnyType obj, Comparator<AnyType> cmp) {
		data = obj;
		left = null;
		right = null;
		cmpr = cmp;
	}
	
	/**
	 * Copy Constructor  creates a deep copy of the subtree rooted at tn
	 * @param  tn  TreeNode object is the root of the subtree to be copied
	 * @param  cmp  a generic Comparator for comparing sequence elements
	 */
	public TreeNode(TreeNode<AnyType> tn, Comparator<AnyType> cmp) {
		data = tn.data( );  // a shallow copy
		cmpr = cmp;
		if (tn.left( )!= null )
			left = new TreeNode<AnyType>(tn.left( ), cmpr);
		else
			left = null;
		if (tn.right( )!= null )
			right = new TreeNode<AnyType>(tn.right( ), cmpr);
		else
			right = null;
	}
	
	/**
	 * returns handle to left subtree
	 * @return  handle to left subtree
	 */
	public TreeNode<AnyType> left( ) {
		return left;
	}
	
	/**
	 * returns handle to right subtree
	 * @return  handle to right subtree
	 */
	public TreeNode<AnyType> right( ) {
		return right;
	}
	
	/**
	 * returns a reference to the generic data element
	 * @return  handle to data member
	 */
	public AnyType data( ) {
		return data;
	}
	
	/**
	 * Adds a new generic sequence element to the subtree rooted at THIS node
	 * precondition:    obj is not in the tree
	 * post-condition:  obj is added inorder to the tree, node holding obj is leaf
	 * exception:       no action taken, error is thrown to client
	 * @param  obj  the element to be added
	 * @throws  InvalidArgumentException if obj already present in TreeNode
	 */
	public void add(AnyType obj) throws IllegalArgumentException {
		if (cmpr.compare(obj,data) < 0) {
			if (left( )!= null )
				left.add(obj);
			else
				//put it here
				left = new TreeNode<AnyType>( obj, cmpr );
		}
		else if (cmpr.compare(obj,data) > 0)  {
			if (right( )!= null )
				right.add(obj);
			else
				//put it here
				right = new TreeNode<AnyType>( obj, cmpr );
		}
		else { //duplicate key
			throw new IllegalArgumentException("error in add -- object already in tree");
		}
	}
	
	/**
	 * removes named object from the tree
	 * precondition:     obj is contained in the tree
	 * post-condition:   obj is removed from the tree and replaced in position
	 *                   by the left-most descendent of right child (if exists)
	 *                   or by left child
	 * exception:        throws ObjectNotFoundException if obj not present
	 * @param  obj  generic sequence element to be removed from tree
	 * @return  a handle to a TreeNode to be reattached in recursive call
	 * @throws  ObjectNotFoundException  if obj is not present in tree
	 */
	public TreeNode<AnyType> delete(AnyType obj) throws ObjectNotFoundException {
		if (this == null)
			throw new ObjectNotFoundException("error delete --- tree is empty\n\n");
		if ((cmpr.compare(obj,data) < 0) && left( )!= null){
			left = left.delete(obj);
			return this;
		}        
		if (cmpr.compare(obj,data) > 0 && right( )!= null){
			right = right.delete(obj);
			return this;
		}
		if (cmpr.compare(obj,data) == 0)
			return deleteRoot( );
		throw new ObjectNotFoundException("error delete --- object not present\n\n");
	}
	
	/**
	 * returns the least elelment of the sequence stored in the tree
	 * precondition:     tree is not empty
	 * post-condition:   minimum data member is returned, tree unchanged
	 * exception:        throws TreeUnderflowException
	 * @return  the least element in the sequence stored in tree
	 * @throws  TreeUnderflowException  if tree is empty
	 */
	public AnyType min( ) throws TreeUnderflowException {
		if (this == null)
			throw new TreeUnderflowException("error min -- tree is empty\n\n");
		if (left( )== null )
			return this.data( );
		else
			return left.min( );
	}
	
	/**
	 * returns the greatest elelment of the sequence stored in the tree
	 * precondition:     tree is not empty
	 * post-condition:   maximum data member is returned, tree unchanged
	 * exception:        throws TreeUnderflowException
	 * @return  the greatest element in the sequence stored in tree
	 * @throws  TreeUnderflowException  if tree is empty
	 */  
	public AnyType max( ) throws TreeUnderflowException {
		if (this == null)
			throw new TreeUnderflowException("error max -- tree is empty\n\n");
		if (right( )== null )
			return this.data( );
		else
			return right.max( );
	}
	
	/**
	 * removes the minimum element in the sequence from the tree
	 * precondition:    none
	 * post-condition:  node containing the minimum key is deleted
	 *                  and replaced with its right child
	 * @return  reference to the TreeNode to be reattached during recusive call
	 */
	public TreeNode<AnyType> deleteMin( ) {
		if (this == null)  return this;
		if (left( )!= null ) {
			left = left( ).deleteMin( );
			return this;
		}
		return right( );
	}
	
	/**
	 * removes the maximum element in the sequence from the tree
	 * precondition:    none
	 * post-condition:  node containing the maximum key is deleted
	 *                  and replaced with its left child
	 * @return  reference to the TreeNode to be reattached during recusive call
	 */
	public TreeNode<AnyType> deleteMax( ) {
		if (this == null)  return this;
		if (right( )!= null) {
			right = right( ).deleteMax( );
			return this;
		}
		return left( );
	}
	
	/**
	 * removes the root element in the tree from the sequence
	 * precondition:    none
	 * post-condition:  this node is removed and replaced by its
	 *                  left-most descendant of the right child or by
	 *                  left child.  Left-most descendant is recursively deleted.
	 * @return  reference to the TreeNode to be reattached during recusive call
	 */
	public TreeNode<AnyType> deleteRoot( ) {
		if (this == null)
			return this;
		if (right( )!= null ) {
			try{
				data = right( ).min( );
				right = right( ).deleteMin( );
				return this;
			}
			catch (TreeUnderflowException e) {
				System.out.println(e);
				System.exit(1);
			}
		}
		return left( );
	}
	
	/**
	 * returns true if the generic object passed is stored in the tree
	 * precondition:     none
	 * post-condition:   true is returned if obj is present in the tree
	 *                   else false is returned.  Tree is unchanged
	 * @param  obj  generic element of the type stored in the tree
	 */
	public boolean contains(AnyType obj) {
		if (this == null)
			return false;
		if (cmpr.compare(obj,data) < 0 && left( )!= null )
			return left.contains(obj);
		if (cmpr.compare(obj,data) > 0 && right( )!= null )
			return right.contains(obj);
		return cmpr.compare(obj,data) == 0;
	}
	
	/**
	 * returns the number of nodes in the subtrees rooted at this node inclusive
	 * preconditions:    none
	 * post-conditions:  tree is unchanged
	 * @return  int  number of nodes in subtree
	 */
	public int size () {
		int count = 1;
		if (left( ) != null)
			count += left( ).size();
		if (right( ) != null)
			count += right( ).size();
		return count;
	}
	
	/**
	 * returns the data element stored in the TreeNode as a String
	 * uses toString( ) method of the generic type
	 * @return  a String
	 */
	public String toString( ) {
		String str = data.toString( );
		return str + "  ";
	}
}
