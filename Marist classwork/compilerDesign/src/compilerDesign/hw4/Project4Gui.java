/* Project4Gui.java */
package compilerDesign.hw4;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import compilerDesign.hw4.grammar.Grammar4;
import compilerDesign.hw4.grammar.Message;
import compilerDesign.util.SwingWorker;

/**
 * A graphical frontend to the lexer and parser for the grammar specified in
 * homework project 4.
 * @author Dan Mangiarelli, Dave Weisfelner
 */
public class Project4Gui extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -676588009726831599L;
	
	private static final String APP_TITLE =
		"Homework Project 4 Grammar Parser";
	
	private SwingWorker compileWorker;
	
	private boolean compileAttempted;
	
	private JFileChooser fileChooser;
	private File sourceFile;
	
	private GrammarSpecFrame grammarSpecFrame;
	
	private JLabel sourceLabel;
	private JLabel statusLabel;
	private JCheckBox verbose;
	private JLabel mooLabel;
	private JCheckBox mooMode;
	private JPanel labelPanel;
	
	private JTextPane sourceText;
	private JPanel sourcePanel;
	
	private JTextPane statusText;
	private JPanel statusPanel;
	
	private JButton saveButton;
	private JButton saveAsButton;
	private JButton loadButton;
	private JPanel sourceButtonPanel;
	
	private JButton compileButton;
	private JButton showGrammarSpecButton;
	private JButton quitButton;
	private JPanel actionButtonPanel;
	
	private JTextPane mooStatusText;
	private JPanel mooPanel;
	
	/* variables for compiling multiple files successively */
	private boolean compileMultipleFiles;
	private File[] filesToCompile;
	private int compilationProgressIndex;
	
	public Project4Gui()
	{
		this(null);
	}
	
	public Project4Gui(String grammarPath)
	{
		super(APP_TITLE);
		
		compileAttempted = false;
		
		fileChooser = new JFileChooser();
		
		sourceFile = null;
		
		if(grammarPath != null)
			grammarSpecFrame = new GrammarSpecFrame(grammarPath);
		else
			grammarSpecFrame = null;
		
		compileMultipleFiles = false;
		filesToCompile = null;
		compilationProgressIndex = -1;
		
		initGui();
	}
	
	private void initGui()
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		createLabelPanel();
		
		createSourceInputPanel();
		createStatusPanel();
		createSourceButtonPanel();
		createActionButtonPanel();
		createMooPanel();
		
		createStyles();
		createActions();
		
		sourceText.requestFocusInWindow();
		
		// Put the source and status/"output" text panes in a grid.
		JPanel textPanels = new JPanel(new GridLayout(1, 0, 5, 5));
		textPanels.add(sourcePanel);
		textPanels.add(statusPanel);
		textPanels.add(mooPanel);
		
		// Put the buttons in JPanels, and those panels into a grid.
		JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		buttonsPanel.add(sourceButtonPanel);
		buttonsPanel.add(actionButtonPanel);
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(5, 5));
		
		cp.add(labelPanel, "North");
		cp.add(textPanels, "Center");
		cp.add(buttonsPanel, "South");
		
		pack();
		
		setMinimumSize(getSize());
		
		setSize(800, 500);
	}
	
	private void createLabelPanel()
	{
		sourceLabel = new JLabel("Source Code:", JLabel.CENTER);
		
		statusLabel = new JLabel("Status:", JLabel.CENTER);
		verbose = new JCheckBox("Verbose", true);
		
		mooLabel = new JLabel("Symbol table:", JLabel.CENTER);
		mooMode = new JCheckBox("Moo Mode", false);
		
		JPanel sourceLabelPanel = new JPanel(new BorderLayout(10, 0));
		JPanel statusLabelPanel = new JPanel(new BorderLayout(10, 0));
		JPanel mooLabelPanel = new JPanel(new BorderLayout(10, 0));
		
		sourceLabelPanel.add(sourceLabel, "Center");
		
		statusLabelPanel.add(statusLabel, "Center");
		statusLabelPanel.add(verbose, "East");
		
		mooLabelPanel.add(mooLabel, "Center");
		mooLabelPanel.add(mooMode, "East");
		
		labelPanel = new JPanel(new GridLayout(1, 0, 10, 2));
		
		labelPanel.add(sourceLabelPanel);
		labelPanel.add(statusLabelPanel);
		labelPanel.add(mooLabelPanel);
	}
	
	private void createSourceInputPanel()
	{
		sourceText = createTextPane();
		
		JScrollPane scrollPane = new JScrollPane(sourceText);
		
		sourcePanel = new JPanel(new BorderLayout());
		
		sourcePanel.add(scrollPane);
	}
	
	private void createStatusPanel()
	{
		statusText = createTextPane();
		statusText.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(statusText);
		
		statusPanel = new JPanel(new BorderLayout());
		
		statusPanel.add(scrollPane);
	}
	
	private void createMooPanel()
	{
		mooStatusText = createTextPane();
		mooStatusText.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(mooStatusText);
		
		mooPanel = new JPanel(new BorderLayout());
		
		mooPanel.add(scrollPane);
	}
	
	private void createSourceButtonPanel()
	{
		sourceButtonPanel = new JPanel(new GridLayout(1, 0, 2, 2));
		
		saveButton = createButton("Save source code");
		saveAsButton = createButton("Save source code as...");
		loadButton = createButton("Load or Compile multiple");
		
		sourceButtonPanel.add(saveButton);
		sourceButtonPanel.add(saveAsButton);
		sourceButtonPanel.add(loadButton);
	}
	
	private void createActionButtonPanel()
	{
		actionButtonPanel = new JPanel(new GridLayout(1, 0, 2, 2));
		
		compileButton = createButton("Compile [F5]");
		showGrammarSpecButton = createButton("Show grammar spec");
		quitButton = createButton("Exit");
		
		if(grammarSpecFrame == null)
			showGrammarSpecButton.setEnabled(false);
		
		actionButtonPanel.add(compileButton);
		actionButtonPanel.add(showGrammarSpecButton);
		actionButtonPanel.add(quitButton);
	}
	
	private void createStyles()
	{
		//add styles
		Style s = statusText.addStyle("Verbose", null);
		StyleConstants.setForeground(s, Color.BLUE);
		
		s = statusText.addStyle("Error", null);
		StyleConstants.setForeground(s, Color.RED);
		
		s = statusText.addStyle("Success", null); // mwahaha
		StyleConstants.setForeground(s, new Color(0, 128, 0));
		
		s = mooStatusText.addStyle("Symbol table", null);
		StyleConstants.setForeground(s, new Color(0, 128, 0));
	}
	
	private void createActions()
	{
		KeyStroke key = KeyStroke.getKeyStroke(
				KeyEvent.VK_F5, 0);
		
		Action c = new CompileAction();
		
		InputMap map = sourceText.getInputMap(
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		
		map.put(key, c);
		sourceText.getActionMap().put(c.getValue(Action.NAME), c);
	}
	
	private JTextPane createTextPane()
	{
		JTextPane text = new JTextPane();
		
		text.setContentType("text/plain");
		text.setFont(new Font("Monospaced", Font.PLAIN, 12));
		
		return text;
	}
	
	private JButton createButton(String text)
	{
		JButton btn = new JButton();
		
		btn.setText(text);
		btn.addActionListener(this);
		
		return btn;
	}
	
	private void initCompileWorker()
	{
		compileWorker = new SwingWorker()
		{
			@Override
			/**
			 * When the SwingWorker object is start()ed, this method gets
			 * called, so doCompile gets called in turn and will perform
			 * the actual compilation.
			 */
			public Object construct()
			{
				doCompile();
				
				return null;
			}
			
			@Override
			/**
			 * This will be performed on completion of the compilation phase.
			 */
			public void finished()
			{
				sourceText.setEditable(true);
				compileButton.setEnabled(true);
			}
		};
	}
	
	/**
	 * This method gets called when an event is fired by pressing the Compile
	 * button.
	 */
	private void beginCompilation()
	{
		initCompileWorker();
		
		statusText.setText(null);
		mooStatusText.setText(null);
		
		Grammar4.reset();
		
		sourceText.setEditable(false);
		compileButton.setEnabled(false);
		
		if (compileMultipleFiles &&
				compilationProgressIndex != filesToCompile.length)
		{
			// load the next source file to compile
			loadSingleSourceFile(filesToCompile[compilationProgressIndex]);
		}
		
		// do the compilation action in a worker thread
		compileWorker.start();
		
		// setup for the next file to be compiled
		compilationProgressIndex++;
		
		if (filesToCompile != null &&
				compilationProgressIndex == filesToCompile.length)
		{
			setCompileMultipleFiles(false);
		}
		
		updateCompileButtonText();
	}
	
	/**
	 * Begin the compilation process.
	 */
	private void doCompile()
	{
		try
		{
			String src = sourceText.getText();
			StringReader srcReader = new StringReader(src);
			
			if(compileAttempted)
				Grammar4.ReInit(srcReader);
			else
				new Grammar4(srcReader);
			
			compileAttempted = true;
			
			statusText.setForeground(Color.BLACK);
			
			// the call to Input() will throw an exception (error?)
			// if an error occurs while parsing
			Grammar4.Input();
			
			printOutput(Grammar4.getOutput());
		}
		catch (Throwable e)
		{
			printOutput(Grammar4.getOutput(), e);
		}
	}
	
	private void printOutput(Message[] output)
	{
		printOutput(output, null);
	}
	
	private void printOutput(Message[] output, Throwable e)
	{
		StyledDocument doc = statusText.getStyledDocument();
		
		if(e != null)
		{
			int startPos = doc.getLength();
			
			appendln("Error encountered during compilation.");
			appendln();
			
			int len = doc.getLength() - startPos;
			
			doc.setCharacterAttributes(
					startPos, len, statusText.getStyle("Error"), true);
		}
		
		for(Message m : output)
		{
			if(!m.isVerbose())
			{
				appendln(m.getMessage());
			}
			
			// else, the message is verbose output,
			// so display if if the user wants
			else if(isVerbose())
			{
				int startPos = doc.getLength();
				
				append("-");
				appendln(m.getMessage());
				
				int len = doc.getLength() - startPos;
				
				// color the verbose text
				doc.setCharacterAttributes(
						startPos, len,
						statusText.getStyle("Verbose"), true);
			}
		}
		
		if(e == null)
		{
			doc = statusText.getStyledDocument();
			
			int startPos = doc.getLength();
			
			appendln();
			appendln("Parse completed successfully.");
			
			int len = doc.getLength() - startPos;
			
			// color the verbose text
			doc.setCharacterAttributes(
					startPos, len,
					statusText.getStyle("Success"), true);
			
			if(isMooMode())
			{
				for(int i = 0; i < 1000; i++)
				{
					appendln("The meaning of life", mooStatusText);
					appendln("  is to find the meaning of life", mooStatusText);
					
					appendln(mooStatusText);
					
					appendln("ERROR: right hand recursion.", mooStatusText);
					
					appendln(mooStatusText);
				}
			}
			
			StyledDocument symDoc = mooStatusText.getStyledDocument();
			
			startPos = symDoc.getLength();
			
			appendln(Grammar4.getSymbolTableText(), mooStatusText);
			
			len = symDoc.getLength() - startPos;
			
			// color the moo mode text
			symDoc.setCharacterAttributes(
					startPos, len,
					mooStatusText.getStyle("Symbol table"), true);
		}
		else // an exception occured
		{
			int startPos = doc.getLength();
			
			appendln();
			
			appendln("Error while parsing:");
			appendln(e.toString());
			
			int len = doc.getLength() - startPos;
			
			doc.setCharacterAttributes(
					startPos, len, statusText.getStyle("Error"), true);
		}
	}
	
	private File saveSource(File destFile)
	{
		int result = 0;
		
		fileChooser.setMultiSelectionEnabled(false);
		
		if (destFile == null)
			result = fileChooser.showSaveDialog(this);
		
		if (destFile != null || result == JFileChooser.APPROVE_OPTION)
		{
			try
			{
				destFile = fileChooser.getSelectedFile();
				
				FileWriter fw = new FileWriter(destFile);
				sourceText.write(fw);
				
				setTitle(APP_TITLE + " - " + destFile.getName());
			}
			catch (IOException e)
			{
				e.printStackTrace();
				
				JOptionPane.showMessageDialog(
						this,
						"An error occured while saving:\n"
						+ e.getLocalizedMessage(),
						"Error while saving file",
						JOptionPane.WARNING_MESSAGE);
			}
		}
		
		return destFile;
	}
	
	private void loadSource()
	{
		fileChooser.setMultiSelectionEnabled(true);
		
		int retVal = fileChooser.showOpenDialog(this);
		
		if (retVal == JFileChooser.APPROVE_OPTION)
		{
			File[] selectedFiles = fileChooser.getSelectedFiles();
			
			if (selectedFiles.length == 1)
			{
				// if only one file was chosen, simply load that file
				loadSingleSourceFile(selectedFiles[0]);
			}
			else
			{
				// otherwise, setup some variables to allow
				// multiple files to be compiled sequentially
				compileMultipleFiles(selectedFiles);
			}
		}
	}
	
	private void loadSingleSourceFile(File sourceFile)
	{
		try
		{
			FileReader fr = new FileReader(sourceFile);
			sourceText.read(fr, null);
			
			setTitle(APP_TITLE + " - " + sourceFile.getName());
		}
		catch (IOException e)
		{
			e.printStackTrace();
			
			JOptionPane.showMessageDialog(
					this,
					"An error occured while loading:\n"
					+ e.getLocalizedMessage(),
					"Error while loading file",
					JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void compileMultipleFiles(File[] sourceFiles)
	{
		filesToCompile = sourceFiles;
		
		setCompileMultipleFiles(true);
		
		beginCompilation();
	}
	
	private void setCompileMultipleFiles(boolean s)
	{
		compileMultipleFiles = s;
		
		if (s)
		{
			// either initialize
			compilationProgressIndex = 0;
		}
		else
		{
			// ...or discard variables associated
			// with multiple file compilation
			filesToCompile = null;
			compilationProgressIndex = -1;
		}
		
		updateCompileButtonText();
	}
	
	private void updateCompileButtonText()
	{
		if (compileMultipleFiles)
		{
			int numFiles = filesToCompile.length;
			int index = compilationProgressIndex;
			
			StringBuilder text = new StringBuilder("Compile [F5]");
			
			StringBuilder toolTip = new StringBuilder();
			
			toolTip
			.append("Next file: ")
			.append(filesToCompile[index].toString());
			
			if (index != numFiles)
			{
				text
				.append(" next (#")
				.append(index + 1)
				.append(" of ")
				.append(numFiles)
				.append(")");
			}
			
			compileButton.setText(text.toString());
			compileButton.setToolTipText(toolTip.toString());
		}
		else
		{
			compileButton.setText("Compile [F5]");
			compileButton.setToolTipText(null);
		}
	}
	
	private void showGrammarSpec()
	{
		grammarSpecFrame.setVisible(true);
	}
	
	/**
	 * Appends the given string to the end of the status area.
	 * @param s the string to append
	 */
	public void append(String s)
	{
		append(s, statusText);
	}
	
	/**
	 * Appends a newline to the end of the status area.
	 */
	public void appendln()
	{
		append(Grammar4.NEWLINE);
	}
	
	/**
	 * Appends the given string followed by a newline to the end of the
	 * status area.
	 * @param s the string to append
	 */
	public void appendln(String s)
	{
		append(s + "\n");
	}
	
	/**
	 * Appends the given string to the end of the status area.
	 * @param s the string to append
	 */
	public void append(String s, JTextPane target)
	{
		Document doc = target.getDocument();
		
		try
		{
			doc.insertString(doc.getLength(), s, null);
		}
		catch (BadLocationException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Appends a newline to the end of the status area.
	 */
	public void appendln(JTextPane target)
	{
		append(Grammar4.NEWLINE, target);
	}
	
	/**
	 * Appends the given string followed by a newline to the end of the
	 * status area.
	 * @param s the string to append
	 */
	public void appendln(String s, JTextPane target)
	{
		append(s + "\n", target);
	}
	
	private boolean isVerbose()
	{
		return verbose.isSelected();
	}
	
	private boolean isMooMode()
	{
		return mooMode.isSelected();
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if (src == compileButton)
		{
			beginCompilation();
		}
		else if (src == showGrammarSpecButton)
		{
			showGrammarSpec();
		}
		else if (src == quitButton)
		{
			dispose();
			
			System.exit(0);
		}
		else if (src == saveButton)
		{
			sourceFile = saveSource(sourceFile);
		}
		else if (src == saveAsButton)
		{
			sourceFile = saveSource(null);
		}
		else if (src == loadButton)
		{
			loadSource();
		}
	}
	
	// Actions for input map
	class CompileAction extends AbstractAction
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = 6387984540934184771L;
		
		public CompileAction()
		{
			super("Compile");
		}
		
		public void actionPerformed(ActionEvent e)
		{
			compileButton.doClick();
		}
	}
}
