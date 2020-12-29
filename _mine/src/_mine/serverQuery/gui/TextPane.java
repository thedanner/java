/* TextPane.java */
package _mine.serverQuery.gui;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import _mine.serverQuery.ServerQuery;

/**
 * 
 * @author Dan
 * @version Feb 11, 2006
 */
public class TextPane extends JFrame {
	private static final long serialVersionUID = 3726537123304887378L;

	/*  */
	private static TextPane instance;
	
	/*  */
	private JEditorPane html;
	
	/*  */
	private JViewport vp;
	
	/**
	 * 
	 *
	 */
	public TextPane(ServerQuery parent) {
		super();
		
		getContentPane().setLayout(new BorderLayout());
		
		setSize(550, 500);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent evt) {
				onCloseAction();
			}
		});
		
		html = new JEditorPane(); 
		html.setEditable(false); 
		html.addHyperlinkListener(createHyperLinkListener(parent)); 
		html.setContentType("text/html");
		
		JScrollPane scroller = new JScrollPane(); 
		vp = scroller.getViewport();
		vp.add(html); 
		getContentPane().add(scroller, BorderLayout.CENTER);
	}
	
	/**
	 * 
	 * @return
	 */
	public HyperlinkListener createHyperLinkListener(final ServerQuery parent) {
		return new HyperlinkListener() {
			@Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (e instanceof HTMLFrameHyperlinkEvent) {
						((HTMLDocument)html.getDocument()).processHTMLFrameHyperlinkEvent(
								(HTMLFrameHyperlinkEvent)e);
					} else {
						String url = e.getURL().toString();
						parent.launchURL(url);
					}
				}
			}
		};
	}
	
	/**
	 * 
	 * @param url
	 */
	public void setPage(URL url) {
		try {
			html.setPage(url);
			setVisible(true);
		} catch(IOException e) {
			e.printStackTrace();
			return;
		}
	}
	
	/**
	 * 
	 * @param s
	 */
	public void setText(String s) {
		html.setText(s);
		setVisible(true);
	}
	
	private void onCloseAction() {
		setVisible(false);
	}
	
	/**
	 * 
	 * @param parent TODO
	 * @param urlPath
	 * @param title
	 */
	public static void showPage(ServerQuery parent,
			String urlPath, String title) {
		URL url = null;
		try {
			url = new URL(urlPath);
		} catch(MalformedURLException e) {
			e.printStackTrace();
			return;
		}
		showPage(parent, url, title);
	}
	
	/**
	 * 
	 * @param parent TODO
	 * @param s
	 */
	public static void showText(ServerQuery parent, String s, String title) {
		TextPane text = getInstance(parent);
		text.setTitle(title);
		text.setText(s);
	}
	
	public static void showPage(ServerQuery parent, URL url, String title) {
		TextPane text = getInstance(parent);
		text.setTitle(title);
		text.setPage(url);
	}
	
	/**
	 * 
	 * @param parent
	 * @return
	 */
	public static TextPane getInstance(ServerQuery parent) {
		if(instance == null)
			instance = new TextPane(parent);
		return instance;
	}
}
