package ads.skiplist;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("unchecked")
public class SkipListTester<AnyType> extends JFrame
implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2339368331845980101L;
	
	private SkipList<AnyType> list;
	private Font normalFont = new Font("Serif", Font.PLAIN, 20);
	private Font smallFont = new Font("Serif", Font.PLAIN, 14);
	private JMenuBar mb = new JMenuBar( );
	private JMenu theMenu = new JMenu("SKIP LIST TEST MAIN MENU");
	private JMenuItem add = new JMenuItem("A. Add a new item to the list");
	private JMenuItem del = new JMenuItem("D. Delete a key from the list");
	private JMenuItem show = new JMenuItem("S. Show an inorder list of all the keys in the list");
	private JMenuItem query = new JMenuItem("Q. Query whether a particular key is in the list");
	private JMenuItem rgen = new JMenuItem("R. Generate 100 Strings");
	private JMenuItem exit = new JMenuItem("E. Exit");
	private JTextArea theDisplay = new JTextArea(10, 40);
	private JScrollPane sp = new JScrollPane(theDisplay, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	private AnyType[] array;
	
	public void insert( AnyType value)  {
		try {
			list.add(value);
		}
		catch (IllegalArgumentException e) {
			//report and do nothing
			System.err.println(e);
		}
	}
	
	public void remove( AnyType value)  {
		list.delete(value);
	}
	
	public boolean IsFound( AnyType value)  {
		return list.contains(value);
	}
	
	public void breadthFirst( ) {
		//TO-DO:  Implement this method
	}
	public void randomStringGenerate( ) {
		Random randy = new Random(12357L);
		array = (AnyType[]) new Object[1000];
		for (int i = 0; i < 1000; i++) {
			StringBuffer st = new StringBuffer();
			for (int j = 0; j < 4; j++) {
				int c = Math.abs(randy.nextInt())% 26;
				char ch = (char)(c + 65);
				st.append(ch);
			}
			array[i] = ((AnyType)st.toString());
		}
		long begin = System.currentTimeMillis();
		for (int j = 0; j < 1000; j++)
			list.add(array[j]);
		long end = System.currentTimeMillis();
		long diff = end - begin;
		theDisplay.append("\n\nThe count is: " + diff + "\n");
	}
	
	public void showAll() {
		SkipListIterator<AnyType> itr = list.iterator( );
		while (itr.hasNext( ))  {
			theDisplay.append("\n" + itr.next( ) +", "+itr.getLevel());
		}
		theDisplay.append("\n\n");
	}
	public void actionPerformed(ActionEvent e) {
		if (e.getSource( ) == add) {
			String value = JOptionPane.showInputDialog(null, 
					"Enter the String to be inserted", 
					"Insert Dialog Box", JOptionPane.QUESTION_MESSAGE);
			insert((AnyType)value);
		}
		else if (e.getSource( ) == del) {
			String value = JOptionPane.showInputDialog(null, 
					"Enter the String to be deleted", 
					"Remove Dialog Box", JOptionPane.QUESTION_MESSAGE);
			remove((AnyType)value);
		}
		else if (e.getSource() == show) {
			showAll( );
		}
		else if (e.getSource() == query) {
			String value = JOptionPane.showInputDialog(null, 
					"Enter the String to find", 
					"Query Dialog Box", JOptionPane.QUESTION_MESSAGE);
			if(IsFound((AnyType)value)) 
				theDisplay.append("\nThe key " + value + " has been found! \n");
			else
				theDisplay.append("\nThe key " + value + " is not in the list! \n");
		}
		else if (e.getSource()== rgen) {
			randomStringGenerate( );
		}
		else if (e.getSource() == exit) {
			theDisplay.append("\n\nTest ended.  Final traversal of the list:\n");
			showAll( );
			JOptionPane.showMessageDialog(null, "Quitting", "Exit Screen", JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
	}
	
	public SkipListTester(SkipList<AnyType> list)  {
		this.list = list;
		Container viewer = getContentPane();
		setJMenuBar(mb);
		mb.add(theMenu);
		theMenu.setFont(normalFont);
		theMenu.add(add);
		add.addActionListener(this);
		theMenu.add(del);
		del.addActionListener(this);
		theMenu.add(show);
		show.addActionListener(this);
		theMenu.add(query);
		query.addActionListener(this);
		theMenu.add(rgen);
		rgen.addActionListener(this);
		theMenu.add(exit);
		exit.addActionListener(this);
		JPanel menuPanel = new JPanel();
		menuPanel.add(mb, BorderLayout.NORTH);
		//menuPanel.add(theMenu,BorderLayout.CENTER);
		viewer.add(menuPanel, BorderLayout.WEST);
		theDisplay.setFont(smallFont);
		viewer.add(sp, BorderLayout.CENTER);
		
		setTitle("SKIP LIST TEST MENU");
		setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	
	public static void main(String[ ] args)  {
		SkipList<String> skippy = new SkipList<String>(new StringComparator ( ), 0.5, 8);
		new SkipListTester<String>(skippy);
	}
}
