package ads.skiplist;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class SkipList<AnyType> {
	private Comparator<AnyType> comp;
	private double p;
	private int Max_Level;
	private Node<AnyType> header;
	private Node<AnyType> nullNode;
	private int maxLevel;
	
	public SkipList(Comparator<AnyType> comp, double p,
			int maxLevels){
		this.comp = comp;
		this.p = p;
		Max_Level = maxLevels;
		header = new Node<AnyType> (Max_Level+1);
		nullNode = new Node<AnyType>(Max_Level);
		for (int i = 0;i <= Max_Level; i++)
			header.next[i] = nullNode;
		maxLevel = 1;
	}
	
	private int randomLevel( ) {
		int newLevel = 1;
		while (Math.random()< p)
			newLevel++;
		return Math.min(newLevel,Max_Level);
	}
	
	public boolean contains(AnyType key) {
		Node<AnyType> p = header;
		for (int i = maxLevel; i > 0; i--) {
			while (p.next[i] != nullNode && comp.compare(p.next[i].key(), key)< 0)
				p = p.next[i];
		}
		p = p.next[1];
		if (p != nullNode)
			return comp.compare(p.key(), key)== 0;
		else
			return false;
	}
	public void add(AnyType item) {
		Node<AnyType>[ ] update = new Node[Max_Level+1];
		Node<AnyType> p = header;
		for (int i = maxLevel; i > 0; i--) {
			while (p.next[i] != nullNode && comp.compare(p.next[i].key(), item) < 0)
				p = p.next[i];
			update[i] = p;
		}
		//Now add the new value to the list
		p = p.next[1];
		if (p!= nullNode && comp.compare(p.key(),item)==0)
			p.key = item;
		else {
			int newLevel = randomLevel();
			if (newLevel > maxLevel) {
				for (int i = maxLevel +1; i <= newLevel; i++)
					update[i] = header;
				maxLevel = newLevel;
			}
			Node<AnyType> q = new Node<AnyType>(item, newLevel);
			for (int j = 1; j <= newLevel; j++) {
				q.next[j] = update[j].next[j];
				update[j].next[j] = q;
			}
		}
	}
	
	public void delete(AnyType item) {
		Node<AnyType>[ ] update = new Node[Max_Level + 1];
		Node<AnyType> p = header;
		for (int i = maxLevel; i > 0; i--) {
			while (p.next[i] != nullNode && comp.compare(p.next[i].key(), item) < 0)
				p = p.next[i];
			update[i] = p;
		}
		p = p.next[1];
		if (p!= nullNode && comp.compare(p.key(), item)== 0) {
			for (int j = 1; j <= maxLevel; j++) {
				if (update[j].next[j] != p) break;
				update[j].next[j] = p.next[j];
			}
			int level = maxLevel;
			while (level > 1 && header.next[level] == nullNode) {
				maxLevel--;
				level--;
			}
		}
	}
	public boolean isNull(Node<AnyType> p) {
		return p == nullNode;
	}
	public SkipListIterator<AnyType> iterator() {
		return new SkipListIterator<AnyType>(this, this.header);
	}
}
