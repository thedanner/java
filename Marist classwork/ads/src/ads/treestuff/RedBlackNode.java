package ads.treestuff;

import java.util.Comparator;

/**
 * A class for holding generic sequence elements in a balanced Red-black tree
 * @author  J. Ten Eyck
 * @date    July 12, 2006
 */
interface colors {int crimson = 0, red = 1, black = 2, absidian = 3;}

class RedBlackNode<AnyType> {
	// data area
	private int   shade;
	private boolean   leaf;
	private RedBlackNode<AnyType>  leftptr;
	private RedBlackNode<AnyType>  rightptr;
	private AnyType data;
	private Comparator<AnyType> cmp;
	
	/** private methods
	 * RedBlackNode<AnyType> RotateLeft      ();
	 * RedBlackNode<AnyType> RotateRight      ();
	 * RedBlackNode<AnyType> restoreLeftBalance   ();
	 * RedBlackNode<AnyType> restoreRightBalance   ();
	 * RedBlackNode<AnyType> balance       ();
	 * RedBlackNode<AnyType> removeLeftmostDescendant (RedBlackNode<AnyType>  );
	 */
	
	/**
	 * Constructor
	 * @param    value   a generic sequence element
	 * @param    cmp   a generic Comparator
	 */
	public RedBlackNode (AnyType  value, Comparator<AnyType> cmp) {
		data = value;
		leaf = false;
		shade = colors.red;
		this.cmp = cmp;
		leftptr = new RedBlackNode<AnyType> (colors.black);
		rightptr = new RedBlackNode<AnyType> (colors.black);
	}
	
	/**
	 * Constructor for an external node 
	 * automatically called when new node is added
	 * @param    shade set to black
	 */
	private RedBlackNode (int shade) {
		data = null;
		leaf = true;
		this.shade = shade;
		leftptr = null;
		rightptr = null;
	}
	
	/**
	 * changes the color of a node to black
	 */
	public void blacken( ) {
		shade = colors.black;
	}
	
	/**
	 * returns true if node referenced is a leaf (external) node
	 * @return  true if an external node
	 */
	public boolean isLeaf( ) {
		return leaf;
	}
	
	/**
	 * perform a single left rotation about the current node
	 * @return     a reference to the node to be reattached
	 */
	private RedBlackNode<AnyType> RotateLeft () {
		// perform single left rotation of the current node
		RedBlackNode<AnyType> nodeA = this;
		RedBlackNode<AnyType> nodeB = nodeA.right ();
		// reconnect
		nodeA.right(nodeB.left ());
		nodeB.left(nodeA);
		return nodeB;
	}
	
	/**
	 * perform a single right rotation about the current node
	 * @return     a reference to the node to be reattached
	 */
	private RedBlackNode<AnyType> RotateRight () {
		// perform single left rotation of the current node
		RedBlackNode<AnyType> nodeA = this;
		RedBlackNode<AnyType> nodeB = nodeA.left ();
		// reconnect
		nodeA.left(nodeB.right ());
		nodeB.right(nodeA);
		return nodeB;
	}
	
	/**
	 * rebalances the tree after a rotation
	 * @return  reference to the root of the subtree
	 */
	private RedBlackNode<AnyType> restoreLeftBalance() {
		if (left ().shade == colors.absidian) {
			if ((right ().shade == colors.red) && (shade == colors.black)) {// case 1
				RedBlackNode<AnyType> nodeB = right ();
				shade = colors.red;  // re-color ourself
				nodeB.shade = colors.black;
				RedBlackNode<AnyType> parent = RotateLeft ();
				parent.left (restoreLeftBalance ());
				return parent;
			}
			RedBlackNode<AnyType> rightchild = right ();
			RedBlackNode<AnyType> leftsib = rightchild.left ();
			RedBlackNode<AnyType> rightsib = rightchild.right ();
			if ((leftsib.shade == colors.black) && (rightsib.shade == colors.black)) {
				// case 2
				rightchild.shade = colors.red;
				left ().shade = colors.black;
				if (shade == colors.red)
					shade = colors.black;
				else
					shade = colors.absidian;  // promote the extra unit of black up the tree
				return this;
			}
			if ((leftsib.shade == colors.red) && (rightsib.shade == colors.black)) {
				// case 3
				leftsib.shade = colors.black;
				rightchild.shade = colors.red;
				rightsib = rightchild;
				right (rightchild.RotateRight ());
			}
			if (rightsib.shade == colors.red) { // case 4
				rightsib.shade = colors.black;
				right ().shade = shade;
				left ().shade = colors.black;
				shade = colors.black;
				return RotateLeft ();
			}
		}
		return this;
	}
	
