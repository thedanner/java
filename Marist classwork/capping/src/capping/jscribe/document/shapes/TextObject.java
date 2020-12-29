package capping.jscribe.document.shapes;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * @author Craig
 *
 */
public class TextObject extends DocumentObject
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5492311841200800112L;
	
	protected Font font;
	protected String text;
	protected boolean underline;
	
	public TextObject(String text, Font font, boolean underline)
	{
		super();
		
		this.text = text;
		this.font = font;
		this.underline = underline;
	}
	
	//paints the text on the document
	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		
		FontMetrics fm = g.getFontMetrics(font);
		
		int textWidth = fm.stringWidth(text);
		int textHeight = fm.getHeight();
		int textDescent = fm.getDescent();
		
		setSize(textWidth + 1, textHeight + textDescent + 1);
		
		g.setColor(getForeground());
		g.setFont(font);
		g.drawString(text, 0, textHeight);
		
		if (underline)
			g.drawLine(0, textHeight, textWidth, textHeight);
	}
	
	//sets the font information
	@Override
	public void setFont(Font font)
	{
		this.font = font;
		
		repaint();
	}
	
	//returns the font information
	@Override
	public Font getFont()
	{
		return font;
	}
}
