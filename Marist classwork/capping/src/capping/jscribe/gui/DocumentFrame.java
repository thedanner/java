package capping.jscribe.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import capping.jscribe.JScribeMain;
import capping.jscribe.document.Document;
import capping.jscribe.document.Sheet;
import capping.jscribe.document.shapes.DocumentObject;
import capping.jscribe.document.shapes.DocumentObjectSelection;
import capping.jscribe.document.shapes.Group;
import capping.jscribe.document.shapes.Image;
import capping.jscribe.document.shapes.Shape;

public class DocumentFrame extends JFrame
implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1228075774505302828L;
	
	//Sets head title for sheets and program window.
	private static final String DEFAULT_SHEET_TITLE = "Sheet";
	private static final String DEFAULT_FRAME_TITLE =
		JScribeMain.APP_NAME + " (v. " + JScribeMain.APP_VER + ")";
	
	//Limits number of sheets to 100.
	private static final int MAX_SHEET_COUNT = 100;
	
	private static final int PASTED_OBJECT_OFFSET = 10;
	
	private static Clipboard clipboard = new Clipboard("JScribe clipboard");
	
	private DocumentObject nextObject;
	
	private GuiWindowAdapter windowListener;
	
	private DocumentEditorPane editor;
	
	private MenuBar menuBar;
	
	private ToolBar toolBar;
	
	private JTabbedPane tabbedPane;
	
	private JLabel statusLabel;
	
	private String documentTitle;
	private boolean dirty;
	
	private JFileChooser fileChooser;
	
	private ShapeProperties shapeProperties;
	
	private Document doc;
	private File file;
	
	private int tabCount;
	
	private DragMover dragMover;
	
	private CursorPositionListener cursorPosition;
	
	private SelectionListener selectionListener;
	/*
	 * Creates new DocumentFrame, then calls init() to add the 
	 * appropriate tools and characters to it.
	 */
	DocumentFrame()
	{
		super();
		
		init();
	}
	
	/*
	 * Initializes a new document, then calls initGui() to
	 * add the GUI features, such as buttons, the toolbar,
	 * and sheets.
	 */
	private void init()
	{
		dirty = false;
		
		fileChooser = new JFileChooser();
		
		shapeProperties = new ShapeProperties();
		
		doc = new Document();
		file = null;
		
		tabCount = 1;
		
		dragMover = new DragMover(this);
		
		selectionListener = new SelectionListener();

		initGui();
	}
	
	/*
	 * Creates a new window with a blank sheet.
	 * In the window, GUI features are added such as the menubar,
	 * and the toolbar. 
	 */
	private void initGui()
	{
		toolBar = new ToolBar(this);
		
		tabbedPane = new JTabbedPane();
		
		TabChangeListener tabListener = new TabChangeListener();
		
		tabbedPane.addChangeListener(tabListener);
		tabbedPane.addMouseListener(tabListener);
		
		tabbedPane.addMouseListener(selectionListener);
		
		menuBar = new MenuBar(this);
		setJMenuBar(menuBar);
		
		windowListener = new GuiWindowAdapter(this);
		addWindowListener(windowListener);
		
		statusLabel = new JLabel("Mouse position: ");
		
		cursorPosition = new CursorPositionListener();
		
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);		
		
		setSize(650, 400);
		
		addSizeBounder();
		
		Container cp = getContentPane();
		
		cp.setLayout(new BorderLayout());
		
		cp.add(toolBar, BorderLayout.NORTH);
		cp.add(tabbedPane, BorderLayout.CENTER);
		cp.add(statusLabel, BorderLayout.SOUTH);
		
		openNewTab();
	}
	
	/*
	 * Sets a boundary so that the window cannot become 
	 * smaller thaty a certain size.
	 */
	private void addSizeBounder()
	{
		setMinimumSize(new Dimension(200, 200));
		
		addComponentListener(new SizeBounder());
	}
	
	/*
	 * Method creates a new number sheet and sets it as active.
	 * checks number of sheets opened in one window, 
	 * if the count is equal to or greater than the declared
	 * maximum sheet count, then an error message will be 
	 * displayed.
	 */
	public void openNewTab()
	{
		if(tabbedPane.getComponentCount() >= MAX_SHEET_COUNT)
		{
			JOptionPane.showMessageDialog(this,
					"HOLY SHEET!!!\nSorry, but I think you've had enough " +
					"sheets for one day!",
					"Too many sheets",
					JOptionPane.ERROR_MESSAGE
			);
			
			return;
		}
		
		String title = null;
		
		do
		{
			title = DEFAULT_SHEET_TITLE + tabCount;
			
			tabCount++;
		}
		while(doc.getSheetByName(title) != null);
		
		Sheet sheet = doc.createSheet(title);
		
		doc.setActiveSheet(sheet);
		
		editor = createNewEditor();
		
		JScrollPane sp = createNewTabComponent(editor);
		
		editor.setSheet(sheet);
		
		tabbedPane.addTab(sheet.getTitle(), sp);
		
		tabbedPane.setSelectedIndex(
				tabbedPane.getComponentCount() - 1);
		
		requestFocusInEditor();
	}
		
	/* 
	 * Creates a new pane and sets scrollbars.
	 */
	private JScrollPane createNewTabComponent(DocumentEditorPane editor)
	{
		JScrollPane sp = new JScrollPane(editor,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		return sp;
	}
	
	private DocumentEditorPane createNewEditor()
	{
		editor = new DocumentEditorPane();
		editor.setPreferredSize(DocumentEditorPane.PREFERRED_SIZE);
		
		editor.addMouseListener(selectionListener);
		editor.addMouseMotionListener(cursorPosition);
		
		editor.addKeyListener(selectionListener);
		
		editor.setShapeDrawer(new ShapeDrawer());
		
		return editor;
	}
	
	/*
	 * Method closes the active sheet.
	 */
	public void closeCurrentTab()
	{
		if (tabbedPane.getSelectedComponent() != null)
		{
			doc.removeSheet(tabbedPane.getSelectedIndex());
			tabbedPane.removeTabAt(tabbedPane.getSelectedIndex());
		}
	}
	
	/*
	 * Method removes every tab in a window by running a for loop.
	 */
	public void closeAllTabs()
	{
		for(int i = tabbedPane.getTabCount() - 1; i >= 0; i--)
			tabbedPane.removeTabAt(i);
	}
	
	/*
	 * Method changes the text in the title of the program.
	 */
	public void updateTitleText()
	{
		String newTitle = documentTitle + " - " + DEFAULT_FRAME_TITLE;
		
		if(isDirty())
			newTitle = "*" + newTitle;
		
		setTitle(newTitle);
	}
	
	//Method returns current title of the document
	public String getDocumentTitle()
	{
		return documentTitle;
	}
	
	/*
	 * Method takes a string that becomes the new document title,
	 * then it calls updateTitleText() to set the string as the 
	 * title.
	 */
	public void setDocumentTitle(String documentTitle)
	{
		this.documentTitle = documentTitle;
		
		updateTitleText();
	}
	
	//Method tells if it's dirty or not
	public boolean isDirty()
	{
		return dirty;
	}
	
	public void setDirty(boolean dirty)
	{
		this.dirty = dirty;
		
		updateTitleText();
	}
	
	/*
	 * Method shows dialog for opening a file. When a file is selected,
	 * the method returns that file. If a file is not approved for
	 * selection, null is returned.
	 */
	public File chooseOpenFile()
	{
		int result = fileChooser.showOpenDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION)
			return fileChooser.getSelectedFile();
		
		return null;
	}
	
	/*
	 * Method shows dialog for saving a file. When a file is selected 
	 * to be saved, the method returns that file. If a selected file
	 * is not approved, null is returned. 
	 */
	public File chooseSaveFile()
	{
		int result = fileChooser.showSaveDialog(this);
		
		if(result == JFileChooser.APPROVE_OPTION)
			return fileChooser.getSelectedFile();
		
		return null;		
	}
	
	/*
	 * Method takes variable of type File declared in choosOpenFile()
	 * and sets it as the current JScribe Project.
	 */
	public void openDocument()
	{
		File f = chooseOpenFile();
		
		if(f == null)
			return;
		
		file = f;
		
		try
		{
			doc = Document.open(file);
			
			syncWithDoc();
			
			doc.setAllZOrders();
			
			setDirty(false);
			
			nextObject = null;
			
			setDocumentTitle(file.getName());
			
			// force keyboard focus
			tabbedPane.setSelectedIndex(0);
			
			JScrollPane sp = (JScrollPane) tabbedPane.getSelectedComponent();
			
			editor = (DocumentEditorPane) sp.getViewport().getView();
			
			return;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			Dialogs.showFileNotFoundErrorDialog(this, e);
		}
		catch (IOException e)
		{
			e.printStackTrace();
			Dialogs.showOpenErrorDialog(this, e);
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			Dialogs.showCorrupedFileDialog(this, e);
		}
		
		// if an error occured, clear the file
		file = null;
	}
	
	/*
	 * Method saves current open project to a file the hard disk.
	 * If it's the first time the project is being saved, the 
	 * saveDocumentAs() method is called, returning a file to save
	 * the project.
	 */
	public boolean saveDocument()
	{
		if(file == null)
		{
			saveDocumentAs();
		}
		else
		{
			try
			{
				doc.save(file);
				
				setDirty(false);
				
				setDocumentTitle(file.getName());
				
				return true;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
				Dialogs.showSaveErrorDialog(this, e);
			}
			catch (IOException e)
			{
				e.printStackTrace();
				Dialogs.showSaveErrorDialog(this, e);
			}
		}
		
		return false;
	}
	
	//return true if file saved, false otherwise
	public boolean saveDocumentAs()
	{
		File f = chooseSaveFile();
		
		if(f == null)
			return false;
		
		file = f;
		
		if(file.exists())
		{
			int result = JOptionPane.showConfirmDialog(this,
					"Do you wish to overwrite the file\n\"" +
					file.getAbsolutePath() + "\"?",
					"File already exists.",
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			
			if(result == JOptionPane.CANCEL_OPTION)
				return false;
			
			if(result == JOptionPane.NO_OPTION)
				return saveDocumentAs();
			
			else
				return saveDocument();
		}
		
		else
		{
			saveDocument();
			return true;
		}
	}
	
	//Prints document.
	public void printDocument()
	{
		doc.print();
	}
	
	//Adds object to the active sheet.
	public void addObject(DocumentObject object)
	{
		editor.addObject(object);
	}
	
	//Removes selected object from the active sheet.
	public void removeObject(DocumentObject object)
	{
		editor.removeObject(object);
	}	
	
	public void requestFocusInEditor()
	{
		editor.requestFocusInWindow();
	}
	
	//Sets an object as active.
	public void select(DocumentObject object)
	{
		selectionListener.select(object);
	}
	//deselects the object
	public void deselect(DocumentObject object)
	{
		selectionListener.deselect(object);
	}
	
	//selects all the objects
	public void selectAll()
	{
		deselectAll();
		
		for (int i = 0; i < editor.getAllObjects().length; i++)
			select(editor.getObject(i));
	}
	
	//deselect all the objects
	public void deselectAll()
	{
		selectionListener.deselectAll();
	}
	
	//return the selected objects
	public DocumentObject[] getSelectedObjects()
	{
		return selectionListener.getSelectedObjects();
	}
	
	//delete object
	public void delete()
	{
		for(int i = 0; i < selectionListener.selectedObjects.size(); i++)
			removeObject(selectionListener.selectedObjects.elementAt(i));
		
		deselectAll();
		
		editor.repaint();
	}
	
	public void toggleGroup()
	{
		requestFocusInEditor();
		
		DocumentObject[] selection = selectionListener.getSelectedObjects();
		
		if(selection.length > 0)
		{
			// dissolve the selected group
			if(selection.length == 1 &&
					selection[0] instanceof Group)
			{
				Group group = (Group) selection[0];
				
				DocumentObject[] objects = group.removeAllObjects();
				
				deselectAll();
				
				for(DocumentObject object : objects)
				{
					addObject(object);
					select(object);
				}
				
				removeObject(group);
			}
			// create a group with the selected components
			else
			{
				Group group = new Group();
				
				setupDocumentObject(group);
				
				DocumentObject.sortObjectsByZOrder(selection);
				
				for(DocumentObject object : selection)
				{
					group.addObject(object);
					removeObject(object);
				}
				
				group.computeBounds();
				
				addObject(group);
				
				deselectAll();
				
				group.setMyZOrder(0);
				
				selectionListener.select(group);
			}
			
			editor.repaint();
		}
	}
	
	/*
	public void addUndoable(Undoable undo)
	{
		undoList.push(undo);
		menuBar.setUndoEnabled(false);
	}	
	public void undo()
	{
		int undoCount = undoList.size();		
		if(undoCount >= 1)
		{
			Undoable undo = undoList.pop();			
			undo.undo();			
			redoList.push(undo);			
			menuBar.setRedoEnabled(true);
		}
		
		if(undoCount == 1)
		{
			menuBar.setUndoEnabled(false);
			setDirty(false);
		}		
		editor.repaint();
	}
	
	public void redo()
	{
		int redoCount = redoList.size();		
		setDirty(true);		
		if(redoCount >= 1)
		{
			Undoable redo = redoList.pop();			
			redo.redo();			
			undoList.push(redo);			
			menuBar.setUndoEnabled(true);
		}		
		if(redoCount == 1)
		{
			menuBar.setRedoEnabled(false);
		}		
		editor.repaint();
	}
	*/
	
	private void syncWithDoc()
	{
		closeAllTabs();
		
		Sheet[] sheets = new Sheet[doc.getSheetCount()];
		
		sheets = doc.getSheets(sheets);
		
		for(Sheet s : sheets)
		{
			DocumentEditorPane editor = createNewEditor();
			
			JScrollPane sp = createNewTabComponent(editor);
			
			tabbedPane.addTab(s.getTitle(), sp);
			
			editor.setSheet(s);
			
			for(DocumentObject obj : s.getAllObjects())
			{
				setupDocumentObjectRec(obj);
				
				obj.setSelected(false);
				
				editor.add(obj);
			}
		}
	}
	
	//close the document
	public int closeDocument() 
	{
		windowListener.windowClosing(
				new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		
		return windowListener.getLastResponse();
	}
	
	//return the next object
	public DocumentObject getNextObject()
	{
		return nextObject;
	}
	
	//set the next object
	public void setNextObject(DocumentObject nextObject)
	{
		this.nextObject = nextObject;
	}
	
	private void setupDocumentObjectRec(DocumentObject object)
	{
		setupDocumentObject(object);
		
		if(object instanceof Group)
		{
			DocumentObject[] groupObjects =
				((Group) object).getAllObjects();
			
			for(DocumentObject groupObj : groupObjects)
				setupDocumentObjectRec(groupObj);
		}
	}
	
	private void setupDocumentObject(DocumentObject object)
	{
		dragMover.install(object);
		
		object.addMouseListener(selectionListener);
		
		object.addMouseListener(editor.getShapeDrawer());
		object.addMouseMotionListener(editor.getShapeDrawer());
		
		object.addMouseMotionListener(cursorPosition);
	}
	
	//return the shape properties
	public ShapeProperties getShapeProperties()
	{
		return shapeProperties;
	}
	
	//used for cut
	public void cut()
	{
		copy();
		delete();
	}
	
	//copy method
	public void copy()
	{
		DocumentObject[] selection =  getSelectedObjects();
		
		if(selection.length == 0)
			return;
		
		DocumentObjectSelection clipboardData =
			new DocumentObjectSelection(selection);
		
		clipboard.setContents(clipboardData, clipboardData);
		
		menuBar.setPasteEnabled(true);
	}
	
	//paste method
	public void paste()
	{
		Object clipboardContents = clipboard.getContents(null);
		
		if(clipboardContents == null)
			return;
		
		DocumentObjectSelection clipboardData =
			(DocumentObjectSelection) clipboardContents;
		
		DocumentObject[] objects = clipboardData.getContent();
		
		deselectAll();
		
		for(DocumentObject obj : objects)
		{
			//offset the new object by a few pixels
			int x = obj.getX() + PASTED_OBJECT_OFFSET;
			int y = obj.getY() + PASTED_OBJECT_OFFSET;
			
			obj.setLocation(x, y);
			
			addObject(obj);
			
			obj.setMyZOrder(0);
			
			select(obj);
		}
		
		copy();
	}
	
	public static boolean clipboardHasContents()
	{
		return clipboard.getContents(null) != null;
	}
	
	//------------------------
	// misc. utilitiy classes
	//------------------------
	class CursorPositionListener implements MouseMotionListener
	{
		@Override
		public void mouseDragged(MouseEvent e)
		{
			updatePosition(e);
		}
		
		@Override
		public void mouseMoved(MouseEvent e)
		{
			updatePosition(e);
		}
		
		private void updatePosition(MouseEvent e)
		{
			int x = e.getX();
			int y = e.getY();
			
			Component comp = e.getComponent();
			
			while(comp != null && comp instanceof DocumentObject)
			{
				x += comp.getX();
				y += comp.getY();
				
				comp = comp.getParent();
			}
			
			StringBuffer coords = new StringBuffer();
			
			coords
			.append("Mouse position: ")
			.append(x)
			.append(", ")
			.append(y);
			
			if(nextObject != null)
			{
				coords
				.append(", object size: ")
				.append(nextObject.getWidth())
				.append(", ")
				.append(nextObject.getHeight());
			}
			
			statusLabel.setText(coords.toString());
		}
	}
	
	class ShapeDrawer
	implements MouseListener, MouseMotionListener, KeyListener
	{
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mouseMoved(MouseEvent e) {}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if (nextObject != null)
			{
				setDirty(true);
			}
			
			nextObject = null;
		}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(nextObject != null && e.getButton() == MouseEvent.BUTTON1)
			{
				deselectAll();
				
				JComponent src = (JComponent) e.getSource();
				
				Point p = null;
				
				if(src instanceof DocumentEditorPane)
					p = new Point(e.getX(), e.getY());
				else
				{
					int x = e.getX() + src.getX();
					int y = e.getY() + src.getY();
					
					p = new Point(x, y);
				}
				
				setupDocumentObject(nextObject);
				
				nextObject.setPoints(p);
				
				nextObject.setForeground(shapeProperties.getForeground());
				nextObject.setBackground(shapeProperties.getBackground());
				
				editor.addObject(nextObject);
				editor.setComponentZOrder(nextObject, 0);
				
				repaint();
			}
		}
		
		@Override
		public void mouseDragged(MouseEvent e)
		{
			if(nextObject != null)
			{
				setDirty(true);
				
				JComponent src = (JComponent) e.getSource();
				
				Point p = null;
				
				if(src instanceof DocumentEditorPane)
					p = new Point(e.getX(), e.getY());
				else
				{
					int x = e.getX() + src.getX();
					int y = e.getY() + src.getY();
					
					p = new Point(x, y);
				}
				
				if(p.x < 0)
					p.x = 0;
				
				if(p.y < 0)
					p.y = 0;
				
				if(nextObject instanceof Image)
					nextObject.setP1(p);
				else
					nextObject.setP2(p);
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			{
				if(nextObject instanceof Shape)
					((Shape) nextObject).setRegularShape(true);
			}
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_SHIFT)
			{
				if(nextObject instanceof Shape)
					((Shape) nextObject).setRegularShape(false);
			}	
		}
		
		@Override
		public void keyTyped(KeyEvent e) {}
	}
	
	class SizeBounder extends ComponentAdapter
	{
		@Override
		public void componentResized(ComponentEvent e)
		{
			JFrame tmp = (JFrame)e.getSource();
			
			int w = tmp.getWidth();
			int h = tmp.getHeight();
			
			int minW = getMinimumSize().width;
			int minH = getMinimumSize().height;
			
			int maxW = getMaximumSize().width;
			int maxH = getMaximumSize().height;
			
			if(w < minW)
				w = minW;
			else if(w > maxW)
				w = maxW;
			
			if(h < minH)
				h = minH;
			else if(h > maxH)
				h = maxH;
			
			tmp.setSize(w, h);
		}
	}
	
	class TabChangeListener implements ChangeListener, MouseListener
	{
		@Override
		public void stateChanged(ChangeEvent e)
		{
			deselectAll();
			
			JTabbedPane src = (JTabbedPane) e.getSource();
			
			JScrollPane sp = (JScrollPane) src.getSelectedComponent();
			
			if(sp != null)
				editor = (DocumentEditorPane) sp.getViewport().getView();
		}

		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			if(e.getButton() == MouseEvent.BUTTON2)
				System.out.println("hak");
		}
		
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
	
	class SelectionListener implements MouseListener, KeyListener
	{
		private Vector<DocumentObject> selectedObjects;
		private boolean isControlDown;
		private boolean isShiftDown;
		
		public SelectionListener()
		{
			selectedObjects = new Vector<DocumentObject>();
			
			isControlDown = false;
			isShiftDown = false;
		}
		
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		
		@Override
		public void mousePressed(MouseEvent e)
		{
			// make sure key events are being heard
			requestFocusInEditor();
			
			boolean selectGroupButtonSelected = false;
			
			if(e.getSource() instanceof DocumentObject && nextObject == null)
			{
				if(!shouldAddToSelection())
					deselectAll();
				
				Component comp = (Component) e.getSource();
				
				DocumentObject obj = (DocumentObject) comp;
				
				select(obj);
				
				selectGroupButtonSelected =
					e.getSource() instanceof Group &&
					selectedObjects.size() == 1;
				
				//XXX
				if (!(comp instanceof Group))
				{
					toolBar.setShapeForegroundButtonColor(obj.getForeground());
					toolBar.setShapeBackgroundButtonColor(obj.getBackground());
				}
				
				repaint();
			}
			
			toolBar.setGroupSelected(selectGroupButtonSelected);
		}
		
		@Override
		public void mouseReleased(MouseEvent e)
		{
			if(e.getSource() instanceof DocumentEditorPane &&
					!shouldAddToSelection())
			{
				deselectAll();
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_CONTROL)
				isControlDown = true;
			
			else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				isShiftDown = true;
		}
		
		@Override
		public void keyReleased(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_CONTROL)
				isControlDown = false;
			
			else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
				isShiftDown = false;
		}
		
		@Override
		public void keyTyped(KeyEvent e) {}
		
		public void select(DocumentObject object)
		{
			if(!selectedObjects.contains(object))
			{
				selectedObjects.add(object);
				object.setSelected(true);
				
				menuBar.setCopyEnabled(true);
				
				editor.repaint();
			}
		}
		
		public void deselect(DocumentObject object)
		{
			selectedObjects.remove(object);
			object.setSelected(false);
			
			if(selectedObjectCount() == 0)
				menuBar.setCopyEnabled(false);
			
			editor.repaint();
		}
		
		public void deselectAll()
		{
			for(DocumentObject obj : selectedObjects)
				obj.setSelected(false);
			
			selectedObjects.clear();
			
			menuBar.setCopyEnabled(false);
			
			editor.repaint();
		}
		
		public int selectedObjectCount()
		{
			return selectedObjects.size();
		}
		
		public DocumentObject[] getSelectedObjects()
		{
			return selectedObjects.toArray(
					new DocumentObject[selectedObjects.size()]);
		}
		
		public boolean shouldAddToSelection()
		{
			return isControlDown || isShiftDown;
		}
	}
}
