package jfacetutorial;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Objects;
import java.util.Optional;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

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

		JMenu fileMenu = new JMenu("File");
		JMenuItem openItem = new JMenuItem("Open");
		openItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openFile();
			}
		});
		fileMenu.add(openItem);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		fileMenu.add(exitItem);

		JMenuBar menuBar = new JMenuBar();
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
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
					Document doc = get();
					JTree tree = new JTree(new DOMTreeModel(doc));
					DOMTreeCellRenderer renderer = new DOMTreeCellRenderer();
//					Icon closedIcon = new ImageIcon(getClass().getResource("check.png"));
//					Icon openIcon = new ImageIcon(getClass().getResource("fs.png"));
//					Icon leafIcon = new ImageIcon(getClass().getResource("cs.png"));
//					renderer.setClosedIcon(closedIcon);
//					renderer.setOpenIcon(openIcon);
//					renderer.setLeafIcon(leafIcon);
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
		System.out.println("length - " + list.getLength());
		System.out.println(list.item(index));

		return list.item(index);
	}

	public int getIndexOfChild(Object parent, Object child) {
		Node node = (Node) parent;
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (getChild(node, i) == child)
				return i;
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

		if (node instanceof Element) {

			setIcon(closedIcon);
			return elementPanel((Element) node);
		}

		if (node instanceof CharacterData) {

			setText("test");
		} else
			setText(node.getClass() + ": " + node.toString());
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