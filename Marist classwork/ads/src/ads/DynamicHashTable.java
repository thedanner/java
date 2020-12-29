package ads;

import java.util.LinkedList;

/**
 * Dynamic Hash Table for Assignment 5.
 * @author Dan Mangiarelli
 *
 * @param <T>
 */
@SuppressWarnings("unchecked")
public class DynamicHashTable<T>
{
	private static final int DEFAULT_INITIAL_CAPACITY = 1;
	
	private int size; // number of entry slots
	private int num; // actual number of entries
	private LinkedList<T>[] data;
	private Counter counter;
	
	public DynamicHashTable()
	{
		size = DEFAULT_INITIAL_CAPACITY;
		num = 0;
		data = (LinkedList<T>[]) new LinkedList[size];
		
		counter = new Counter();
	}
	
	public DynamicHashTable(DynamicHashTable<T> source)
	{
		if (source == null)
		{
			throw new NullPointerException("cannot clone a null vector");
		}
		
		size = source.size;
		num = source.num;
		
		data = (LinkedList<T>[]) new LinkedList[size];
		
		for (int i = 0; i < size; i++)
		{
			data[i] = source.data[i];
		}
	}
	
	public int getCounterValue()
	{
		return counter.get();
	}
	
	public void insert(T elem)
	{
		if (num >= size )
		{
			expand();
		}
		
		int key = Math.abs(elem.hashCode() % size);
		
		if(data[key] == null)
			data[key] = new LinkedList<T>();
		
		data[key].addFirst(elem);
		
		num++;
		
		counter.increment();
	}
	
	public void remove(T elem)
	{
		int key = Math.abs(elem.hashCode() % size);
		
		if(data[key] != null)
			data[key].remove(elem);
		
		num--;
		
		if(num == .25*size)
		{
			contract();
		}
		
		counter.increment();
	}
	
	public T get(int i)
	{
		if (i < 0 || i >= num)
			throw new IndexOutOfBoundsException(""+i);
		
		return (T) data[i];
	}
	
	public int size()
	{
		return num;
	}
	
	public int capacity()
	{
		return size;
	}
	
	public boolean isEmpty()
	{
		return (num == 0);
	}
	
