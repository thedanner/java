package compilerDesign.hw2;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import compilerDesign.hw2.grammar.Grammar;
import compilerDesign.hw2.token.Token;
import compilerDesign.util.SwingWorker;

public class RecursiveDescentParserGui extends JFrame
implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9080378596305194133L;
	
	private String grammarPath;
	private Grammar<Token> grammarRules;
	
	private JFileChooser fileChooser;
	private File sourceFile;
	
	private JLabel grammarLabel;
	private JTextPane grammarText;
	private JPanel grammarPanel;
	
	private JLabel sourceLabel;
	private JTextPane sourceText;
	private JPanel sourcePanel;
	
	private JLabel statusLabel;
	private JCheckBox verboseCheckBox;
	private JTextPane statusText;
	private JPanel statusPanel;
	
	private JButton compileButton;
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton loadButton;
	private JButton quitButton;
	private JPanel buttonPanel;
	
	private SwingWorker compileWorker;
	
	public RecursiveDescentParserGui(String grammarPath, Grammar<Token> grammarRules) {
		this.grammarPath = grammarPath;
		this.grammarRules = grammarRules;
		
		fileChooser = new JFileChooser();
		sourceFile = null;
		
		initGui();
		
		grammarRules.setOutputComponent(this);
	}
	
	private void initGui() {
		setTitle("Recursive Descent Parser");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		
		createLanguageSpecPanel(grammarPath);
		createSourceInputPanel();
		createStatusPanel();
		createButtonPanel();
		
		JPanel textAreas = new JPanel(createBorderLayout());
		
		textAreas.add(grammarPanel, "North");
		textAreas.add(sourcePanel, "Center");
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(10, 10));
		
		cp.add(textAreas, "West");
		cp.add(statusPanel, "Center");
		cp.add(buttonPanel, "South");
		
		pack();
		
		setMinimumSize(getSize());
		
		setSize(625, 600);
	}
	
	private void initCompileWorker() {
		compileWorker = new SwingWorker() {
			@Override
			public Object construct() {
				grammarRules.run(sourceText.getText());
				
				return null;
			}
			
			@Override
			public void finished() {
				sourceText.setEditable(true);
				compileButton.setEnabled(true);
			}
		};
	}
	
	private void createLanguageSpecPanel(final String filePath) {
		grammarText = createTextPane();
		
		grammarText.setEditable(false);
		grammarText.setText("Loading grammar spec...");

		JScrollPane scrollPane = new JScrollPane(grammarText);
		
		grammarPanel = new JPanel(createBorderLayout());
		grammarLabel = new JLabel("Grammar:");
		
		grammarPanel.add(grammarLabel, "North");
		grammarPanel.add(scrollPane, "Center");
		
		SwingWorker grammarSpecLoader = new SwingWorker() {
			@Override
			public Object construct() {
				try {
					InputStream is = getClass().getResourceAsStream(filePath);
					
					grammarText.read(is, "UTF-8");
				} catch (IOException e) {
					e.printStackTrace();
					
					String errorText = String.format(
							"Error loading grammar at\n" +
							"\t'%1$s'\n" +
							"\t(URL='%2$s')\n" +
							"Exception: %3$s",
							filePath, grammarPath, e.getLocalizedMessage());
					
					grammarText.setText(errorText);
				}
				return null;
			}
		};
		
		grammarSpecLoader.start();
	}
	
	private void createSourceInputPanel() {
		sourceText = createTextPane();
		
		JScrollPane scrollPane = addToScrollPane(sourceText);
		
		sourcePanel = new JPanel(createBorderLayout());
		sourceLabel = new JLabel("Source Code:");
		
		sourcePanel.add(sourceLabel, "North");
		sourcePanel.add(scrollPane, "Center");
	}
	
	private void createStatusPanel() {
		statusLabel = new JLabel("Status:");
		statusPanel = new JPanel(createBorderLayout());
		
		verboseCheckBox = new JCheckBox("Verbose", true);
		statusText = createTextPane();
		
		statusText.setEditable(false);
		
		JScrollPane scrollPane = addToScrollPane(statusText);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		
		topPanel.add(statusLabel, "West");
		topPanel.add(verboseCheckBox, "East");
		
		statusPanel.add(topPanel, "North");
		statusPanel.add(scrollPane, "Center");
	}
	
	private void createButtonPanel() {
		buttonPanel = new JPanel(new FlowLayout());
		
		compileButton = createButton("Compile");
		saveButton = createButton("Save source code");
		saveAsButton = createButton("Save source code as...");
		loadButton = createButton("Load source code");
		quitButton = createButton("Exit");
		
		buttonPanel.add(compileButton);
		buttonPanel.add(saveButton);
		buttonPanel.add(saveAsButton);
		buttonPanel.add(loadButton);
		buttonPanel.add(quitButton);
	}
	
	private JTextPane createTextPane() {
		JTextPane text = new JTextPane();
		
		text.setContentType("text/plain");
		text.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		return text;
	}
	
	private JScrollPane addToScrollPane(Component view) {
		return new JScrollPane(view);
	}
	
	private JButton createButton(String text) {
		JButton btn = new JButton();
		
		btn.setText(text);
		btn.addActionListener(this);
		
		return btn;
	}
	
	private BorderLayout createBorderLayout() {
		return new BorderLayout(2, 2);
	}
	
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src == compileButton) {
			compile();
		}
		else if(src == saveButton) {
			sourceFile = saveSource(sourceFile);
		}
		else if(src == saveAsButton) {
			sourceFile = saveSource(null);
		}
		else if(src == loadButton) {
			loadSource();
		}
		else if(src == quitButton) {
			dispose();
			
			System.exit(0);
		}
	}
	
	private void compile() {
		initCompileWorker();
		
		grammarRules.reset();
		
		statusText.setText(null);
		
		sourceText.setEditable(false);
		compileButton.setEnabled(false);
		
		// do the compilation action in a worker thread
		compileWorker.start();
	}
	
	private File saveSource(File destFile) {
		int result = JFileChooser.CANCEL_OPTION;
		
		if(destFile == null)
			result = fileChooser.showSaveDialog(this);
		
		if(destFile != null ||
				result == JFileChooser.APPROVE_OPTION) {
			
			try {
				destFile = fileChooser.getSelectedFile();
				
				FileWriter fw = new FileWriter(destFile);
				sourceText.write(fw);
				
			} catch(IOException e) {
				e.printStackTrace();
				
				JOptionPane.showMessageDialog(
						this,
						"An exception occured while saving:" +
						e.getLocalizedMessage(),
						"Error while saving file",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
		return destFile;
	}
	
	private void loadSource() {
		int retVal = JFileChooser.CANCEL_OPTION;
		
		retVal = fileChooser.showOpenDialog(this);
		
		if(retVal == JFileChooser.APPROVE_OPTION) {
			sourceFile = fileChooser.getSelectedFile();
			
			try {
				FileReader fr = new FileReader(sourceFile);
				sourceText.read(fr, null);
				
			} catch(IOException e) {
				e.printStackTrace();
				
				JOptionPane.showMessageDialog(
						this,
						"An exception occured while loading:" +
						e.getLocalizedMessage(),
						"Error while loading file",
						JOptionPane.WARNING_MESSAGE);
			}
		}
	}
	
	// status output methods
	
	/**
	 * Returns true of verbose output is enabled, false otherwise.
	 * @return true if verbose output is enabled (the checkbox is indeed
	 * checked), false otherwise.
	 */
	public boolean isVerbose() {
		return verboseCheckBox.isSelected();
	}
	
	/**
	 * Appends the given string to append to the end of the status area if
	 * verbose mode is on.
	 * @param s the string to append
	 */
	public void appendV(String s) {
		if(isVerbose())
			append(s);
	}
	
	/**
	 * Appends a newline to the end of the status area if verbose mode is on.
	 */
	public void appendlnV() {
		if(isVerbose())
			appendln();
	}
	
	/**
	 * Appends the given string followed by a newline to the end of the status
	 * area if verbose mode is on.
	 * @param s the string to append
	 */
	public void appendlnV(String s) {
		if(isVerbose())
			appendln(s);
	}
	
	/**
	 * Appends the given string to the end of the status area.
	 * @param s the string to append
	 */
	public void append(String s) {
		Document doc = statusText.getDocument();
		
		try {
			doc.insertString(doc.getLength(), s, null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a newline to the end of the status area.
	 */
	public void appendln() {
		Document doc = statusText.getDocument();
		
		try {
			doc.insertString(doc.getLength(), "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends the given string followed by a newline to the end of the
	 * status area.
	 * @param s the string to append
	 */
	public void appendln(String s) {
		Document doc = statusText.getDocument();
		
		try {
			doc.insertString(doc.getLength(), s + "\n", null);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Really, really verbose way of getting the path to the grammar spec
	 * for this assignment.
	 * We're assuming that the a plaintext file will contain a human readable
	 * representation of a grammar.  If it's in the same directory as a
	 * class file, use that class file as the first argument (e.g.
	 * RecursiveDescentParserGui.class) and the name of grammar file
	 * as the second.  This will return a string that can be used by
	 * getClass().getResourceAsStream(String) to read a file, and works
	 * for resources within a JAR file.
	 */
	public static<T> String getResourcePath(Class<T> c, String file) {
		String name = '/' +
			c.getPackage().getName().replace('.', '/') + '/' + file;
		
		return name;
	}
}
