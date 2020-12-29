package ads.skiplist;

@SuppressWarnings("unchecked")
public class Node<AnyType> {
	protected int level;
	protected Node<AnyType>[ ] next;
	protected AnyType key;
	
	public Node(int levels) {
		level = levels;
		next = new Node[level+1];
		key = null;
	}
	public Node(AnyType item, int level) {
		this.level = level;
		key = item;
		next = new Node[level+1];
	}
	public AnyType key( ) {
		return key;
	}
	public int level( ) {
		return level;
	}

}
