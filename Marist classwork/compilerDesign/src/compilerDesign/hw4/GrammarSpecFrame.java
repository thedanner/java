/* GrammarSpecFrame.java */
package compilerDesign.hw4;

import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import compilerDesign.util.SwingWorker;

/**
 * A separate JFrame for showing plain text from a file.  The file is read
 * from the file system or a JAR file. 
 * @author Dan Mangiarelli, Dave Weisfelner
 */
public class GrammarSpecFrame extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4776737837346053441L;
	
	private JTextPane grammarText;
	
	public GrammarSpecFrame(String grammarPath)
	{
		super("Homework Project 4 Grammar Spec");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		createLanguageSpecPanel(grammarPath);
		
		pack();
	}
	
	private void createLanguageSpecPanel(final String filePath) {
		grammarText = createTextPane();
		
		grammarText.setEditable(false);
		grammarText.setText("Loading grammar spec...");

		JScrollPane scrollPane = new JScrollPane(grammarText);
		getContentPane().add(scrollPane, "Center");
		
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
							filePath, filePath, e.getLocalizedMessage());
					
					grammarText.setText(errorText);
				}
				return null;
			}
			
			@Override
			public void finished() {
				pack();
			}
		};
		
		grammarSpecLoader.start();
	}
	
	private JTextPane createTextPane() {
		JTextPane text = new JTextPane();
		
		text.setContentType("text/plain");
		text.setFont(new Font("Monospaced", Font.PLAIN, 12));
		text.setEditable(false);
		
		return text;
	}
}
