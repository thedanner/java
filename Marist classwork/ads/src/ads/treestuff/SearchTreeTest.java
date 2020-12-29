package ads.treestuff;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

/**
 * Modified test app for a Binary Search Tree to demonstrate a breadth-first
 * traversal.  See methods breadthFirst() and traverseBreadthFirst() for
 * the traversal implementation.
 *  
 * @author Dan Mangiarelli
 * @version October 15, 2007
 *
 * @param <AnyType> the data type of the Binary Search Tree we're showing off.
 */
@SuppressWarnings("unchecked")
public class SearchTreeTest<AnyType> extends JFrame implements ActionListener {
	/**  */
	private static final long serialVersionUID = 8744781382394461392L;
	
	private static final String TRAVERSAL_OUT_FILE = "breadth_first.txt";
	
	private static final StringComparator STRING_COMPARATOR =
		new StringComparator();
	
	private static final String[] SAMPLE_VALUES = new String[] {
		"MOUSE", "ZEBRA", "BEAR", "DOG", "CAT",
		"TIGER", "LION", "ELEPHANT", "SEAL", "MOOSE",
		"MULE", "HORSE", "HIPPOPOTAMUS", "KANGAROO", "WHALE", "RACCOON",
		"SKUNK", "DEER", "ELK", "GNU", "FROG", "SNAKE", "PIG",
		"COW", "GORILLA"
	};
	
	private final TreeNode<AnyType> END_OF_TREE_LEVEL_NODE = (TreeNode<AnyType>)
		new TreeNode<String>("", STRING_COMPARATOR);
	
