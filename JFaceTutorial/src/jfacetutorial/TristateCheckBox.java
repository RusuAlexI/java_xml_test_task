package jfacetutorial;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ActionMapUIResource;

public class TristateCheckBox extends JCheckBox {
	static final long serialVersionUID = 0;

	public static class State {
		private State() {
		}
	}

	public final State NOT_SELECTED = new State();
	public final State SELECTED = new State();
	public final static State DONT_CARE = new State();

	private final TristateDecorator model;

	public TristateCheckBox(String text, Icon icon, State initial) {
		super(text, icon);
		super.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				grabFocus();
				model.nextState();
			}
		});

		ActionMap map = new ActionMapUIResource();
		map.put("pressed", new AbstractAction() {

			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				grabFocus();
				model.nextState();
			}
		});

		map.put("released", null);

		SwingUtilities.replaceUIActionMap(this, map);

		model = new TristateDecorator(getModel());
		setModel(model);
		setState(initial);
	}

	public TristateCheckBox(String text, State initial) {
		this(text, null, initial);
	}

	public TristateCheckBox(String text) {
		this(text, DONT_CARE);
	}

	public TristateCheckBox() {
		this(null);
	}

	public void addMouseListener(MouseListener l) {
	}

	public void setState(State state) {
		model.setState(state);
	}

	public State getState() {
		return model.getState();
	}

	public void setSelected(boolean b) {
		if (b) {
			setState(SELECTED);
		} else {
			setState(NOT_SELECTED);
		}
	}

	private class TristateDecorator implements ButtonModel {
		private final ButtonModel other;

		private TristateDecorator(ButtonModel other) {
			this.other = other;
		}

		private void setState(State state) {
			if (state == NOT_SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(false);
			} else if (state == SELECTED) {
				other.setArmed(false);
				setPressed(false);
				setSelected(true);
			} else { // either "null" or DONT_CARE
				other.setArmed(true);
				setPressed(true);
				setSelected(false);
			}
		}

		private State getState() {
			if (isSelected() && !isArmed()) {
				return SELECTED;
			} else if (isSelected() && isArmed()) {
				return DONT_CARE;
			} else {
				return NOT_SELECTED;
			}
		}

		private void nextState() {
			State current = getState();
			if (current == NOT_SELECTED) {
				setState(SELECTED);
			} else if (current == SELECTED) {
				setState(DONT_CARE);
			} else if (current == DONT_CARE) {
				setState(NOT_SELECTED);
			}
		}

		public void setArmed(boolean b) {
		}

		public void setEnabled(boolean b) {
			setFocusable(b);
			other.setEnabled(b);
		}

		public boolean isArmed() {
			return other.isArmed();
		}

		public boolean isSelected() {
			return other.isSelected();
		}

		public boolean isEnabled() {
			return other.isEnabled();
		}

		public boolean isPressed() {
			return other.isPressed();
		}

		public boolean isRollover() {
			return other.isRollover();
		}

		public int getMnemonic() {
			return other.getMnemonic();
		}

		public String getActionCommand() {
			return other.getActionCommand();
		}

		public Object[] getSelectedObjects() {
			return other.getSelectedObjects();
		}

		public void setSelected(boolean b) {
			other.setSelected(b);
		}

		public void setPressed(boolean b) {
			other.setPressed(b);
		}

		public void setRollover(boolean b) {
			other.setRollover(b);
		}

		public void setMnemonic(int key) {
			other.setMnemonic(key);
		}

		public void setActionCommand(String s) {
			other.setActionCommand(s);
		}

		public void setGroup(ButtonGroup group) {
			other.setGroup(group);
		}

		public void addActionListener(ActionListener l) {
			other.addActionListener(l);
		}

		public void removeActionListener(ActionListener l) {
			other.removeActionListener(l);
		}

		public void addItemListener(ItemListener l) {
			other.addItemListener(l);
		}

		public void removeItemListener(ItemListener l) {
			other.removeItemListener(l);
		}

		public void addChangeListener(ChangeListener l) {
			other.addChangeListener(l);
		}

		public void removeChangeListener(ChangeListener l) {
			other.removeChangeListener(l);
		}

	}
}