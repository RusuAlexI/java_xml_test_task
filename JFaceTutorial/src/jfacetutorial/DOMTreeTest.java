package jfacetutorial;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class DOMTreeTest {

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				JFrame frame = new DOMTreeFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
			}
		});
	}
}


class DOMTreeFrame extends JFrame {
	AddCheckBoxToTree AddCh = new AddCheckBoxToTree();
	private AddCheckBoxToTree.CheckTreeManager checkTreeManager;

	public DOMTreeFrame() {
		setTitle("DOMTreeTest");
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
				openFile();
	}

	public void openFile() {
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new File("."));

		chooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().toLowerCase().endsWith(".xml");
			}

			public String getDescription() {
				return "XML files";
			}
		});
		int r = chooser.showOpenDialog(this);
		if (r != JFileChooser.APPROVE_OPTION)
			return;
		final File file = chooser.getSelectedFile();

		new SwingWorker<Document, Void>() {
			protected Document doInBackground() throws Exception {
				if (builder == null) {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					builder = factory.newDocumentBuilder();
				}
				return builder.parse(file);
			}
			
			

			protected void done() {
				try {
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					 JFrame.setDefaultLookAndFeelDecorated(true);
					 Document doc = parseDocument(new FileInputStream(new File("C:/Users/Sandu/task-third-try/JFaceTutorial/resource/struct.xml")));
					doc.getDocumentElement().normalize();
					
					JTree tree = new JTree(new DOMTreeModel(doc));	
					
					DOMTreeCellRenderer renderer = new DOMTreeCellRenderer();
					
					Icon closedIcon = new ImageIcon(getClass().getResource("cs.png"));
					
					renderer.setClosedIcon(closedIcon);
					tree.setCellRenderer(renderer);
					
					checkTreeManager = AddCh.new CheckTreeManager(tree, null);

					setContentPane(new JScrollPane(tree));
					validate();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(DOMTreeFrame.this, e);
				}
			}
		}.execute();
	}
	
	public static Document parseDocument(InputStream inputStream) throws Exception {
		 
		InputSource inputSource = new InputSource(inputStream);
		 
		SAXParserFactory saxFactory = SAXParserFactory.newInstance();
		 SAXParser parser = saxFactory.newSAXParser();
		 XMLReader reader = new XMLTrimFilter(parser.getXMLReader());
		 
		TransformerFactory factory = TransformerFactory.newInstance();
		 Transformer transformer = factory.newTransformer();
		 transformer.setOutputProperty(OutputKeys.INDENT, "no");
		 DOMResult result = new DOMResult();
		 transformer.transform(new SAXSource(reader, inputSource), result);
		 return (Document) result.getNode();
		 }
	
	

	private DocumentBuilder builder;
	private static final int DEFAULT_WIDTH = 400;
	private static final int DEFAULT_HEIGHT = 400;
}


class DOMTreeModel implements TreeModel {
	public DOMTreeModel(Document doc) {
		this.doc = doc;
	}

	public Object getRoot() {
		doc.getDocumentElement().normalize();
		return doc.getDocumentElement();
	}

	public int getChildCount(Object parent) {
		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		return list.getLength();
	}
	

	public Object getChild(Object parent, int index) {
		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		System.out.println("child length - " + list.getLength());
		System.out.println(list.item(index));

		return list.item(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (getChild(node, i) == child)
				return i+1;
		}
		return -1;
	}

	public boolean isLeaf(Object node) {
		return getChildCount(node) == 0;
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
	}

	public void addTreeModelListener(TreeModelListener l) {
	}

	public void removeTreeModelListener(TreeModelListener l) {
	}

	private Document doc;
}

class DOMTreeCellRenderer extends DefaultTreeCellRenderer {

	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus) {
		Node node = (Node) value;

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			setText("test2");
			setIcon(closedIcon);
			return elementPanel((Element) node);
		}

		if (node instanceof CharacterData) {
			setText("test");
			setIcon(openIcon);
		} else {
			setText(node.getClass() + ": " + node.toString());}
		return this;
	}

	public static JPanel elementPanel(Element e) {
		JPanel panel = new JPanel();
		final NamedNodeMap map = e.getAttributes();
		panel.add(new JTable(new AbstractTableModel() {
			public int getRowCount() {
				return map.getLength();
			}

			public int getColumnCount() {
				return 1;
			}

			public Object getValueAt(int r, int c) {
				return map.item(r).getNodeValue().substring(c);
			}
		}));
		return panel;
	}

	public static String characterString(CharacterData node) {
		StringBuilder builder = new StringBuilder(node.getData());

		return builder.toString();
	}
}