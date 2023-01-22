import calculation.StatisticsGenerator;
import calculation.enums.SortType;
import models.Transaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainWindow {
    private JPanel rootPanel;
    private JTable contentTable;
    private JRadioButton allTransactionsForACategoryButton;
    private JRadioButton totalOutgoingPerCategoryRadioButton;
    private JRadioButton monthlyAverageSpendInRadioButton;
    private JRadioButton highestSpendInARadioButton;
    private JRadioButton lowestSpendInARadioButton;
    private ButtonGroup selectionGroup;
    private JComboBox categorySelector;
    private JButton calculateButton;
    private JTextField yearEntry;
    private JLabel infoLabel;
    private JLabel yearLabel;
    private JTextPane resultPane;
    private StatisticsGenerator statisticsGenerator;

    public static void main(String[] args){
        JFrame frame = new JFrame();
        frame.setContentPane(new MainWindow().rootPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Virgin Money Challenge");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public MainWindow(){
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                infoLabel.setText("");
                String resultText= "";
                switch (getSelectedRadioButton().getActionCommand()){
                    case "allForCategory":
                        List<Transaction> transactionsForCategory = statisticsGenerator.getTransactionsForCategory(getSelectedCategory(), SortType.DATE_DESC);

                        for(Transaction transaction : transactionsForCategory){
                            resultText = resultText+transaction.getTransactionDateString()+" "+transaction.getVendor()+" £"+transaction.getAmount()+"\n";
                        }

                        break;
                    case "totalPerCategory":
                        Map<String,BigDecimal> totalPerCategory = statisticsGenerator.getTotalSpendByCategory();
                        for(Map.Entry<String,BigDecimal> entry : totalPerCategory.entrySet()){
                            if(entry.getKey().isEmpty())
                                resultText = resultText + "No Category"+":£"+entry.getValue()+"\n";
                            else
                                resultText = resultText + entry.getKey()+":£"+entry.getValue()+"\n";
                        }
                        break;
                    case "monthlySpendInCategory":
                        BigDecimal average = statisticsGenerator.getAverageSpend(getSelectedCategory());
                        resultText = "£"+average;
                        break;
                    case "highestSpendInCategory":
                        try {
                            int year = Integer.parseInt(yearEntry.getText());
                            BigDecimal highest;
                            if(year >0)
                                highest = statisticsGenerator.getHighestSpendForYear(getSelectedCategory(),year);
                            else
                                highest = BigDecimal.ZERO;
                            resultText = "£"+highest;
                        }catch (NumberFormatException ex){
                            infoLabel.setText("Unable to convert year");
                        }

                        break;
                    case "lowestSpendInCategory":
                        try {
                            int year = Integer.parseInt(yearEntry.getText());
                            BigDecimal lowest;
                            if(year >0)
                                lowest = statisticsGenerator.getLowestSpendForYear(getSelectedCategory(),year);
                            else
                                lowest = BigDecimal.ZERO;
                            resultText = "£"+lowest;
                        }catch (NumberFormatException ex){
                            infoLabel.setText("Unable to convert year");
                        }
                        break;
                    default:
                        infoLabel.setText("No option selected");
                }
                resultPane.setText(resultText);
            }
        });
        setupRadioButtons();
        statisticsGenerator = new StatisticsGenerator();
        ArrayList<Transaction> transactions = generateContent();
        populateTable(transactions);
        populateCategorySelector(transactions);
        statisticsGenerator.setTransactionList(transactions);
        BigDecimal total = statisticsGenerator.getTotalSpendForCategory("OnlineShopping");
        List<Transaction> transactionsForCategory = statisticsGenerator.getTransactionsForCategory("OnlineShopping",SortType.DATE_DESC);
        transactionsForCategory.size();
    }

    private void setupRadioButtons(){
        allTransactionsForACategoryButton.setActionCommand("allForCategory");
        allTransactionsForACategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yearEntry.setEditable(false);
                categorySelector.setEnabled(true);
                calculateButton.setEnabled(true);
                yearLabel.setText("");
                rootPanel.repaint();
            }
        });
        totalOutgoingPerCategoryRadioButton.setActionCommand("totalPerCategory");
        totalOutgoingPerCategoryRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yearEntry.setEditable(false);
                categorySelector.setEnabled(false);
                calculateButton.setEnabled(true);
                yearLabel.setText("");
                rootPanel.repaint();
            }
        });
        monthlyAverageSpendInRadioButton.setActionCommand("monthlySpendInCategory");
        monthlyAverageSpendInRadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yearEntry.setEditable(false);
                categorySelector.setEnabled(true);
                calculateButton.setEnabled(true);
                yearLabel.setText("");
                rootPanel.repaint();
            }
        });
        highestSpendInARadioButton.setActionCommand("highestSpendInCategory");
        highestSpendInARadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yearEntry.setEditable(true);
                categorySelector.setEnabled(true);
                calculateButton.setEnabled(true);
                yearLabel.setText("Year");
                rootPanel.repaint();
            }
        });
        lowestSpendInARadioButton.setActionCommand("lowestSpendInCategory");
        lowestSpendInARadioButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                yearEntry.setEditable(true);
                categorySelector.setEnabled(true);
                calculateButton.setEnabled(true);
                yearLabel.setText("Year");
                rootPanel.repaint();

            }
        });
        selectionGroup = new ButtonGroup();
        selectionGroup.add(allTransactionsForACategoryButton);
        selectionGroup.add(totalOutgoingPerCategoryRadioButton);
        selectionGroup.add(monthlyAverageSpendInRadioButton);
        selectionGroup.add(highestSpendInARadioButton);
        selectionGroup.add(lowestSpendInARadioButton);
    }

    private ArrayList<Transaction> generateContent(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2020,11, 1),"Morrisons","card", BigDecimal.valueOf(10.40),"Groceries"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,28),"CYBG","direct debit",BigDecimal.valueOf(600),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,28),"PureGym","direct debit",BigDecimal.valueOf(40),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,1),"M&S","card",BigDecimal.valueOf(5.99),"Groceries"));
        transactions.add(new Transaction(LocalDate.of(2020,9,30),"McMillan","internet",BigDecimal.valueOf(10)));

        transactions.add(new Transaction(LocalDate.of(2020,8,15),"Amazon","internet",BigDecimal.valueOf(31.98),"OnlineShopping"));
        transactions.add(new Transaction(LocalDate.of(2020,9,21),"Amazon","internet",BigDecimal.valueOf(12.99),"OnlineShopping"));
        transactions.add(new Transaction(LocalDate.of(2020,11,8),"Amazon","internet",BigDecimal.valueOf(42.32),"OnlineShopping"));
        transactions.add(new Transaction(LocalDate.of(2020, 9,28),"CYBG","direct debit",BigDecimal.valueOf(600),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 9,28),"PureGym","direct debit",BigDecimal.valueOf(40),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 1,28),"CYBG","direct debit",BigDecimal.valueOf(600),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 1,28),"PureGym","direct debit",BigDecimal.valueOf(40),"MyMonthlyDD"));
        return transactions;
    }

    private void populateTable(ArrayList<Transaction> transactions){
        DefaultTableModel tableModel = (DefaultTableModel) contentTable.getModel();
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        tableModel.addColumn("Date");
        tableModel.addColumn("Vendor");
        tableModel.addColumn("Type");
        tableModel.addColumn("Amount");
        tableModel.addColumn("Category");
        for(Transaction transaction : transactions){
            tableModel.addRow(transaction.toArray());
        }
    }

    private void populateCategorySelector(ArrayList<Transaction> transactions){
        List<String> categories = getCatagoriesFromTransactions(transactions);
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) categorySelector.getModel();
        comboBoxModel.addAll(categories);

    }

    private List<String> getCatagoriesFromTransactions(ArrayList<Transaction> transactions){
        List<String> categories = transactions.stream().map(Transaction::getCategory).distinct().sorted().collect(Collectors.toList());
        return categories;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        contentTable = new JTable(){
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        contentTable.setAutoCreateRowSorter(true);

    }

    private JRadioButton getSelectedRadioButton(){
        Enumeration<AbstractButton> buttons = selectionGroup.getElements();
        while(buttons.hasMoreElements()){
            JRadioButton radioButton = (JRadioButton) buttons.nextElement();
            if(radioButton.isSelected())
                return radioButton;
        }
        return null;
    }

    private String getSelectedCategory(){
        String result = "";
        if(categorySelector.getModel().getSelectedItem() != null)
            result = categorySelector.getModel().getSelectedItem().toString();
        return result;
    }
}
