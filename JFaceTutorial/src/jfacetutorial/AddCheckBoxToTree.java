package jfacetutorial;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class AddCheckBoxToTree {

	public class CheckTreeSelectionModel extends DefaultTreeSelectionModel {
		static final long serialVersionUID = 0;
		private TreeModel model;

		public CheckTreeSelectionModel(TreeModel model) {
			this.model = model;

			setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		}

		public boolean isPartiallySelected(TreePath path) {
			if (isPathSelected(path, true)) {
				return false;
			}

			TreePath[] selectionPaths = getSelectionPaths();

			if (selectionPaths == null) {
				return false;
			}

			for (int j = 0; j < selectionPaths.length; j++) {
				if (isDescendant(selectionPaths[j], path)) {
					return true;
				}
			}

			return false;
		}

		public boolean isPathSelected(TreePath path, boolean dig) {
			if (!dig) {
				return super.isPathSelected(path);
			}

			while (path != null && !super.isPathSelected(path)) {
				path = path.getParentPath();
			}

			return path != null;
		}

		private boolean isDescendant(TreePath path1, TreePath path2) {
			Object obj1[] = path1.getPath();
			Object obj2[] = path2.getPath();
			for (int i = 0; i < obj2.length; i++) {
				if (obj1[i] != obj2[i])
					return false;
			}
			return true;
		}

		public void setSelectionPaths(TreePath[] pPaths) {
			throw new UnsupportedOperationException("not implemented yet!!!");
		}

		public void addSelectionPaths(TreePath[] paths) {

			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];

				TreePath[] selectionPaths = getSelectionPaths();

				if (selectionPaths == null) {
					break;
				}

				ArrayList<TreePath> toBeRemoved = new ArrayList<TreePath>();

				for (int j = 0; j < selectionPaths.length; j++) {
					if (isDescendant(selectionPaths[j], path)) {
						toBeRemoved.add(selectionPaths[j]);
					}
				}
				super.removeSelectionPaths((TreePath[]) toBeRemoved.toArray(new TreePath[0]));
			}

			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];

				TreePath temp = null;

				while (areSiblingsSelected(path)) {
					temp = path;

					if (path.getParentPath() == null) {
						break;
					}

					path = path.getParentPath();
				}

				if (temp != null) {
					if (temp.getParentPath() != null) {
						addSelectionPath(temp.getParentPath());
					} else {
						if (!isSelectionEmpty()) {
							removeSelectionPaths(getSelectionPaths());
						}

						super.addSelectionPaths(new TreePath[] { temp });
					}
				} else {
					super.addSelectionPaths(new TreePath[] { path });
				}
			}
		}

		private boolean areSiblingsSelected(TreePath path) {
			TreePath parent = path.getParentPath();

			if (parent == null) {
				return true;
			}

			Object node = path.getLastPathComponent();

			Object parentNode = parent.getLastPathComponent();

			int childCount = model.getChildCount(parentNode);

			Boolean isParameters = false;
			Boolean isDescription = false;

			for (int i = 0; i < childCount; i++) {

				Object childNode = model.getChild(parentNode, i);

				if (childNode == node) {
					continue;
				}

				if (childCount == 2) {
					if (childNode.toString().equals("parameters") && model.isLeaf(childNode)) {
						isParameters = true;
					}
					if (childNode.toString().equals("description") && model.isLeaf(childNode)) {
						isDescription = true;
					}
				}

				if (!isPathSelected(parent.pathByAddingChild(childNode)) && !isParameters && !isDescription) {
					return false;
				}
			}

			return true;
		}

		public void removeSelectionPaths(TreePath[] paths) {
			for (int i = 0; i < paths.length; i++) {
				TreePath path = paths[i];
				if (path.getPathCount() == 1)
					super.removeSelectionPaths(new TreePath[] { path });
				else
					toggleRemoveSelection(path);
			}
		}

		private void toggleRemoveSelection(TreePath path) {

			Stack<TreePath> stack = new Stack<TreePath>();
			TreePath parent = path.getParentPath();

			Boolean isParameters = false;
			Boolean isDescription = false;

			while (parent != null && !isPathSelected(parent)) {
				stack.push(parent);
				parent = parent.getParentPath();
			}
			if (parent != null)
				stack.push(parent);
			else {
				super.removeSelectionPaths(new TreePath[] { path });
				return;
			}

			while (!stack.isEmpty()) {
				TreePath temp = (TreePath) stack.pop();

				TreePath peekPath = stack.isEmpty() ? path : (TreePath) stack.peek();

				Object node = temp.getLastPathComponent();
				Object peekNode = peekPath.getLastPathComponent();
				int childCount = model.getChildCount(node);

				for (int i = 0; i < childCount; i++) {
					Object childNode = model.getChild(node, i);

					if (childNode.toString().equals("parameters") && model.isLeaf(childNode)) {
						isParameters = true;
					}
					if (childNode.toString().equals("description") && model.isLeaf(childNode)) {
						isDescription = true;
					}

					if (childNode != peekNode) {
						if (!isParameters && !isDescription)
							super.addSelectionPaths(new TreePath[] { temp.pathByAddingChild(childNode) });
					}
				}
			}

			super.removeSelectionPaths(new TreePath[] { parent });
		}

		public TreeModel getModel() {
			return model;
		}
	}

	public class CheckTreeCellRenderer extends JPanel implements TreeCellRenderer {

		static final long serialVersionUID = 0;

		CheckTreeSelectionModel selectionModel;
		private TreeCellRenderer delegate;
		TristateCheckBox checkBox = new TristateCheckBox();

		public CheckTreeCellRenderer(TreeCellRenderer delegate, CheckTreeSelectionModel selectionModel) {
			this.delegate = delegate;
			this.selectionModel = selectionModel;

			setLayout(new BorderLayout());
			setOpaque(false);
			checkBox.setOpaque(false);

		}

		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			Component renderer = delegate.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row,
					hasFocus);

			TreePath path = tree.getPathForRow(row);

			if (path != null) {
				if (selectionModel.isPathSelected(path, true)) {
					checkBox.setState(checkBox.SELECTED);
				} else {
					checkBox.setState(checkBox.NOT_SELECTED);
				}

				if (selectionModel.isPartiallySelected(path)) {
					checkBox.setState(checkBox.DONT_CARE);
				}
			}

			removeAll();

			add(checkBox, BorderLayout.WEST);
			add(renderer, BorderLayout.CENTER);

			return this;
		}

		public TreeCellRenderer getDelegate() {
			return delegate;
		}

		public void setDelegate(TreeCellRenderer delegate) {
			this.delegate = delegate;
		}
	}

	public class CheckTreeManager extends MouseAdapter implements TreeSelectionListener {
		CheckTreeSelectionModel selectionModel;
		private JTree tree = new JTree();

		int hotspot = new JCheckBox().getPreferredSize().width;

		public CheckTreeManager(JTree tree, CheckTreeSelectionModel checkTreeSelectionModel) {
			this.tree = tree;

			if (checkTreeSelectionModel != null) {
				selectionModel = checkTreeSelectionModel;

			} else {
				selectionModel = new CheckTreeSelectionModel(tree.getModel());
			}

			tree.setCellRenderer(new CheckTreeCellRenderer(tree.getCellRenderer(), selectionModel));

			tree.addMouseListener(this);
			selectionModel.addTreeSelectionListener(this);
		}

		public void mouseClicked(MouseEvent me) {

			TreePath path = tree.getPathForLocation(me.getX(), me.getY());

			if (path == null) {
				return;
			}

			if (me.getX() / 1.2 > tree.getPathBounds(path).x + hotspot) {
				return;
			}

			boolean selected = selectionModel.isPathSelected(path, true);
			selectionModel.removeTreeSelectionListener(this);

			try {
				if (selected) {
					selectionModel.removeSelectionPath(path);
				} else {
					selectionModel.addSelectionPath(path);
				}
			} finally {
				selectionModel.addTreeSelectionListener(this);
				tree.treeDidChange();
			}
		}

		public CheckTreeSelectionModel getSelectionModel() {
			return selectionModel;
		}

		public void setSelectionModel(CheckTreeSelectionModel selectionModel) {
			this.selectionModel = selectionModel;
		}

		public void valueChanged(TreeSelectionEvent e) {
			tree.treeDidChange();
		}
	}
}