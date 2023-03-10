import calculation.StatisticsGenerator;
import calculation.enums.SortType;
import models.Transaction;
import models.TransactionTableModel;

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
    private JButton assignCategoryButton;
    private StatisticsGenerator statisticsGenerator;

    private List<Transaction> listedTransactions;

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
                            resultText = resultText+transaction.getTransactionDateString()+" "+transaction.getVendor()+" ??"+transaction.getAmount()+"\n";
                        }

                        break;
                    case "totalPerCategory":
                        Map<String,BigDecimal> totalPerCategory = statisticsGenerator.getTotalSpendByCategory();
                        for(Map.Entry<String,BigDecimal> entry : totalPerCategory.entrySet()){
                            if(entry.getKey().isEmpty())
                                resultText = resultText + "No Category"+":??"+entry.getValue()+"\n";
                            else
                                resultText = resultText + entry.getKey()+":??"+entry.getValue()+"\n";
                        }
                        break;
                    case "monthlySpendInCategory":
                        BigDecimal average = statisticsGenerator.getAverageSpend(getSelectedCategory());
                        resultText = "??"+average;
                        break;
                    case "highestSpendInCategory":
                        try {
                            int year = Integer.parseInt(yearEntry.getText());
                            BigDecimal highest;
                            if(year >0)
                                highest = statisticsGenerator.getHighestSpendForYear(getSelectedCategory(),year);
                            else
                                highest = BigDecimal.ZERO;
                            resultText = "??"+highest;
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
                            resultText = "??"+lowest;
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
        assignCategoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = contentTable.getSelectedRow();
                if(selectedRow == -1)
                    return;
                String result = JOptionPane.showInputDialog("Assign category to selected transaction");
                if(result != null){
                    Transaction transaction = listedTransactions.get(contentTable.convertRowIndexToModel(selectedRow));
                    transaction.setCategory(result);
                    populateCategorySelector(listedTransactions);
                    rootPanel.repaint();
                }
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

    /**
     * Setups up the functionality of the radio buttons
     */
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

    /**
     * Generates the transaction data used by the program
     * @return
     */
    private ArrayList<Transaction> generateContent(){
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(new Transaction(LocalDate.of(2020,11, 1),"Morrisons","card", BigDecimal.valueOf(10.40),"Groceries"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,28),"CYBG","direct debit",BigDecimal.valueOf(600),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,28),"PureGym","direct debit",BigDecimal.valueOf(40),"MyMonthlyDD"));
        transactions.add(new Transaction(LocalDate.of(2020, 10,1),"M&S","card",BigDecimal.valueOf(5.99),"Groceries"));
        transactions.add(new Transaction(LocalDate.of(2020,9,30),"McMillan","internet",BigDecimal.valueOf(10)));
        listedTransactions = transactions;
        return transactions;
    }

    /**
     * Populates the content table
     * @param transactions The transactions to add to the table
     */
    private void populateTable(ArrayList<Transaction> transactions){
        TransactionTableModel transactionTableModel = new TransactionTableModel(transactions);
        contentTable.setModel(transactionTableModel);
    }

    /**
     * Populates the category selector using the given transactions
     * @param transactions The transactions used to generate the category list
     */
    private void populateCategorySelector(List<Transaction> transactions){
        List<String> categories = getCatagoriesFromTransactions(transactions);
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) categorySelector.getModel();
        comboBoxModel.removeAllElements();
        comboBoxModel.addAll(categories);

    }

    /**
     * Retrieves a list of the categories used in the transactions supplied
     * @param transactions The transactions used to generate the category list
     * @return The list of unique categories mentioned in the transactions supplied
     */
    private List<String> getCatagoriesFromTransactions(List<Transaction> transactions){
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

    /**
     * Returns the selected radio button
     * @return The radio button selected or null if none selected
     */
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
