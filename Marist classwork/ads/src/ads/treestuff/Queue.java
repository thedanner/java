package ads.treestuff;

import java.util.Vector;

public class Queue<AnyType> {
	Vector<AnyType> buffer;
	public Queue( ) {
		buffer = new Vector<AnyType>();
	}
	public void enqueue(AnyType obj) {
		buffer.add(obj);
	}
	public AnyType dequeue( ) {
		return buffer.remove(0);
	}
	public boolean isEmpty( ) {
		return buffer.isEmpty();
	}	
}
