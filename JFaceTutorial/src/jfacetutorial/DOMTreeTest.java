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
				return parseDocument(new FileInputStream(file));
			}
			
			

			protected void done() {
				try {
					 UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					 JFrame.setDefaultLookAndFeelDecorated(true);
					 Document doc = get();
					 doc.getDocumentElement().normalize();
					
					JTree tree = new JTree(new DOMTreeModel(doc));						
					XmlTreeRenderer renderer2 = new XmlTreeRenderer();

					tree.setCellRenderer(renderer2);
					
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


 class XmlTreeRenderer extends DefaultTreeCellRenderer {
	 
private Color elementColor = new Color(0, 0, 128);
 private Color attributeColor = new Color(0, 128, 0);
 
public XmlTreeRenderer() {
	Icon leafIcon = new ImageIcon(getClass().getResource("/cs2.png"));
 setLeafIcon(leafIcon);
 }
 
@Override
 public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
 Node node = (Node) value;
 
 switch (node.getNodeType()) {
 case Node.ELEMENT_NODE:
	 Element element = (Element) node;
	 if( element.getAttribute("name")!=null)
		 value = element.getAttribute("name");
	 else
		 value = element.getElementsByTagName(element.getTagName()).item(0).getTextContent();
 break;
 case Node.ATTRIBUTE_NODE:
 value = '@' + node.getNodeName();
 break;
 case Node.TEXT_NODE:
 value = node.getNodeValue().trim();
 break;
 case Node.COMMENT_NODE:
 value = "<!--" + node.getNodeValue() + "-->";
 break;
 case Node.DOCUMENT_TYPE_NODE:
 DocumentType dtype = (DocumentType) node;
 value = "<!DOCTYPE " + dtype.getName() + '>';
 break;
 default:
 value = node.getNodeName();
 }
 
 super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
 
if (!selected) {
 switch (node.getNodeType()) {
 case Node.ELEMENT_NODE:
 setForeground(elementColor);
 break;
 case Node.ATTRIBUTE_NODE:
 setForeground(attributeColor);
 break;
 }
 }
 
 return this;
 }
}