package ads.skiplist;

import java.util.Iterator;

public class SkipListIterator<AnyType> implements Iterator<AnyType> {
	private Node<AnyType> p;
	private SkipList<AnyType> list;
	private int level;

	public SkipListIterator(SkipList<AnyType> list, Node<AnyType> header) {
		p = header;
		this.list = list;
	}
	public boolean hasNext() {
		return !list.isNull(p.next[1]);
	}

	public AnyType next() {
		AnyType returnValue = p.next[1].key();
		level = p.next[1].level( );
		p = p.next[1];
		return returnValue;
	}
	public int getLevel( ) {
		return level;
	}

	public void remove() {
		// TODO Auto-generated method stub

	}
}
