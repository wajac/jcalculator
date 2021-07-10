package com.jcalculator;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class JCalculator extends JFrame implements ActionListener, KeyListener {
	JTextField displayText;
	JButton[] digits;
	Map<String, JButton> otherButtons;
	
	public JCalculator() {
		super("Calculator");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(270, 375);
		setResizable(false);
		setLocationByPlatform(true);
		// Set up the content panel
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new BorderLayout(0, 24));
		// Set up the display panel
		JPanel displayPane = new JPanel();
		displayText = new JTextField();
		displayText.setPreferredSize(new Dimension(getWidth() - 2 * getInsets().left - 1, 55));
		displayText.setHorizontalAlignment(JTextField.RIGHT);
		displayText.setFont(new Font("Nimbus Sans", Font.PLAIN, 22));
		displayText.setOpaque(true);
		displayText.setBackground(Color.white);
		displayText.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
		displayText.setEditable(false);
		displayText.addKeyListener(this);
		displayText.addActionListener(event -> actionPerformed(new ActionEvent(displayText, ActionEvent.ACTION_PERFORMED, "=")));
		displayPane.add(displayText);
		contentPane.add(BorderLayout.NORTH, displayPane);
		// Set up the buttons panel
		digits = new JButton[10];
		for(int i=0; i<10; i++) {
			digits[i] = new JButton("" + i);
			digits[i].addActionListener(this);
			digits[i].setFocusable(false);
		}
		otherButtons = new HashMap<>();
		otherButtons.put("equal", new JButton("="));
		otherButtons.put("addition", new JButton("+"));
		otherButtons.put("substraction", new JButton("-"));
		otherButtons.put("multiplication", new JButton("*"));
		otherButtons.put("division", new JButton("/"));
		otherButtons.put("dot", new JButton("."));
		otherButtons.put("back", new JButton("\u232B"));
		otherButtons.put("C", new JButton("C"));
		otherButtons.put("CE", new JButton("CE"));
		otherButtons.forEach((key, button) -> {
			button.setFocusable(false);
			button.addActionListener(this);
		});
		JPanel buttonsPane = new JPanel();
		// GridBagLayout
		buttonsPane.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(1, 1, 1, 1);
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		addComponent(buttonsPane, otherButtons.get("CE"), gbc);
		addComponent(buttonsPane, otherButtons.get("C"), gbc);
		addComponent(buttonsPane, otherButtons.get("back"), gbc);
		addComponent(buttonsPane, otherButtons.get("division"), gbc);
		int i = 7;
		while(i > 0) {
			for(int j=0; j<3; j++) {
				addComponent(buttonsPane, digits[i + j], gbc);
				if(j == 2) {
					i = i - 3;
					switch(i) {
					case 4:
						addComponent(buttonsPane, otherButtons.get("multiplication"), gbc);
						break;
					case 1:
						addComponent(buttonsPane, otherButtons.get("substraction"), gbc);
						break;
					}
				}
			}
		}
		addComponent(buttonsPane, otherButtons.get("addition"), gbc);
		addComponent(buttonsPane, digits[0], gbc);
		addComponent(buttonsPane, otherButtons.get("dot"), gbc);
		gbc.gridwidth = 2;
		addComponent(buttonsPane, otherButtons.get("equal"), gbc);
		contentPane.add(BorderLayout.CENTER, buttonsPane);
		// Display the frame
		setVisible(true);
	}
	
	/**
	 * This is a convenience method to add components to a GridBagLayout.
	 * It is used to update the most frequently changing constraints, other constraints should be set somewhere else.
	 * By default, this method assumes a 5x4 grid and adds the components in a sequential left-to-right and top-to-bottom order.
	 * @param cont
	 * @param comp
	 * @param gbc
	 */
	private void addComponent(Container cont, JComponent comp, GridBagConstraints gbc) {
		cont.add(comp, gbc);
		gbc.gridx++;
		if(gbc.gridx == 4) {
			gbc.gridx = 0;
			gbc.gridy++;
		}
	}
	
	@Override
	public Insets getInsets() {
		return new Insets(60, 18, 25, 18);
	}
	
	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		String[] operations = { "+", "-", "*", "/" };
		java.util.List<String> operationsList =  Arrays.asList(operations);
		try {
			int x = Integer.parseInt(command);
			if(x >= 0 & x <= 9) {
				displayText.setText(displayText.getText() + x);
			}
		} catch (NumberFormatException e) {
			// Decimal dots and arithmetic operations
			if(command.equals(".") || operationsList.contains(command)) {
				if(displayText.getText().isEmpty()) {
					displayText.setText(command);
				} else if(displayText.getText().endsWith(command)) {
					// Do nothing
				} else {
					if(!command.equals(".") && operationsList.contains("" + displayText.getText().charAt(displayText.getText().length() - 1))) {
						// If the text ends with a different operation, replace it with the current one
						displayText.setText(displayText.getText().substring(0, displayText.getText().length() - 1) + command);
					} else {
						// Add the command to the text
						displayText.setText(displayText.getText() + command);
					}
				}
			}
			// Other commands
			switch(command) {
			case "\u232B":
				StringBuilder sb = new StringBuilder(displayText.getText());
				if(sb.length() != 0) {
					displayText.setText(sb.substring(0, sb.length() - 1));
				}
				break;
			case "CE":
			case "C":
				displayText.setText("");
				break;
			case "=":
				displayText.setText(calculate(displayText.getText()));
				break;
			}
		}
	}
	
	/**
	 * This is the method to do the match specified by the command String parameter and returns the result as a String. 
	 * @param command
	 * @return
	 */
	private String calculate(String command) {
		String result = "";
		String[] operands;
		operands = command.split("[+[\\-]*/]");
		System.out.println(command);
		System.out.println(Arrays.asList(operands));
		if(operands.length > 1) {
			try {
				Float val1, val2;
				// A special case for the first arguments
				int i = 0, j = 0;
				if(operands[i].equals("")) {
					if(command.charAt(j) == '+' || command.charAt(j) == '-') {
						// If the first operand was a signed value
						operands[i+1] = command.charAt(j) + operands[i+1];
						i++;
					} else {
						// Error
						throw new NumberFormatException();
					}
				}
				val1 = Float.parseFloat(operands[i]);
				j = operands[i].length();
				i++;
				// Iterate through the rest of the arguments
				for(; i<operands.length; i++) {
					val2 = Float.parseFloat(operands[i]);
					// ---
					System.out.println(val1 + " " + command.charAt(j) + " " + val2);
					switch(command.charAt(j)) {
					case '+':
						val1 += val2;
						break;
					case '-':
						val1 -= val2;
						break;
					case '*':
						val1 *= val2;
						break;
					case '/':
						val1 /= val2;
						break;
					}
					j += operands[i].length() + 1;
				}
				// Display the result as a mathematical integer or a floating-point value
				if(val1.floatValue() - val1.intValue() == 0) {
					result = "" + val1.intValue();
				} else {
					result = val1.toString();
				}
				System.out.println("= " + result);
			} catch(NumberFormatException e) {
				result = "E";
			}
		}
		return result;
	}
	
	@Override
	public void keyPressed(KeyEvent event) {
		// ...
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		// ...
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
		// Create a new ActionEvent and pass it to the ActionListener
		String command;
		if(event.getKeyChar() == '\u0008') {
			// Match the backspace key to the delete button
			command = "\u232B";
		} else {
			command = "" + event.getKeyChar();
		}
		actionPerformed(new ActionEvent(displayText, ActionEvent.ACTION_PERFORMED, command));
	}
	
	public static void main(String[] args) {
		new JCalculator();
	}
}