	private BinarySearchTree<AnyType> tree;
	private Font normalFont = new Font("Serif", Font.PLAIN, 20);
	private Font smallFont = new Font("Serif", Font.PLAIN, 14);
	private JMenuBar mb = new JMenuBar();
	private JMenu theMenu = new JMenu("AVL TREE TEST MAIN MENU");
	private JMenuItem populate = new JMenuItem("P. Populate the tree with sample values.");
	private JMenuItem add = new JMenuItem("A. Add a new item to the tree");
	private JMenuItem del = new JMenuItem("D. Delete a key from the tree");
	private JMenuItem show = new JMenuItem(
			"S. Show an inorder list of all the keys in the tree");
	private JMenuItem query = new JMenuItem(
			"Q. Query whether a particular key is in the tree");
	private JMenuItem bfs = new JMenuItem("B. Breadth-first Search");
	private JMenuItem exit = new JMenuItem("E. Exit");
	private JTextArea theDisplay = new JTextArea(10, 40);
	private JScrollPane sp = new JScrollPane(theDisplay,
			ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	
	public void insert(AnyType value) {
		try {
			tree.add(value);
		} catch (IllegalArgumentException e) {
			// report and do nothing
			e.printStackTrace();
		}
	}
	
	public void remove(AnyType value) {
		tree.remove(value);
	}
	
	public boolean isFound(AnyType value) {
		return tree.contains(value);
	}
	
	public void breadthFirst() {
		
		theDisplay.append(String.format("Breadth-first traversal:%n"));
		
		traverseBreadthFirst(tree.root);
		
		theDisplay.append(String.format("%n%n"));
	}
	
	private void traverseBreadthFirst(TreeNode<AnyType> node)
	{
		PrintWriter out = null;
		
		try {
			out = new PrintWriter(new FileWriter(TRAVERSAL_OUT_FILE));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Queue< TreeNode<AnyType> > queue = new Queue< TreeNode<AnyType> >();
		
		// Enqueue the top of the tree, and since that's the only node at that
		// level, throw our level terminator on the queue as well.
		queue.enqueue(node);
		queue.enqueue(END_OF_TREE_LEVEL_NODE);
		
		while (!queue.isEmpty())
		{
			TreeNode<AnyType> current = queue.dequeue();
			
			if (current != null)
			{
				// We're not done traversing a level yet.
				if (current != END_OF_TREE_LEVEL_NODE)
				{
					printf(out, "%1$s  ", current);
					
					queue.enqueue(current.left());
					queue.enqueue(current.right());
				}
				// END of a level in the tree, is indicated by the
				// "sentinel value" node that added to the queue at the end of
				// traversing the previous level.
				else
				{
					printf(out, "%n--");
					
					if (!queue.isEmpty())
					{
						queue.enqueue(END_OF_TREE_LEVEL_NODE);
					}
				}
			}
			// the node is null, meaning its parent doesn't have
			// a child, or it has no parent in the preceding level.
			else
			{
				printf(out, "NULL  ");
			}
		}
		
		if (out != null)
			out.close();
	}
	
	/**
	 * Formats the given string based on the provided arguments,
	 * and then print that string to standard output, to the specified
	 * PrintWriter instance if it's not null, and appends it to display area.
	 * 
	 * @param out an optional PrintWriter to direct output to.
	 * @param formatString a format string
	 * @param args arguments to the format string
	 */
	private void printf(PrintWriter out, String formatString, Object... args)
	{
		String outString = String.format(formatString, args);
		
		theDisplay.append(outString);
		System.out.print(outString);
		
		if (out != null)
		{
			out.print(outString);
		}
	}
	
	public void showAll() {
		TreeIterator<AnyType> itr = tree.iterator();
		
		while (itr.hasNext())
			theDisplay.append(String.format("%n%1$s", itr.next()));
		
		theDisplay.append(String.format("%n%n"));
	}
	
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == populate) {
			for (String animal : SAMPLE_VALUES)
				insert((AnyType) animal);
			
			theDisplay.append(String.format("Sample values added.%n%n"));
		} else if (e.getSource() == add) {
			String value = JOptionPane.showInputDialog(this,
					"Enter the String to be inserted", "Insert Item",
					JOptionPane.QUESTION_MESSAGE);
			
			insert((AnyType) value);
		} else if (e.getSource() == del) {
			String value = JOptionPane.showInputDialog(this,
					"Enter the String to be deleted", "Remove Item",
					JOptionPane.QUESTION_MESSAGE);
			
			remove((AnyType) value);
		} else if (e.getSource() == show) {
			showAll();
		} else if (e.getSource() == query) {
			String value = JOptionPane.showInputDialog(null,
					"Enter the String to find", "Query",
					JOptionPane.QUESTION_MESSAGE);
			
			if (isFound((AnyType) value))
				theDisplay.append(
						String.format("%nThe key %1$s has been found.%n", value));
			else
				theDisplay.append(
						String.format("%nThe key %1$s is not in the tree.%n", value));
		} else if (e.getSource() == bfs) {
			breadthFirst();
		} else if (e.getSource() == exit) {
			theDisplay.append("\n\nTest ended.  Final traversal of the tree:\n");
			
			showAll();
			
			JOptionPane.showMessageDialog(this,
					"Quitting", "Bye Bye",
					JOptionPane.PLAIN_MESSAGE);
			
			System.exit(0);
		}
	}
	
	public SearchTreeTest(BinarySearchTree<AnyType> tree) {
		this.tree = tree;
		Container viewer = getContentPane();
		setJMenuBar(mb);
		mb.add(theMenu);
		theMenu.setFont(normalFont);
		theMenu.add(populate);
		populate.addActionListener(this);
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
		// menuPanel.add(theMenu,BorderLayout.CENTER);
		viewer.add(menuPanel, BorderLayout.WEST);
		theDisplay.setFont(smallFont);
		viewer.add(sp, BorderLayout.CENTER);
		
		setTitle("AVLTREE TEST MENU");
		setSize(600, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public static void main(String[] args) {
		final BinarySearchTree<String> bst =
			new BinarySearchTree<String>(STRING_COMPARATOR);
		
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new SearchTreeTest<String>(bst).setVisible(true);
			}
		});
	}
}
