package ads.treestuff;

import java.util.Vector;

public class Stack<AnyType> {
	private Vector<AnyType> v;
	private int last;
	
	public Stack( ) {
		v = new Vector<AnyType>( );
		last = 0;
	}
	public void push(AnyType obj) {
		v.add(obj);
		last++;
	}
	public AnyType pop( ) throws ObjectNotFoundException {
		if (last > 0){
			last--;
			AnyType temp = v.get(last);
			v.remove(last);
			return temp;
		}
		else
			throw new ObjectNotFoundException("error -- stack is empty");
	}
	
	public AnyType top( ) throws ObjectNotFoundException {
		if (last > 0) {
			return v.get(last-1);
		}
		else
			throw new ObjectNotFoundException("error -- stack is empty");
	}
	
	public boolean isEmpty( ) {
		return last == 0;
	}
}
