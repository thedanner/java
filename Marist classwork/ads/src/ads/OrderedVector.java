package ads;

import java.util.Comparator;

public class OrderedVector<AnyType> {
	/**
	 * capacity = the capacity of the vector size = the number of elements in
	 * the vector data = a vector of type AnyType
	 */
	private int capacity;
	private int size;
	private AnyType[] data;
	private Comparator<AnyType> comp;
	
	/**
	 * constructor
	 */
	@SuppressWarnings("unchecked")
	public OrderedVector(Comparator<AnyType> comp) {
		capacity = 10;
		data = (AnyType[]) new Object[capacity];
		size = 0;
		this.comp =  comp;
	}
	
	@SuppressWarnings("unchecked")
	public OrderedVector(OrderedVector<AnyType> source, Comparator<AnyType> comp) {
		/**
		 * Copy constructor -- Makes a deep copy of the supplied source Vector
		 * precondition -- source vector is not null
		 */
		this.comp = comp;
		if (source == null) {
			System.out.println("Cannot clone a null vector!");
			System.exit(1);
		}
		capacity = source.capacity;
		data = (AnyType[]) new Object[capacity];
		for (int i = 0; i < source.size; i++)
			data[i] = source.data[i];
		size = source.size;
	}
	
	/**
	 * Precondition: create(v) there is already a vector created to clone
	 * Post-condition: vector is cloned
	 */
	@SuppressWarnings("unchecked")
	public OrderedVector<AnyType> clone() {
		try {
			OrderedVector<AnyType> clone = (OrderedVector<AnyType>) super.clone();
			clone.data = data.clone();
			return clone;
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.toString());
		}
	}
	
	/**
	 * Adds AnyType to the vector
	 */
	public void add(AnyType o) {
		if (size >= capacity) {
			resize();
		}
		
		if(size == 0) {
			addAt(o, 0);
		} else {
			int i = 0;
			
			while(i < size && comp.compare(data[i], o) <= 0)
				i++;
			
			addAt(o, i);
		}
	}
	
	/**
	 * Precondition: the size of the vector is >= 0 and i<= the size of the
	 * vector
	 *
	 * @param ob =
	 *            the object to be added to the vector
	 * @param i =
	 *            the location of it to be added in the vector
	 */
	private void addAt(AnyType ob, int i) throws IndexOutOfBoundsException {
		if (i > size || i < 0)
			throw new IndexOutOfBoundsException("index error in insert( )");
		if (size == capacity)
			resize();
		int j = size;
		while (j > i) {
			data[j] = data[j - 1];
			j--;
		}
		data[i] = ob;
		size++;
	}
	
	/**
	 * throws exception if the vector is empty param i = the part of the vector
	 * to be removed
	 */
	public void removeAt(int i) throws ArrayIndexOutOfBoundsException {
		if (i >= size || i < 0)
			throw new IndexOutOfBoundsException("index error in remove( )");
		for (int j = i + 1; j < size; j++)
			data[j - 1] = data[j];
		size--;
	}
	
	/**
	 * @param obj =
	 *            element to be removed precondition: the vector contains the
	 *            element
	 */
	public void remove(AnyType obj) {
		int i = 0;
		while (i < size && !data[i].equals(obj))
			i++;
		for (int j = i + 1; j < size; j++)
			data[j - 1] = data[j];
		size--;
	}
	
	/**
	 * @param obj =
	 *            object to search for post-condition: return true if the vector
	 *            contains obj
	 *            comparator
	 */
	public boolean contains(AnyType obj) {
		int j = 0;
		while (j < size) {
			//state = state || (data[j].equals(obj));
			if(comp.compare(data[j], obj)==0)
				return true;
			j++;
		}
		return false;
	}
	
	/**
	 * precondition: the position to set the AnyType is between 0 and the size
	 * of the vector
	 *
	 * @param i =
	 *            position to put object in the vector
	 * @param obj =
	 *            AnyType to put in the vector overrides object in the vector
	 */
	public void set(int i, AnyType obj) {
		if (i >= size || i < 0)
			throw new IndexOutOfBoundsException("index error in set( )");
		data[i] = obj;
	}
	
	/**
	 * precondition: the size of the vector is greater than 0 and the value of i
	 * is >=0 and <=size-1
	 *
	 * @param i =
	 *            the index of the AnyType object
	 * @return = return the object from index i
	 */
	public AnyType get(int i) {
		if (i >= size || i < 0)
			throw new IndexOutOfBoundsException("index error in get( )");
		return (AnyType)data[i];
	}
	
	/**
	 * Post-conditions: the size of a new vector is 0, the size increases by 1
	 * when a new element is added, the size decreases by 1 if an element is
	 * removed return = the size of the vector
	 */
	public int size() {
		return size;
	}
	
	
	
	/**
	 * Post-conditions: if a vector is cloned it has the same size, when adding
	 * to the vector its capacity increases, the size of the vector is bigger
	 * even when an element is removed
	 *
	 * @return = the capacity of the vector
	 */
	public int capacity() {
		return capacity;
	}
	
	/**
	 * post-condition: if empty returns true, if something is added to the
	 * vector it is false
	 */
	public boolean isEmpty()
	{
		return size==0;
	}
	
	
	/**
	 * returns true if a = b
	 */
	public boolean equals(OrderedVector<AnyType> a) {
		if (capacity != a.capacity())
			return false;
		if (size != a.size)
			return false;
		int i = 0;
		while (i < size)
		{
			if (!data[i].equals(a.data[i]))
				return false;
			i++;
		}
		return true;
	}
	
	/**
	 * resizes the capacity
	 */
	@SuppressWarnings("unchecked")
	private void resize() {
		if (capacity == 0)
			capacity = capacity + 1;
		else
			capacity = capacity * 2;
		AnyType newBuffer[];
		newBuffer = (AnyType[]) new Object[capacity];
		for (int i = 0; i < size; i++)
			newBuffer[i] = data[i];
		data = newBuffer;
	}
}
