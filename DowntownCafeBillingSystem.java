package com.mycompany.downtowncafebillingsystem;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.ArrayList;

public class DowntownCafeBillingSystem extends JFrame {

    private static final long serialVersionUID = 1L;

    private JTextArea billArea;
    private JLabel totalLabel, finalAmountLabel;
    private JTextField discountField, customerNameField, phoneNumberField, billNumberField;
    private double totalAmount = 0.0;
    private List<OrderItem> orderItems = new ArrayList<>();
    private JComboBox<String> categoryDropdown, itemDropdown;
    private JSpinner quantitySpinner;

    // Inner class to represent an order item
    private static class OrderItem {
        String name;
        int quantity;
        double pricePerUnit;

        OrderItem(String name, int quantity, double pricePerUnit) {
            this.name = name;
            this.quantity = quantity;
            this.pricePerUnit = pricePerUnit;
        }

        double getTotal() {
            return quantity * pricePerUnit;
        }
    }

    public DowntownCafeBillingSystem() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Downtown Cafe Billing System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 900);
        setLocationRelativeTo(null);

        // Gradient background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.PINK, 0, getHeight(), Color.CYAN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // Logo (fallback to text if image not found)
        JLabel logoLabel = new JLabel("Downtown Cafe");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setBounds(20, 20, 300, 50);
        backgroundPanel.add(logoLabel);

        // Customer details
        addCustomerDetailFields(backgroundPanel);

        // Bill area
        billArea = new JTextArea();
        billArea.setBackground(Color.WHITE);
        billArea.setForeground(Color.BLACK);
        billArea.setFont(new Font("Arial", Font.PLAIN, 14));
        billArea.setEditable(false);
        billArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        JScrollPane scrollPane = new JScrollPane(billArea);
        scrollPane.setBounds(20, 200, 320, 200);
        backgroundPanel.add(scrollPane);

        // Total label
        totalLabel = new JLabel("Total: ₹0.00");
        totalLabel.setBounds(20, 410, 200, 30);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(new Color(0, 0, 102));
        backgroundPanel.add(totalLabel);

        // Discount field
        addDiscountField(backgroundPanel);

        // Final amount label
        finalAmountLabel = new JLabel("Final Amount: ₹0.00");
        finalAmountLabel.setBounds(20, 480, 250, 30);
        finalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        finalAmountLabel.setForeground(new Color(255, 69, 0));
        finalAmountLabel.setBorder(null);
        backgroundPanel.add(finalAmountLabel);

        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBounds(360, 200, 300, 150);
        backgroundPanel.add(buttonPanel);

        // Clear Bill Button
        JButton clearButton = createStyledButton("Clear Bill", new Color(255, 215, 0), Color.BLACK);
        clearButton.addActionListener(e -> clearBill());
        buttonPanel.add(clearButton);

        // Remove Last Item Button
        JButton removeButton = createStyledButton("Remove Last Item", new Color(255, 215, 0), Color.BLACK);
        removeButton.addActionListener(e -> removeLastItem());
        buttonPanel.add(removeButton);

        // Generate Bill Button
        JButton totalButton = createStyledButton("Generate Bill", new Color(255, 215, 0), Color.BLACK);
        totalButton.addActionListener(e -> applyDiscountAndGenerateTotal());
        buttonPanel.add(totalButton);

        // Category and item selection
        categoryDropdown = new JComboBox<>(new String[]{
                "Select Category", "Beverages", "Starters", "Salads", "Pasta", "Seafood", "Desserts", "Cocktails"
        });
        categoryDropdown.setBounds(20, 520, 150, 30);
        categoryDropdown.addActionListener(e -> populateItemDropdown());
        backgroundPanel.add(categoryDropdown);

        itemDropdown = new JComboBox<>();
        itemDropdown.setBounds(200, 520, 150, 30);
        itemDropdown.setEnabled(false);
        backgroundPanel.add(itemDropdown);

        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        quantitySpinner.setBounds(370, 520, 50, 30);
        backgroundPanel.add(quantitySpinner);

        JButton addItemButton = createStyledButton("Add Item", new Color(255, 215, 0), Color.BLACK);
        addItemButton.setBounds(440, 520, 100, 30);
        addItemButton.addActionListener(e -> addItemToBill());
        backgroundPanel.add(addItemButton);

        setVisible(true);
    }

    private void addCustomerDetailFields(JPanel backgroundPanel) {
        JLabel nameLabel = new JLabel("Customer Name:");
        nameLabel.setBounds(20, 80, 150, 30);
        backgroundPanel.add(nameLabel);

        customerNameField = new JTextField();
        customerNameField.setBounds(150, 80, 200, 30);
        backgroundPanel.add(customerNameField);

        JLabel phoneLabel = new JLabel("Phone Number:");
        phoneLabel.setBounds(20, 120, 150, 30);
        backgroundPanel.add(phoneLabel);

        phoneNumberField = new JTextField();
        phoneNumberField.setBounds(150, 120, 200, 30);
        backgroundPanel.add(phoneNumberField);

        JLabel billNumberLabel = new JLabel("Bill Number:");
        billNumberLabel.setBounds(20, 160, 150, 30);
        backgroundPanel.add(billNumberLabel);

        billNumberField = new JTextField();
        billNumberField.setBounds(150, 160, 200, 30);
        backgroundPanel.add(billNumberField);
    }

    private void addDiscountField(JPanel backgroundPanel) {
        JLabel discountLabel = new JLabel("Discount (%):");
        discountLabel.setBounds(20, 440, 150, 30);
        discountLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        discountLabel.setForeground(new Color(0, 0, 102));
        backgroundPanel.add(discountLabel);

        discountField = new JTextField();
        discountField.setBounds(150, 440, 50, 30);
        discountField.addActionListener(e -> updateFinalAmount());
        backgroundPanel.add(discountField);
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    private void populateItemDropdown() {
        itemDropdown.removeAllItems();
        String selectedCategory = (String) categoryDropdown.getSelectedItem();

        if ("Select Category".equals(selectedCategory)) {
            itemDropdown.setEnabled(false);
            return;
        }

        switch (selectedCategory) {
            case "Beverages":
                itemDropdown.addItem("Coffee (₹200)");
                itemDropdown.addItem("Tea (₹100)");
                itemDropdown.addItem("Juice (₹150)");
                itemDropdown.addItem("Soda (₹80)");
                itemDropdown.addItem("Sparkling Water (₹120)");
                itemDropdown.addItem("Smoothie (₹250)");
                break;
            case "Starters":
                itemDropdown.addItem("Bruschetta (₹600)");
                itemDropdown.addItem("Stuffed Mushrooms (₹800)");
                itemDropdown.addItem("Chicken Wings (₹700)");
                itemDropdown.addItem("Spring Rolls (₹500)");
                itemDropdown.addItem("Cheese Platter (₹900)");
                itemDropdown.addItem("Garlic Bread (₹400)");
                break;
            case "Salads":
                itemDropdown.addItem("Caesar Salad (₹900)");
                itemDropdown.addItem("Greek Salad (₹700)");
                itemDropdown.addItem("Quinoa Salad (₹800)");
                itemDropdown.addItem("Caprese Salad (₹750)");
                itemDropdown.addItem("Nicoise Salad (₹850)");
                itemDropdown.addItem("Asian Slaw Salad (₹650)");
                break;
            case "Pasta":
                itemDropdown.addItem("Lobster Linguine (₹3200)");
                itemDropdown.addItem("Truffle Mac & Cheese (₹1800)");
                itemDropdown.addItem("Wild Mushroom Ravioli (₹1500)");
                itemDropdown.addItem("Penne Arrabbiata (₹1200)");
                itemDropdown.addItem("Fettuccine Alfredo (₹1600)");
                itemDropdown.addItem("Spaghetti Carbonara (₹1400)");
                break;
            case "Seafood":
                itemDropdown.addItem("Grilled Salmon (₹4000)");
                itemDropdown.addItem("Shrimp Scampi (₹3500)");
                itemDropdown.addItem("Sea Bass with Lemon Butter (₹3800)");
                itemDropdown.addItem("Crab Cakes (₹2800)");
                itemDropdown.addItem("Tuna Tartare (₹3000)");
                itemDropdown.addItem("Lobster Tail (₹6000)");
                break;
            case "Desserts":
                itemDropdown.addItem("Chocolate Lava Cake (₹800)");
                itemDropdown.addItem("Cheesecake (₹700)");
                itemDropdown.addItem("Tiramisu (₹850)");
                itemDropdown.addItem("Panna Cotta (₹600)");
                itemDropdown.addItem("Macarons (₹1000)");
                itemDropdown.addItem("Fruit Tart (₹750)");
                break;
            case "Cocktails":
                itemDropdown.addItem("Mojito (₹600)");
                itemDropdown.addItem("Cosmopolitan (₹800)");
                itemDropdown.addItem("Martini (₹900)");
                itemDropdown.addItem("Margarita (₹750)");
                itemDropdown.addItem("Old Fashioned (₹1000)");
                itemDropdown.addItem("Pina Colada (₹700)");
                break;
        }

        itemDropdown.setEnabled(true);
    }

    private void addItemToBill() {
        String selectedItem = (String) itemDropdown.getSelectedItem();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(this, "Please select an item!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int quantity = (int) quantitySpinner.getValue();
        String itemName = selectedItem.split(" \\(")[0];
        double price = Double.parseDouble(selectedItem.substring(selectedItem.indexOf("₹") + 1, selectedItem.indexOf(")")));
        OrderItem orderItem = new OrderItem(itemName, quantity, price);
        orderItems.add(orderItem);

        double totalPrice = orderItem.getTotal();
        totalAmount += totalPrice;
        billArea.append(itemName + " x" + quantity + ": ₹" + String.format("%.2f", totalPrice) + "\n");
        totalLabel.setText("Total: ₹" + String.format("%.2f", totalAmount));
        updateFinalAmount();
    }

    private void updateFinalAmount() {
        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText());
            if (discount < 0 || discount > 100) discount = 0;
        } catch (NumberFormatException e) {
            discount = 0;
        }

        double discountAmount = (discount / 100) * totalAmount;
        double finalAmount = totalAmount - discountAmount;

        finalAmountLabel.setText("Final Amount: ₹" + String.format("%.2f", finalAmount));
    }

    private void applyDiscountAndGenerateTotal() {
        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText());
            if (discount < 0 || discount > 100) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid discount value! (0-100)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double discountAmount = (discount / 100) * totalAmount;
        double cgst = (totalAmount * 0.025);
        double sgst = (totalAmount * 0.025);
        double finalAmount = totalAmount - discountAmount + cgst + sgst;

        StringBuilder billSummary = new StringBuilder();
        billSummary.append("Bill Number: ").append(billNumberField.getText()).append("\n");
        billSummary.append("Customer: ").append(customerNameField.getText()).append("\n");
        billSummary.append("Phone: ").append(phoneNumberField.getText()).append("\n");
        billSummary.append("\n----- Your Order -----\n\n");

        for (OrderItem item : orderItems) {
            billSummary.append(item.name)
                .append(" x").append(item.quantity)
                .append(": ₹").append(String.format("%.2f", item.getTotal()))
                .append("\n");
        }

        billSummary.append("\nTotal Amount: ₹").append(String.format("%.2f", totalAmount))
                .append("\nDiscount: ₹").append(String.format("%.2f", discountAmount))
                .append("\nCGST (2.5%): ₹").append(String.format("%.2f", cgst))
                .append("\nSGST (2.5%): ₹").append(String.format("%.2f", sgst))
                .append("\nFinal Amount: ₹").append(String.format("%.2f", finalAmount))
                .append("\n\nThank you for dining with us! Come back soon!\n")
                .append("\n--- Downtown Cafe ---\n")
                .append("© 2024 Downtown Cafe. All Rights Reserved.");

        JOptionPane.showMessageDialog(this, billSummary.toString(), "Bill Summary", JOptionPane.INFORMATION_MESSAGE);
    }

    private void removeLastItem() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items to remove!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        OrderItem lastItem = orderItems.remove(orderItems.size() - 1);
        totalAmount -= lastItem.getTotal();

        // Rebuild bill area
        billArea.setText("");
        for (OrderItem item : orderItems) {
            billArea.append(item.name + " x" + item.quantity + ": ₹" + String.format("%.2f", item.getTotal()) + "\n");
        }
        totalLabel.setText("Total: ₹" + String.format("%.2f", totalAmount));
        updateFinalAmount();
    }

    private void clearBill() {
        totalAmount = 0.0;
        orderItems.clear();
        billArea.setText("");
        totalLabel.setText("Total: ₹0.00");
        discountField.setText("");
        customerNameField.setText("");
        phoneNumberField.setText("");
        billNumberField.setText("");
        finalAmountLabel.setText("Final Amount: ₹0.00");
        categoryDropdown.setSelectedIndex(0);
        itemDropdown.removeAllItems();
        itemDropdown.setEnabled(false);
        quantitySpinner.setValue(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DowntownCafeBillingSystem::new);
    }
}