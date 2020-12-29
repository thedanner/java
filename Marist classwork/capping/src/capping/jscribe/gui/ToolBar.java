/**
 * 
 */
package capping.jscribe.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileFilter;

import capping.jscribe.document.shapes.DocumentObject;
import capping.jscribe.document.shapes.Image;
import capping.jscribe.document.shapes.Line;
import capping.jscribe.document.shapes.OutlinedOval;
import capping.jscribe.document.shapes.OutlinedRectangle;
import capping.jscribe.document.shapes.TextObject;
import capping.jscribe.gui.resources.ResourceUtil;

/**
 * @author Craig 
 *
 */
public class ToolBar extends JToolBar implements ActionListener
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5240008943164687427L;
	
	private DocumentFrame docFrame;
	
	private FontChooser fontChooser;
	
	private JFileChooser imageChooser;
	
	private JToggleButton circle;
	private JToggleButton square;
	private JToggleButton ellipse;
	private JToggleButton rectangle;
	private JToggleButton line;
	private JButton text;
	private JButton image;
	private JToggleButton group;
	
	private JButton shapeForegroundColor;
	private JButton shapeBackgroundColor;
	private JButton swapColors;
	
	private ImageIcon circleIcon;
	private ImageIcon squareIcon;
	private ImageIcon ellipseIcon;
	private ImageIcon rectangleIcon;
	private ImageIcon lineIcon;
	
	public ToolBar(DocumentFrame docFrame)
	{
		super();
		
		this.docFrame = docFrame;
		
		fontChooser = new FontChooser();
		
		imageChooser = new JFileChooser();
		imageChooser.setFileFilter(new ImageFileFilter());
		
		initImageIcons();
		
		initButtons();
		
		addButtons();
		
		ButtonGroup buttonGroup = new ButtonGroup();
		
		buttonGroup.add(circle); //need circle and squares with shift key
		buttonGroup.add(square);
		buttonGroup.add(ellipse);
		buttonGroup.add(rectangle);
		buttonGroup.add(line);
		
		// default to a line
		line.doClick();
	}
	
	//initialize the image icons in the toolbar
	private void initImageIcons()
	{
		try
		{
			ellipseIcon = new ImageIcon(
					ResourceUtil.getImageData(
							"ellipse.png"),
					"ellipse icon");
			
			rectangleIcon = new ImageIcon(
					ResourceUtil.getImageData(
							"rectangle.png"),
					"rectangle icon");
			
			lineIcon = new ImageIcon(
					ResourceUtil.getImageData(
							"line.png"),
					"line icon");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	//create the buttons for the toolbar
	//and sets the color in the foreground and background
	private void initButtons()
	{
		circle = new JToggleButton(circleIcon);
		circle.addActionListener(this);
		circle.setToolTipText("Circle");
		
		square = new JToggleButton(squareIcon);
		square.addActionListener(this);
		square.setToolTipText("Square");
		
		ellipse = new JToggleButton(ellipseIcon);
		ellipse.addActionListener(this);
		ellipse.setToolTipText("Ellipse");
		
		rectangle = new JToggleButton(rectangleIcon);
		rectangle.addActionListener(this);
		rectangle.setToolTipText("Rectangle");
		
		line = new JToggleButton(lineIcon);
		line.addActionListener(this);
		line.setToolTipText("Line");
		
		text = new JButton("Text Box");
		text.addActionListener(this);
		
		image = new JButton("Insert Image");
		image.addActionListener(this);
		
		group = new JToggleButton("Group");
		group.addActionListener(this);
		
		shapeForegroundColor = new JButton("Foreground Color");
		shapeForegroundColor.addActionListener(this);
		shapeForegroundColor.setBackground(Color.RED);
		shapeForegroundColor.setForeground(contrastColor(Color.RED));
		
		shapeBackgroundColor = new JButton("Background Color");
		shapeBackgroundColor.addActionListener(this);
		shapeBackgroundColor.setBackground(Color.BLUE);
		shapeBackgroundColor.setForeground(contrastColor(Color.BLUE));
		
		swapColors = new JButton("Swap colors");
		swapColors.addActionListener(this);
	}
	
	//add the buttons to the toolbar
	private void addButtons()
	{
		add(ellipse);
		add(rectangle);
		add(line);
		add(text);
		add(image);
		add(group);
		
		addSeparator();
		
		//add(textOptions);
		add(shapeForegroundColor);
		add(shapeBackgroundColor);
		add(swapColors);
	}
	
	//action performed method for buttons in toolbar
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object src = e.getSource();
		
		if(src == ellipse)
		{
			docFrame.setNextObject(new OutlinedOval());
		}
		
		else if(src == rectangle)
		{
			docFrame.setNextObject(new OutlinedRectangle());	
		}
		
		else if(src == line)
		{
			docFrame.setNextObject(new Line());
		}
		
		else if(src == text)
		{
			fontChooser.showDialog();
			Font font = fontChooser.getSelectedFont();
			
			if(font != null)
			{
				String textStr = fontChooser.getEnteredText();			
				docFrame.setNextObject(
						new TextObject(textStr, font,
								fontChooser.isUnderlined()));
			}
			
		}
		
		else if(src == image)
		{
			int result = imageChooser.showOpenDialog(docFrame);
			
			if(result == JFileChooser.APPROVE_OPTION)
			{
				File selectedFile = imageChooser.getSelectedFile();
				
				docFrame.setNextObject(new Image(selectedFile));
			}
		}
		
		else if(src == group)
		{
			docFrame.toggleGroup();
		}
		
		else if(src == shapeForegroundColor)
		{
			Color prevColor = docFrame.getShapeProperties().getForeground();
			
			Color c = JColorChooser.showDialog(this,
					"JScribe Color Chooser", prevColor);
			
			if(c == null)
			{
				shapeForegroundColor.setBackground(prevColor);
				docFrame.getShapeProperties().setForeground(prevColor);
			}
			else
			{
				shapeForegroundColor.setBackground(c);
				shapeForegroundColor.setForeground(contrastColor(c));
				
				docFrame.getShapeProperties().setForeground(c);
				
				for (int i = 0; i < docFrame.getSelectedObjects().length; i ++)
					docFrame.getSelectedObjects()[i].setForeground(c);
			}
		}
		
		else if(src == shapeBackgroundColor)
		{
			Color prevColor = docFrame.getShapeProperties().getBackground();
			
			Color c = JColorChooser.showDialog(this,
					"JScribe Color Chooser", prevColor);
			
			if(c == null)
			{
				shapeBackgroundColor.setBackground(prevColor);
				docFrame.getShapeProperties().setBackground(prevColor);
			}
			else
			{
				shapeBackgroundColor.setBackground(c);
				shapeBackgroundColor.setForeground(contrastColor(c));
				
				docFrame.getShapeProperties().setBackground(c);
				
				for (int i = 0; i < docFrame.getSelectedObjects().length; i ++)
					docFrame.getSelectedObjects()[i].setBackground(c);
			}
		}
		
		else if(src == swapColors)
		{
			//i made it so all swap colors does is switch each selected
			//object's foreground and background...
			//see if you like it the original code for swap is commented
			//out above
			for(DocumentObject obj : docFrame.getSelectedObjects())
			{
				Color newBG = obj.getForeground();
				Color newFG = obj.getBackground();
				
				if(newFG != null && newBG != null)
				{
					obj.setBackground(newBG);
					obj.setForeground(newFG);
					
					setShapeForegroundButtonColor(newFG);
					setShapeBackgroundButtonColor(newBG);
				}
			}
		}
	}
	
	//sets the selected group
	public void setGroupSelected(boolean selected)
	{
		group.setSelected(selected);
	}
	
	public Color contrastColor(Color c)
	{
		//each RGB has different brightness effects so give them a factor
		if (c.getRed()*2 + c.getGreen()*3 + c.getBlue() < 1000)
			return Color.WHITE;
		else
			return Color.DARK_GRAY;
	}
	
	//sets the shapes foreground color
	public void setShapeForegroundButtonColor(Color c)
	{
		shapeForegroundColor.setBackground(c);
		shapeForegroundColor.setForeground(contrastColor(c));
		
		docFrame.getShapeProperties().setForeground(c);
	}
	
	//sets the shapes background color
	public void setShapeBackgroundButtonColor(Color c)
	{
		shapeBackgroundColor.setBackground(c);
		shapeBackgroundColor.setForeground(contrastColor(c));
		
		docFrame.getShapeProperties().setBackground(c);
	}
}

class ImageFileFilter extends FileFilter
{
	@Override
	public boolean accept(File pathname)
	{
		if(pathname.isDirectory())
			return true;
		
		String path = pathname.getName().toLowerCase();
		
		if(path.endsWith(".jpg") ||
				path.endsWith(".gif") ||
				path.endsWith(".png"))
			return true;
		
		return false;
	}
	
	@Override
	public String getDescription()
	{
		return "Image files (*.jpg, *.gif, *.png)";
	}
}