	/**
	 * rebalances the tree after a rotation
	 * @return  reference to the root of the subtree
	 */
	private RedBlackNode<AnyType> restoreRightBalance() {
		if (right ().shade == colors.absidian) {
			if ((left ().shade == colors.red) && (shade == colors.black)) {// case 1
				RedBlackNode<AnyType> nodeB = left ();
				shade = colors.red;  // re-color ourself
				nodeB.shade = colors.black;
				RedBlackNode<AnyType> parent = RotateRight ();
				parent.right (restoreRightBalance ());
				return parent;
			}
			RedBlackNode<AnyType> leftchild = left ();
			RedBlackNode<AnyType> leftsib = leftchild.left ();
			RedBlackNode<AnyType> rightsib = leftchild.right ();
			if ((leftsib.shade == colors.black) && (rightsib.shade == colors.black)) {
				// case 2
				leftchild.shade = colors.red;
				right ().shade = colors.black;
				if (shade == colors.red)
					shade = colors.black;
				else
					shade = colors.absidian;  // promote the extra unit of black up the tree
				return this;
			}
			if ((rightsib.shade == colors.red) && (leftsib.shade == colors.black)) {
				// case 3
				rightsib.shade = colors.black;
				leftchild.shade = colors.red;
				leftsib = leftchild;
				left (leftchild.RotateLeft ());
			}
			if (leftsib.shade == colors.red) { // case 4
				leftsib.shade = colors.black;
				left ().shade = shade;
				right ().shade = colors.black;
				shade = colors.black;
				return RotateRight ();
			}
		}
		return this;
	}
	
