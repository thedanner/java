package ads.treestuff;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("unchecked")
public class AVLtest<AnyType> extends JFrame
implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private AVLtree<AnyType> tree;
	private Font normalFont = new Font("Serif", Font.PLAIN, 20);
	private Font smallFont = new Font("Serif", Font.PLAIN, 14);
	private JMenuBar mb = new JMenuBar( );
	private JMenu theMenu = new JMenu("AVL TREE TEST MAIN MENU");
	private JMenuItem add = new JMenuItem("A. Add a new item to the tree");
	private JMenuItem del = new JMenuItem("D. Delete a key from the tree");
	private JMenuItem show = new JMenuItem("S. Show an inorder list of all the keys in the tree");
	private JMenuItem query = new JMenuItem("Q. Query whether a particular key is in the tree");
	private JMenuItem bfs = new JMenuItem("B. Breadth-first Search");
	private JMenuItem exit = new JMenuItem("E. Exit");
	private JTextArea theDisplay = new JTextArea(10, 40);
	private JScrollPane sp = new JScrollPane(theDisplay, 
			JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	
	public void insert( AnyType value)  {
		try {
			tree.add(value);
		}
		catch (IllegalArgumentException e) {
			//report and do nothing
			System.err.println(e);
		}
	}
	
	public void remove( AnyType value)  {
		tree.remove(value);
	}
	
	public boolean IsFound( AnyType value)  {
		return tree.contains(value);
	}
	
	public void breadthFirst( ) {
		//To-Do:  implement this method
	}
	
	public void showAll() {
		AVLTreeIterator<AnyType> itr = tree.iterator( );
		while (itr.hasNext( ))  {
			theDisplay.append("\n" + itr.next( ));
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
				theDisplay.append("\nThe key " + value + " is not in the tree! \n");
		}
		else if (e.getSource()== bfs) {
			breadthFirst( );
		}
		else if (e.getSource() == exit) {
			theDisplay.append("\n\nTest ended.  Final traversal of the tree:\n");
			showAll( );
			JOptionPane.showMessageDialog(null, "Quitting", "Exit Screen", JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		}
	}
	
	public AVLtest(AVLtree<AnyType> tree)  {
		this.tree = tree;
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
		theMenu.add(bfs);
		bfs.addActionListener(this);
		theMenu.add(exit);
		exit.addActionListener(this);
		JPanel menuPanel = new JPanel();
		menuPanel.add(mb, BorderLayout.NORTH);
		//menuPanel.add(theMenu,BorderLayout.CENTER);
		viewer.add(menuPanel, BorderLayout.WEST);
		theDisplay.setFont(smallFont);
		viewer.add(sp, BorderLayout.CENTER);
		
		setTitle("AVLTREE TEST MENU");
		setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	public static void main(String[ ] args)  {
		AVLtree<String> avlt = new AVLtree<String>(new StringComparator ( ));
		new AVLtest<String>(avlt);
	}
}
