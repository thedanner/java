package ads.treestuff;
/**
 * A class for holding generic sequence elements in an AVL tree
 * @author  J. Ten Eyck
 * @date    July 5, 2006
 */
import java.util.Comparator;

class AVLNode<AnyType> {
	
	// private data  -- accessible to member functions
	private AnyType data;
	private int  bf;
	private AVLNode<AnyType>  leftptr;
	private AVLNode<AnyType>  rightptr;
	private Comparator<AnyType> cmp;
	
	/**
	 *  private methods
	 * AVLNode<Comparable> RotateLeft       ();
	 * AVLNode<Comparable> RotateRight      ();
	 * AVLNode<Comparable> restoreLeftBalance    (int);
	 * AVLNode<Comparable> restoreRightBalance    (int);
	 * AVLNode<Comparable> balance        ();
	 * AVLNode<Comparable> removeLeftmostDescendant (AVLNode<Comparable> avlnode )
	 */
	
	/**
	 * Constructor
	 * @param    obj   a generic sequence element
	 * @param    cmp   a generic Comparator
	 */
	public AVLNode(AnyType  obj, Comparator<AnyType> cmp) {
		data = obj;
		leftptr = null;
		rightptr = null;
		bf = 0;
		this.cmp = cmp;
	}
	
	/**
	 * Copy Constructor  creates a deep copy of the subtree rooted at avlnode
	 * @param  avlnode  AVLNode object is the root of the subtree to be copied
	 * @param  cmp  a generic Comparator for comparing sequence elements
	 */
	public AVLNode(AVLNode<AnyType> avlnode, Comparator<AnyType> cmp) {
		data = avlnode.data( );  // a shallow copy
		this.cmp = cmp;
		bf = 0;
		if (avlnode.left( )!= null )
			leftptr = new AVLNode<AnyType>(avlnode.left( ), cmp);
		else
			leftptr = null;
		if (avlnode.right( )!= null )
			rightptr = new AVLNode<AnyType>(avlnode.right( ), cmp);
		else
			rightptr = null;
	}
	
	/**
	 * Constructor with supplied left and right subtrees
	 * @param    obj   a generic sequence element
	 * @param    left  a handle to the left subtree
	 * @param    right  a handle to the right subtree
	 * @param    cmp   a generic Comparator
	 */
	public AVLNode(AnyType obj, AVLNode<AnyType> left,
			AVLNode<AnyType> right, Comparator<AnyType> cmp) {
		data = obj;
		this.cmp = cmp;
		bf = 0;
		leftptr = left;
		rightptr = right;
	}
	
	/**
	 * perform a single left rotation about the current node
	 * @return     a reference to the node to be reattached
	 */
	private AVLNode<AnyType> RotateLeft () {
		AVLNode<AnyType> nodeA = this;
		AVLNode<AnyType> nodeB = nodeA.right ();
		// reconnect
		nodeA.right(nodeB.left ());
		nodeB.left(nodeA);
		// update the balance factors
		int Abf = nodeA.bf;
		int Bbf = nodeB.bf;
		if (Bbf <= 0) {
			if (Abf >= 1)
				nodeB.bf = (Bbf - 1);
			else
				nodeB.bf = (Abf + Bbf - 2);
			nodeA.bf = (Abf - 1);
		}
		else {
			if (Abf <= Bbf)
				nodeB.bf = (Abf - 2);
			else
				nodeB.bf = (Bbf - 1);
			nodeA.bf = ((Abf - Bbf) - 1);
		}
		return nodeB;
	}
	
	/**
	 * perform a single right rotation about the current node
	 * @return     a reference to the node to be reattached
	 */
	private AVLNode<AnyType> RotateRight () {
		AVLNode<AnyType> nodeA = this;
		AVLNode<AnyType> nodeB = nodeA.left ();
		// reconnect
		nodeA.left(nodeB.right ());
		nodeB.right(nodeA);
		// update the balance factors
		int Abf = nodeA.bf;
		int Bbf = nodeB.bf;
		if (Bbf <= 0) {
			if (Bbf > Abf)
				nodeB.bf = (Bbf + 1);
			else
				nodeB.bf = (Abf + 2);
			nodeA.bf = (1 + Abf - Bbf);
		}
		else {
			if (Abf <= -1)
				nodeB.bf = (Bbf + 1);
			else
				nodeB.bf = (Abf + Bbf + 2);
			nodeA.bf = (Abf + 1);
		}
		return nodeB;
	}
	
