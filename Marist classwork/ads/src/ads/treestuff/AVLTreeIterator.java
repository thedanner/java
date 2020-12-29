package ads.treestuff;

import java.util.Iterator;

/**
 * A class for traversing in order an ordered sequence of unique elements
 * @author  J. Ten Eyck
 * @date    July 12, 2006
 */

class AVLTreeIterator<AnyType> implements Iterator<AnyType> {
	private AVLNode<AnyType> root, last;
	private Stack<AVLNode<AnyType>> stack;
	
	/**
	 * Constructor for creating an Iterator for the (sub)tree rooted at tn
	 * @param  tn  an AVLTreeNode
	 */
	public AVLTreeIterator (AVLNode<AnyType> tn) {
		root = tn;
		last = null;
		stack = new Stack<AVLNode<AnyType>>( );
		initStack(root);
	}
	
	/**
	 * Stack is used to explicitly keep an ordered sequence of the leftmost
	 * unvisited nodes
	 * @param  node  the root of the subtree to traverse (push onto stack)
	 */
	private void initStack(AVLNode<AnyType> node) {
		stack.push(node);
		while(node.left( )!= null) {
			stack.push(node.left( ) );
			node = node.left( );
		}
	}
	
	/**
	 * returns true if there are more unvisited nodes to traverse
	 * precondition:     none
	 * @return   boolean  true if nodes remaining to visit
	 */
	public boolean hasNext( ) {
		return !stack.isEmpty( );
	}
	
	/**
	 * returns the next item in the stored sequence
	 * precondition:    stack is not empty
	 * post-condition:  node is popped from stack, initStack(right( )) called
	 * exception   ObjectNotFoundException is raised and program terminates
	 * @return   AnyType   item from stored sequence returned
	 */
	public AnyType next( ) {
		try {
			AVLNode<AnyType> tn = stack.pop( );
			last = tn;
			if (tn.right( )!= null )
				initStack(tn.right( ) );
			return tn.data( );
		}
		catch (ObjectNotFoundException e) {
			System.out.println(e);
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * resets the iterator to the original root and calls initStack
	 */
	public void reset( ) {
		stack = new Stack<AVLNode<AnyType>>( );
		initStack(root);
	}
	
	/**
	 * removes the last referenced node from the tree
	 */
	public void remove( ) {
		try {
			root = root.delete(last.data( ) );
			//leftmost descendant will already be on top of stack
		} catch (ObjectNotFoundException e) {
			System.out.println(e);
			System.exit(1);
		}
	}
}