	public boolean equals(DynamicHashTable<T> other)
	{
		if (size != other.size)
		{
			return false;
		}
		
		if (num != other.num)
		{
			return false;
		}
		
		for (int i = 0; i < size; i++) 
		{
			if (!data[i].equals(other.data[i]))
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void expand()
	{
		size = (size == 0 ? 1 : size * 2);
		
		rehash();
	}
	
	private void contract()
	{
		size = (size == 0 ? 1 : size / 2);
		
		rehash();
	}
	
	private void rehash()
	{
		LinkedList<T>[] oldData = data;
		
		data = (LinkedList<T>[]) new LinkedList[size];
		
		num = 0;
		
		for (int i = 0; i < oldData.length; i++)
		{
			if (oldData[i] != null)
			{
				for (T elem : oldData[i])
				{
					insert(elem);
				}
			}
		}
		
		counter.increment();
	}
	
	public static <T> void printTable(DynamicHashTable<T> table)
	{
		System.out.print("size = ");
		System.out.print(table.size());
		System.out.print(", capacity = ");
		System.out.print(table.capacity());
		System.out.print(", cost = ");
		System.out.print(table.getCounterValue());
		System.out.println();
		
		for (int i = 0; i < table.size; i++)
		{	
			System.out.print(i);
			System.out.print(" ---- ");
			System.out.print(table.data[i]);
			System.out.print(" ");
			System.out.println();
		}
		
		System.out.println();
	}
	
	public static void main(String[] args)
	{
		DynamicHashTable<String> table = new DynamicHashTable<String>();
		
		table.insert("Cox");
		table.insert("Glavine");
		table.insert("Smoltz");
		printTable(table);
		
		table.insert("Avery");
		table.insert("Pendleton");
		table.insert("Justice");
		table.insert("Gant");
		table.insert("Nixon");
		table.insert("Sanders");
		table.insert("Stanton");
		table.insert("Bream");
		//printTable(table);
		table.insert("Belliard");
		table.insert("Blauser");
		table.insert("Lemke");
		table.insert("Treadway");
		table.insert("Liebrandt");
		table.insert("Pena");
		table.insert("Olson");
		table.insert("Merker");
		printTable(table);
		table.insert("Berenguer");
		table.insert("LSmith");
		table.insert("PSmith");
		
		table.remove("Berenger");
		table.remove("Treadway");
		table.remove("Pena");
		
		table.insert("Maddux");
		table.insert("McGriff");
		//printTable(table);
		table.insert("McMichael");
		table.insert("O'Brien");
		
		table.remove("Bream");
		table.remove("Pendelton");
		table.remove("Stanton");
		table.remove("Olson");
		table.remove("Gant");
		table.remove("LSmith");
		printTable(table);
		table.remove("PSmith");
		table.remove("Nixon");
		table.remove("Sanders");
		
		table.insert("CJones");
		table.insert("Klesko");
		table.insert("Lopez");
		table.insert("Wohlers");
		table.insert("Grissom");
		//printTable(table);
		table.insert("Clontz");
		table.insert("Borbon");
		table.insert("DSmith");
		table.insert("Devereaux");
		table.insert("Polonia");
		table.insert("Pena");
		table.insert("Mordecai");
		table.insert("Neagle");
		printTable(table);
		table.insert("Dye");
		table.insert("AJones");
		table.insert("Galarraga");
		table.insert("Boone");
		table.insert("Remlinger");
		table.insert("Millwood");
		
		table.remove("O'Brien");
		table.remove("Pena");
		//printTable(table);
		table.remove("Devereaux");
		table.remove("Polonia");
		table.remove("Merker");
		table.remove("Belliard");
		table.remove("Blauser");
		table.remove("DSmith");
		table.remove("Mordecai");
		table.remove("Grissom");
		printTable(table);
		table.remove("Justice");
		table.remove("Dye");
		table.remove("McGriff");
		table.remove("Lemke");
		table.remove("McMichael");
		table.remove("Clontz");
		table.remove("Borbon");
		table.remove("Neagle");
		//printTable(table);
		table.remove("Klesko");
		table.remove("Boone");
		table.remove("Wohlers");
		
		table.insert("Furcal");
		table.insert("McGlinchey");
		table.insert("Veras");
		table.insert("RSanders");
		table.insert("Jordan");
		printTable(table);
		table.insert("Mulholland");
		table.insert("Rocker");
		table.insert("Seanez");
		table.insert("Joyner");
		table.insert("Bonilla");
		table.insert("Rivera");
		table.insert("Hammond");
		
		table.remove("McGlinchey");
		//printTable(table);
		table.remove("Veras");
		table.remove("RSanders");
		table.remove("Jordan");
		table.remove("Rocker");
		table.remove("Mulholland");
		table.remove("Seanez");
		table.remove("Joyner");
		table.remove("Bonilla");
		printTable(table);
		table.remove("Rivera");
		table.remove("Glavine");
		table.remove("Galarraga");
		table.remove("Remilnger");
		table.remove("Hammond");
		table.remove("Millwood");
		
		table.insert("Sheffield");
		table.insert("Giles");
		//printTable(table);
		table.insert("DeRosa");
		table.insert("Castilla");
		table.insert("JFranco");
		table.insert("Fick");
		table.insert("Bragg");
		table.insert("Ramirez");
		table.insert("Ortiz");
		table.insert("Hampton");
		printTable(table);
		table.insert("Estrada");
		table.insert("Drew");
		table.insert("Thomas");
		table.insert("Green");
		table.insert("Wright");
		table.insert("Reitsma");
		table.insert("Byrd");
		table.insert("Alfonseca");
		
		//printTable(table);
		
		table.remove("Maddux");
		table.remove("Sheffield");
		table.remove("Lopez");
		table.remove("Castilla");
		table.remove("Fick");
		table.remove("Ortiz");
		table.remove("DeRosa");
		table.remove("Wright");
		printTable(table);
		table.remove("Byrd");
		table.remove("Alfonseca");
		table.remove("Green");
		table.remove("Drew");
		table.remove("Bragg");
		table.remove("Thomas");
		
		table.insert("Kolb");
		table.insert("Hudson");
		//printTable(table);
		table.insert("Francouer");
		table.insert("McCann");
		table.insert("Johnson");
		table.insert("Langerhans");
		table.insert("Davies");
		table.insert("Boyer");
		table.insert("Betemit");
		table.insert("Orr");
		//printTable(table);
		table.insert("McBride");
		table.insert("Lerew");
		table.insert("James");
		
		table.remove("Kolb");
		table.remove("Estrada");
		table.remove("Betemit");
		
		printTable(table);
	}
}
/*
size = 3, capacity = 4, cost = 8
0 ---- [Cox] 
1 ---- null 
2 ---- [Glavine] 
3 ---- [Smoltz] 

size = 19, capacity = 32, cost = 55
0 ---- [Gant] 
1 ---- [Bream] 
2 ---- [Merker, Blauser] 
3 ---- [Smoltz] 
4 ---- null 
5 ---- [Treadway] 
6 ---- [Glavine] 
7 ---- null 
8 ---- [Pena] 
9 ---- null 
10 ---- null 
11 ---- null 
12 ---- [Cox] 
13 ---- [Belliard] 
14 ---- [Lemke] 
15 ---- null 
16 ---- null 
17 ---- [Pendleton] 
18 ---- null 
19 ---- null 
20 ---- null 
21 ---- [Olson, Liebrandt] 
22 ---- null 
23 ---- [Avery] 
24 ---- null 
25 ---- null 
26 ---- null 
27 ---- [Stanton] 
28 ---- [Nixon] 
29 ---- null 
30 ---- [Sanders] 
31 ---- [Justice] 

size = 17, capacity = 32, cost = 71
0 ---- [] 
1 ---- [] 
2 ---- [Merker, Blauser] 
3 ---- [Smoltz] 
4 ---- null 
5 ---- [] 
6 ---- [Glavine] 
7 ---- null 
8 ---- [] 
9 ---- [Maddux] 
10 ---- null 
11 ---- null 
12 ---- [Cox] 
13 ---- [PSmith, Belliard] 
14 ---- [Lemke] 
15 ---- null 
16 ---- null 
17 ---- [McMichael, Pendleton] 
18 ---- null 
19 ---- [Berenguer] 
20 ---- null 
21 ---- [Liebrandt] 
22 ---- [O'Brien] 
23 ---- [Avery] 
24 ---- [McGriff] 
25 ---- null 
26 ---- null 
27 ---- [] 
28 ---- [Nixon] 
29 ---- null 
30 ---- [Sanders] 
31 ---- [Justice] 

size = 27, capacity = 32, cost = 87
0 ---- [] 
1 ---- [Devereaux] 
2 ---- [Lopez, Merker, Blauser] 
3 ---- [Smoltz] 
4 ---- [Polonia] 
5 ---- [] 
6 ---- [Mordecai, Glavine] 
7 ---- null 
8 ---- [Pena] 
9 ---- [Maddux] 
10 ---- [Neagle] 
11 ---- null 
12 ---- [Cox] 
13 ---- [Klesko, Belliard] 
14 ---- [Clontz, Lemke] 
15 ---- null 
16 ---- null 
17 ---- [McMichael, Pendleton] 
18 ---- null 
19 ---- [Berenguer] 
20 ---- [CJones] 
21 ---- [Liebrandt] 
22 ---- [Wohlers, O'Brien] 
23 ---- [Avery] 
24 ---- [McGriff] 
25 ---- null 
26 ---- null 
27 ---- [] 
28 ---- [Borbon, Grissom] 
29 ---- null 
30 ---- [] 
31 ---- [DSmith, Justice] 

size = 25, capacity = 64, cost = 138
0 ---- null 
1 ---- null 
2 ---- [] 
3 ---- null 
4 ---- null 
5 ---- null 
6 ---- [] 
7 ---- null 
8 ---- [] 
9 ---- null 
10 ---- [Neagle] 
11 ---- null 
12 ---- null 
13 ---- null 
14 ---- [Lemke] 
15 ---- null 
16 ---- [Dye] 
17 ---- [McMichael] 
18 ---- null 
19 ---- null 
20 ---- null 
21 ---- null 
22 ---- [Wohlers] 
23 ---- [Millwood] 
24 ---- null 
25 ---- [Boone] 
26 ---- null 
27 ---- null 
28 ---- null 
29 ---- null 
30 ---- null 
31 ---- [] 
32 ---- null 
33 ---- [] 
34 ---- [Lopez] 
35 ---- [Smoltz, Remlinger] 
36 ---- [] 
37 ---- null 
38 ---- [Glavine] 
39 ---- null 
40 ---- null 
41 ---- [Maddux] 
42 ---- null 
43 ---- null 
44 ---- [Cox, Galarraga] 
45 ---- [Klesko] 
46 ---- [Clontz] 
47 ---- null 
48 ---- null 
49 ---- [Pendleton] 
50 ---- null 
51 ---- [Berenguer] 
52 ---- [CJones] 
53 ---- [Liebrandt] 
54 ---- [AJones] 
55 ---- [Avery] 
56 ---- [McGriff] 
57 ---- null 
58 ---- null 
59 ---- null 
60 ---- [Borbon] 
61 ---- null 
62 ---- null 
63 ---- [Justice] 

size = 19, capacity = 32, cost = 171
0 ---- null 
1 ---- null 
2 ---- [Lopez] 
3 ---- [Remlinger, Smoltz] 
4 ---- null 
5 ---- null 
6 ---- [Glavine] 
7 ---- [McGlinchey] 
8 ---- null 
9 ---- [Maddux] 
10 ---- null 
11 ---- [Furcal] 
12 ---- [Galarraga, Cox] 
13 ---- null 
14 ---- null 
15 ---- null 
16 ---- [RSanders] 
17 ---- [Pendleton] 
18 ---- null 
19 ---- [Berenguer] 
20 ---- [CJones] 
21 ---- [Veras, Liebrandt] 
22 ---- [AJones] 
23 ---- [Avery, Millwood] 
24 ---- null 
25 ---- [] 
26 ---- null 
27 ---- null 
28 ---- [Jordan] 
29 ---- null 
30 ---- null 
31 ---- null 

size = 17, capacity = 32, cost = 187
0 ---- null 
1 ---- null 
2 ---- [Lopez] 
3 ---- [Remlinger, Smoltz] 
4 ---- null 
5 ---- null 
6 ---- [Glavine] 
7 ---- [] 
8 ---- null 
9 ---- [Maddux] 
10 ---- null 
11 ---- [Rivera, Furcal] 
12 ---- [Galarraga, Cox] 
13 ---- null 
14 ---- [] 
15 ---- null 
16 ---- [] 
17 ---- [Pendleton] 
18 ---- null 
19 ---- [Berenguer] 
20 ---- [Hammond, CJones] 
21 ---- [Liebrandt] 
22 ---- [AJones] 
23 ---- [Avery, Millwood] 
24 ---- null 
25 ---- [] 
26 ---- null 
27 ---- null 
28 ---- [] 
29 ---- null 
30 ---- null 
31 ---- null 

size = 21, capacity = 32, cost = 203
0 ---- null 
1 ---- null 
2 ---- [Ortiz, Lopez] 
3 ---- [Remlinger, Smoltz] 
4 ---- [Ramirez] 
5 ---- null 
6 ---- [] 
7 ---- [] 
8 ---- null 
9 ---- [Hampton, Maddux] 
10 ---- null 
11 ---- [Fick, Furcal] 
12 ---- [DeRosa, Cox] 
13 ---- null 
14 ---- [] 
15 ---- null 
16 ---- [] 
17 ---- [Bragg, JFranco, Pendleton] 
18 ---- null 
19 ---- [Berenguer] 
20 ---- [CJones] 
21 ---- [Liebrandt] 
22 ---- [AJones] 
23 ---- [Castilla, Avery] 
24 ---- [Giles] 
25 ---- [] 
26 ---- null 
27 ---- null 
28 ---- [Sheffield] 
29 ---- null 
30 ---- null 
31 ---- null 

size = 21, capacity = 32, cost = 219
0 ---- [Drew] 
1 ---- null 
2 ---- [] 
3 ---- [Green, Remlinger, Smoltz] 
4 ---- [Ramirez] 
5 ---- null 
6 ---- [] 
7 ---- [] 
8 ---- null 
9 ---- [Byrd, Hampton] 
10 ---- [Alfonseca] 
11 ---- [Furcal] 
12 ---- [Cox] 
13 ---- null 
14 ---- [] 
15 ---- null 
16 ---- [] 
17 ---- [Bragg, JFranco, Pendleton] 
18 ---- [Estrada] 
19 ---- [Berenguer] 
20 ---- [CJones] 
21 ---- [Liebrandt] 
22 ---- [AJones] 
23 ---- [Reitsma, Avery] 
24 ---- [Giles] 
25 ---- [] 
26 ---- null 
27 ---- [] 
28 ---- [Thomas] 
29 ---- null 
30 ---- null 
31 ---- null 

size = 25, capacity = 32, cost = 241
0 ---- [] 
1 ---- null 
2 ---- [] 
3 ---- [Francouer, Remlinger, Smoltz] 
4 ---- [James, Ramirez] 
5 ---- [Hudson] 
6 ---- [] 
7 ---- [Johnson] 
8 ---- null 
9 ---- [Hampton] 
10 ---- [] 
11 ---- [Lerew, Furcal] 
12 ---- [McCann, Cox] 
13 ---- null 
14 ---- [] 
15 ---- [Orr] 
16 ---- [] 
17 ---- [JFranco, Pendleton] 
18 ---- [] 
19 ---- [Berenguer] 
20 ---- [CJones] 
21 ---- [Liebrandt] 
22 ---- [AJones] 
23 ---- [Reitsma, Avery] 
24 ---- [Giles] 
25 ---- [Boyer, Langerhans] 
26 ---- [] 
27 ---- [] 
28 ---- [McBride] 
29 ---- null 
30 ---- [Davies] 
31 ---- null 

*/
