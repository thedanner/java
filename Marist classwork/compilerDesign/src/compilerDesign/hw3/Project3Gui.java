/* Project3Gui.java */
package compilerDesign.hw3;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import compilerDesign.hw3.grammar.Grammar3;
import compilerDesign.util.SwingWorker;

/**
 * A graphical frontend to the lexer and parser for the grammar specified in
 * homework project 3.
 * @author Dan Mangiarelli, Dave Weisfelner
 */
public class Project3Gui extends JFrame implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -676588009726831599L;
	
	private static final String APP_TITLE =
		"Homework Project 3 Grammar Parser";
	
	private SwingWorker compileWorker;
	
	private boolean compileAttempted;
	
	private JFileChooser fileChooser;
	private File sourceFile;
	
	private GrammarSpecFrame grammarSpecFrame;
	
	private JLabel sourceLabel;
	private JTextPane sourceText;
	private JPanel sourcePanel;
	
	private JLabel statusLabel;
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
	
	/* variables for compiling multiple files successively */
	private boolean compileMultipleFiles;
	private File[] filesToCompile;
	private int compilationProgressIndex;
	
	public Project3Gui()
	{
		this(null);
	}
	
	public Project3Gui(String grammarPath)
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
		
		createSourceInputPanel();
		createStatusPanel();
		createSourceButtonPanel();
		createActionButtonPanel();
		
		// Put the source and status/"output" text panes in a 2x1 grid.
		JPanel textPanel = new JPanel(new GridLayout(1, 0, 5, 5));
		textPanel.add(sourcePanel);
		textPanel.add(statusPanel);
		
		// Put the buttons in JPanels, and those panels into a 1x2 grid.
		JPanel buttonsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
		buttonsPanel.add(sourceButtonPanel);
		buttonsPanel.add(actionButtonPanel);
		
		Container cp = getContentPane();
		cp.setLayout(new BorderLayout(5, 5));
		
		cp.add(textPanel, "Center");
		cp.add(buttonsPanel, "South");
		
		pack();
		
		setMinimumSize(getSize());
		
		setSize(625, 500);
	}
	
	private void createSourceInputPanel()
	{
		sourceText = createTextPane();
		
		JScrollPane scrollPane = new JScrollPane(sourceText);
		
		sourcePanel = new JPanel(new BorderLayout());
		sourceLabel = new JLabel("Source Code:");
		
		sourcePanel.add(sourceLabel, "North");
		sourcePanel.add(scrollPane, "Center");
	}
	
	private void createStatusPanel()
	{
		statusPanel = new JPanel(new BorderLayout());
		
		statusLabel = new JLabel("Status:");
		
		statusText = createTextPane();
		
		/*
		Style s = statusText.addStyle("Red", null);
		StyleConstants.setForeground(s, Color.RED);
		*/
		
		statusText.setEditable(false);
		
		JScrollPane scrollPane = new JScrollPane(statusText);
		
		JPanel topPanel = new JPanel(new BorderLayout());
		
		topPanel.add(statusLabel, "West");
		
		statusPanel.add(topPanel, "North");
		statusPanel.add(scrollPane, "Center");
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
		
		compileButton = createButton("Compile");
		showGrammarSpecButton = createButton("Show grammar spec");
		quitButton = createButton("Exit");
		
		if(grammarSpecFrame == null)
			showGrammarSpecButton.setEnabled(false);
		
		actionButtonPanel.add(compileButton);
		actionButtonPanel.add(showGrammarSpecButton);
		actionButtonPanel.add(quitButton);
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
			 * When the SwingWorker object is run, this method gets called,
			 * so doCompile gets called in turn and will perform the actual
			 * compilation.
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
	private void compileActonPerformed()
	{
		initCompileWorker();
		
		statusText.setText(null);
		Grammar3.resetOutput();
		
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
				Grammar3.ReInit(srcReader);
			else
				new Grammar3(srcReader);
			
			compileAttempted = true;
			
			// the call to Input() will throw an exception
			// if an error occurs while parsing
			Grammar3.Input();
			
			appendln(Grammar3.getOutput());
			appendln("Parse completed successfully.");
			
			// everything went well, so make sure the text color is black
			statusText.setForeground(Color.BLACK);
		}
		catch (Throwable e)
		{
			//StyledDocument doc = statusText.getStyledDocument();
			
			e.printStackTrace();
			
			//int startPos = doc.getLength();
			
			appendln();
			
			appendln("Error while parsing:");
			appendln(e.toString());
			
			//int len = doc.getLength() - startPos;
			
			//doc.setCharacterAttributes(
			//		startPos, len, statusText.getStyle("Red"), true);
			
			// set the text color to red to indicate an error
			statusText.setForeground(Color.RED);
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
		
		compileActonPerformed();
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
			
			StringBuilder text = new StringBuilder("Compile");
			
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
			compileButton.setText("Compile");
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
		Document doc = statusText.getDocument();
		
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
	public void appendln()
	{
		append(Grammar3.NEWLINE);
	}
	
	/**
	 * Appends the given string followed by a newline to the end of the
	 * status area.
	 * @param s the string to append
	 */
	public void appendln(String s)
	{
		append(s + Grammar3.NEWLINE);
	}
	
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if (src == compileButton)
		{
			compileActonPerformed();
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
}