	/**
	 * used to restore the balance of the red-black tree after an add or delete
	 * works with the two rotations and the two restoreBalance methods
	 * @return reference to the new root of the subtree
	 */
	private RedBlackNode<AnyType> balance () {
		// balance tree rooted at node using single or double rotations
		if (left ().shade == colors.crimson) {
			if (right ().shade == colors.red) { // case 1
				right ().shade = colors.black;
				left ().shade = colors.black;
				shade = colors.red;  // color myself red
				return this;
			}
			else { // case 2 or 3
				if (left ().left ().shade == colors.red) {
					left ().shade = colors.black;
					shade = colors.red;
					return RotateRight ();  // case 3
				}
				else  { // perform double rotation
					left ().shade = colors.red;  // no longer crimson
					left (left ().RotateLeft ()); // convert to case 3
					left ().shade = colors.black;
					shade = colors.red;
					return RotateRight ();
				}
			}
		}
		else {  // right child is crimson
			if (left ().shade == colors.red) { // case 1
				right ().shade = colors.black;
				left ().shade = colors.black;
				shade = colors.red;  // color myself red
				return this;
			}
			else { // case 2 or 3
				if (right ().right ().shade == colors.red) {
					right ().shade = colors.black;
					shade = colors.red;
					return RotateLeft ();  // case 3
				}
				else  { // perform double rotation
					right ().shade = colors.red;  // no longer crimson
					right (right ().RotateRight ()); // convert to case 3
					right ().shade = colors.black;
					shade = colors.red;
					return RotateLeft ();
				}
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
	public RedBlackNode<AnyType> add  (AnyType  value) throws IllegalArgumentException{
		RedBlackNode<AnyType> childptr = null;
		if (cmp.compare (value, data) < 0) {
			if (!left ().leaf) {
				left(left ().add(value));
				childptr = left ();
			}
			else {
				left(new RedBlackNode<AnyType> (value, cmp));
				childptr = left ();
			}
		}
		else if (cmp.compare(value,data) > 0) {
			if (!right ().leaf) {
				right(right ().add(value));
				childptr = right ();
			}
			else {
				right(new RedBlackNode<AnyType> (value, cmp));
				childptr = right ();
			}
		}
		else if (cmp.compare(value, data) == 0)
			throw new IllegalArgumentException("error add -- value already present\n\n");
		// check to make sure the tree is not out of balance
		if ((shade == colors.red) && (childptr.shade == colors.red)) {
			shade = colors.crimson;
			return this;
		}
		if (childptr.shade != colors.crimson)
			return this;
		return balance ();
	}
	
	/**
	 * removes named object from the tree
	 * precondition:     value is contained in the tree
	 * post-condition:   value is removed from the tree and replaced in position
	 *                   by the left-most descendent of right child (if exists)
	 *                   or by left child
	 * exception:        throws ObjectNotFoundException if obj not present
	 * @param  value  generic sequence element to be removed from tree
	 * @return  a handle to a TreeNode to be reattached in recursive call
	 * @throws  ObjectNotFoundException  if obj is not present in tree
	 */
	public RedBlackNode<AnyType> delete (AnyType value) throws ObjectNotFoundException{
		RedBlackNode<AnyType> temp;
		if (cmp.compare(value,data) == 0) { // we're the one
			if (right ().leaf) {
				temp = left ();
				if (shade == colors.black)
					if (temp.shade == colors.red)
						temp.shade = colors.black;
					else
						temp.shade = colors.absidian;
				return left ();
			}
			// else find and remove the leftmost descendant of the right child
			RedBlackNode<AnyType> newroot = new RedBlackNode<AnyType>(this.data, cmp);
			right(right().removeLeftmostDescendant(newroot) );
			// connect the new root
			newroot.left(left() );
			newroot.right(right() );
			newroot.shade = shade;
			return  newroot.restoreRightBalance();
		}
		else if (cmp.compare(value, data) < 0 && !left().leaf) {  // remove from left child
			if (left().leaf )
				return this;   // no left child
			// do the deletion
			left(left().delete(value));
			return restoreLeftBalance ();
		}
		else if (cmp.compare(value, data) > 0 && !right().leaf) { // remove from right child
			if (right().leaf )
				return this;  // no right child
			// do deletion
			right(right().delete(value));
			return restoreRightBalance ();
		}
		throw new ObjectNotFoundException("error delete --- object not present\n\n");
	}
	
	/**
	 * removes the leftmost descendant of the right child during a delete
	 * @param  reference to the deleted node (to be returned)
	 * @return  reference to the node replacing the deleted leftmost child
	 */    
	private RedBlackNode<AnyType> removeLeftmostDescendant
	(RedBlackNode<AnyType> childptr)  {
		// see if we are the leftmost node
		RedBlackNode<AnyType> leftchild = left();
		if (leftchild.leaf)  { // we are
			childptr.data = this.data;
			RedBlackNode<AnyType> temp = right ();
			if (shade == colors.black)
				if (temp.shade == colors.red)
					temp.shade = colors.black;
				else
					temp.shade = colors.absidian;
			return right(); // remove self
		}
		// else do the deletion
		left(leftchild.removeLeftmostDescendant (childptr) );
		return restoreLeftBalance ();
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
	public void left(RedBlackNode<AnyType> tnode) {
		leftptr = tnode;
	}
	
	/**
	 * returns handle to left subtree
	 * @return  handle to left subtree
	 */
	public RedBlackNode<AnyType> left () {
		return leftptr;
	}
	
	/**
	 * attaches tnode as root of right subtree
	 * @param  tnode  node to attach as right subtree
	 */
	public void right(RedBlackNode<AnyType> tnode) {
		rightptr = tnode;
	}
	
	/**
	 * returns handle to right subtree
	 * @return  handle to right subtree
	 */
	public RedBlackNode<AnyType> right  () {
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
		else if (cmp.compare(item, data) < 0 && !left ().leaf)
			found = left().contains (item);
		else if (!right ().leaf)
			found = right().contains (item);
		return found;
	}
	
	/**
	 * returns the number of nodes in the subtrees rooted at this node inclusive
	 * preconditions:    none
	 * post-conditions:  tree is unchanged
	 * @return  int  number of internal nodes in subtree
	 */
	public int size () {
		int count = 1;
		if (!leftptr.leaf)
			count += leftptr.size();
		if (!rightptr.leaf)
			count += rightptr.size();
		return count;
	}
	
	
	/**
	 * makes a deep copy of the (sub)tree rooted at root
	 * @param  root of the tree to be copied
	 * @return  reference to the root of the copied tree
	 */
	public RedBlackNode<AnyType> copy (RedBlackNode<AnyType> root) {
		RedBlackNode<AnyType> newRoot;
		if (root != null && !root.leaf) {
			newRoot = new RedBlackNode<AnyType> (root.data, cmp);
			newRoot.leaf = root.leaf;
			newRoot.shade = root.shade;
			if (!root.left( ).leaf)
				newRoot.leftptr = root.copy (root.left());
			if (!root.right( ).leaf)
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
	 * returns the greatest element of the sequence stored in the tree
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
