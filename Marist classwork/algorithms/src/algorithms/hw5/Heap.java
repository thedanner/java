package algorithms.hw5;

import java.util.Comparator;

public class Heap<T>
{
	private Object[] buffer;
	private Comparator<T> comp;
	private int next;
	
	public Heap(Comparator<T> comp, T[] keys)
	{
		this.comp = comp;
		
		heapify(keys);
	}
	
	private void heapify(T[] keys)
	{
		buffer = new Object[keys.length + 1];
		
		next = keys.length + 1;
		
		for (int i = 0; i < keys.length; i++)
		{
			set(i + 1, keys[i]);
		}
		
		for (int i = (next - 1) / 2; i > 0; i--)
		{
			siftDown(i);
		}
	}
	
	public T deleteMin()
	{
		if (next <= 1)
			throw new IndexOutOfBoundsException(next+"");
		
		T root = get(1);
		
		next--;
		
		if (next >= 1)
		{
			set(1, get(next));
			siftDown(1);
		}
		
		buffer[next] = null;
		
		return root;
	}
	
	private void siftDown(int i)
	{
		if (((2*i+1) < next) &&
				(compare(get(2*i+1), get(2*i)) < 0))
		{
			if (compare(get(2*i+1), get(i)) < 0)
			{
				swap(i, 2*i+1);
				siftDown(2*i+1);
			}
		}
		else if ((2*i < next) &&
				(compare(get(2*i), get(i)) < 0))
		{
			swap(i, 2*i);
			siftDown(2*i);
		}
	}
	
	private void siftUp(int i)
	{
		if (i == 1)
			return;
		
		int j = i / 2;
		
		if (compare(get(i), get(j)) < 0)
		{
			swap(i, j);
			siftUp(j);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public T get(int i)
	{
		return (T) buffer[i];
	}
	
	public void set(int i, T element)
	{
		buffer[i] = element;
	}
	
	public boolean empty()
	{
		return (next <= 1);
	}
	
	private int compare(T o1, T o2)
	{
		return comp.compare(o1, o2);
	}
	
	private void swap(int i1, int i2)
	{
		T temp = get(i1);
		set(i1, get(i2));
		set(i2, temp);
	}
}