	/**
	 * rebalances the tree after a rotation
	 * @param  oldbf  the previous balance factor of the node
	 * @return  reference to the root of the subtree
	 */
	private AVLNode<AnyType> restoreLeftBalance(int oldbf) {
		AVLNode<AnyType> leftchild = left ();
		if (leftchild == null)
			bf++;
		else if ((leftchild.bf  != oldbf) && (leftchild.bf == 0 ))
			bf++;
		if (bf > 1)
			return balance ();
		return this;
	}
	
	/**
	 * rebalances the tree after a rotation
	 * @param  oldbf  the previous balance factor of the node
	 * @return  reference to the root of the subtree
	 */
	private AVLNode<AnyType> restoreRightBalance(int oldbf) {
		AVLNode<AnyType> rightchild = right ();
		if (rightchild == null)
			bf--;
		else if ((rightchild.bf  != oldbf) && (rightchild.bf == 0 ))
			bf--;
		if (bf < -1)
			return balance ();
		return this;
	}
	
	/**
	 * used to restore the balance of the AVL tree after an add or delete
	 * works with the two rotations and the two restoreBalance methods
	 * @return reference to the new root of the subtree
	 */
	private AVLNode<AnyType> balance () {
		// balance tree rooted at node using single or double rotations
		if (bf < 0) {
			if (left ().bf <= 0)
				return RotateRight ();
			else  { // perform double rotation
				left (left ().RotateLeft ());
				return RotateRight ();
			}
		}
		else {
			if (right ().bf >= 0)
				return RotateLeft ();
			else  { // perform double rotation
				right (right ().RotateRight ());
				return RotateLeft ();
			}
		}
	}
	
	/**
	 * Adds a new generic sequence element to the subtree rooted at THIS node
	 * precondition:    value is not in the tree
	 * post-condition:  value is added inorder to the tree, node holding obj is leaf
	 * exception:       no action taken, error is thrown to client
	 * @param  value  the element to be added
	 * @throws  InvalidArgumentException if value already present in AVLNode
	 */  
	public AVLNode<AnyType> add (AnyType value) throws IllegalArgumentException {
		if (cmp.compare(value, data) < 0) {
			if (left () != null) {
				int oldbf = left ().bf;
				left(left ().add(value));
				// check to see if tree grew
				if ((left ().bf != oldbf) && (left ().bf != 0))
					bf--;
			}
			else {
				left(new AVLNode<AnyType> (value, cmp));
				bf--;
			}
		}
		else if (cmp.compare(value, data) > 0) {
			if (right () != null) {
				int oldbf = right ().bf;
				right(right ().add(value));
				// check to see if tree grew
				if ((right ().bf != oldbf) && (right ().bf != 0))
					bf++;
			}
			else {
				right(new AVLNode<AnyType> (value, cmp));
				bf++;
			}
		}
		else if (cmp.compare(value, data) == 0)
			throw new IllegalArgumentException("error add -- value already present\n\n");
		// check to make sure the tree is not out of balance
		if ((bf < -1) || (bf > 1))
			return balance ();
		return this;
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
	public AVLNode<AnyType> delete (AnyType value) throws ObjectNotFoundException {
		if (this == null)
			throw new ObjectNotFoundException("error delete --- tree is empty\n\n");
		if (cmp.compare(value, data) == 0) { // we're the one
			if (right () == null)
				return left ();
			// else find and remove the leftmost descendant of the right child
			int oldbf = right ().bf;
			AVLNode<AnyType> newroot = new AVLNode<AnyType> (this.data, cmp);
			right(right().removeLeftmostDescendant(newroot) );
			// connect the new root
			newroot.left(left() );
			newroot.right(right() );
			newroot.bf = bf;
			return  newroot.restoreRightBalance(oldbf);
		}
		else if (cmp.compare(value, data) < 0 && leftptr != null) {//remove from left child
			// do the deletion
			int oldbf = left ().bf;
			left(left().delete(value));
			return restoreLeftBalance (oldbf);
		}
		else if (cmp.compare(value, data) > 0 && rightptr != null) {//remove from right child
			// do deletion
			int oldbf = right ().bf;
			right(right().delete(value));
			return restoreRightBalance (oldbf);
		}
		throw new ObjectNotFoundException("error delete --- object not present\n\n");
	}
	
	/**
	 * removes the leftmost descendant of the right child during a delete
	 * @param  reference to the deleted node (to be returned)
	 * @return  reference to the node replacing the deleted leftmost child
	 */    
	private AVLNode<AnyType> removeLeftmostDescendant(AVLNode<AnyType> childptr) {
		// see if we are the leftmost node
		AVLNode<AnyType> leftchild = left();
		if (leftchild == null)  { // we are
			childptr.data = this.data;
			return right(); // remove self
		}
		// else do the deletion
		int oldbf = leftchild.bf;
		left(leftchild.removeLeftmostDescendant (childptr) );
		return restoreLeftBalance (oldbf);
	}
	
	/**
	 * returns a reference to the generic data element
	 * @return  handle to data member
	 */
	public AnyType data( ) {
		return data;
	}
	
	/**
	 * attaches tnode as root of left subtree
	 * @param  tnode  node to attach as left subtree
	 */
	public void left(AVLNode<AnyType> tnode) {
		leftptr = tnode;
	}
	
	/**
	 * returns handle to left subtree
	 * @return  handle to left subtree
	 */
	public AVLNode<AnyType> left () {
		return leftptr;
	}
	
	/**
	 * attaches tnode as root of right subtree
	 * @param  tnode  node to attach as right subtree
	 */
	public void right(AVLNode<AnyType> tnode) {
		rightptr = tnode;
	}
	
	/**
	 * returns handle to right subtree
	 * @return  handle to right subtree
	 */
	public AVLNode<AnyType> right  () {
		return rightptr;
	}
	
	/**
	 * returns true if the generic object passed is stored in the tree
	 * precondition:     none
	 * post-condition:   true is returned if item is present in the tree
	 *                   else false is returned.  Tree is unchanged
	 * @param  item  generic element of the type stored in the tree
	 */
	public boolean contains (AnyType item) {
		boolean found = false;
		if (cmp.compare(data, item) == 0)
			found = true;
		else if (cmp.compare(item,data) < 0 && (left () != null))
			found = left().contains (item);
		else if (right () != null)
			found = right().contains (item);
		return found;
	}
	
	/**
	 * returns the number of nodes in the subtrees rooted at this node inclusive
	 * preconditions:    none
	 * post-conditions:  tree is unchanged
	 * @return  int  number of nodes in subtree
	 */
	public int size () {
		int count = 1;
		if (leftptr != null)
			count += leftptr.size();
		if (rightptr != null)
			count += rightptr.size();
		return count;
	}
	
	/**
	 * makes a deep copy of the (sub)tree rooted at root
	 * @param  root of the tree to be copied
	 * @return  reference to the root of the copied tree
	 */
	public AVLNode<AnyType> copy(AVLNode<AnyType> root) {
		AVLNode<AnyType> newRoot;
		if (root != null) {
			newRoot = new AVLNode<AnyType> (root.data, root.cmp);
			newRoot.bf = root.bf;
			newRoot.leftptr = root.copy (root.left());
			newRoot.rightptr = root.copy (root.right());
		}
		else
			newRoot = null;
		return newRoot;
	}
	
	/**
	 * makes null all of the references in a deleted tree
	 * (not really used with automatic garbage collection)
	 */
	public void release () {
		if (leftptr != null) {
			leftptr.release ();
			leftptr = null;
		}
		if (rightptr != null) {
			rightptr.release ();
			rightptr = null;
		}
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
			return leftptr.min( );
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
			return rightptr.max( );
	}
	
	/**
	 * returns the data element stored in the AVLNode as a String
	 * uses toString( ) method of the generic type
	 * @return  a String
	 */
	public String toString( ) {
		String str = data.toString( );
		return str + "  ";
	}
}
