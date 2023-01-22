package models;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class TransactionTableModel extends AbstractTableModel {
    List<Transaction> transactionList;
    String[] headerList = {"Date","Vendor","Type","Amount","Category"};
    Class[] classes = { LocalDate.class, String.class, String.class, BigDecimal.class,String.class};

    public TransactionTableModel(List<Transaction> transactions){
        transactionList = transactions;
    }
    @Override
    public int getColumnCount() {
        return headerList.length;
    }

    @Override
    public int getRowCount() {
        return transactionList.size();
    }

    @Override
    public Class<?> getColumnClass(int arg0) {
        // TODO Auto-generated method stub
        return classes[arg0];
    }

    @Override
    public Object getValueAt(int row, int column) {
        Transaction entity = null;
        entity = transactionList.get(row);
        switch (column) {
            case 0:
                return entity.getTransactionDate();
            case 1:
                return entity.getVendor();
            case 2:
                return entity.getTransactionType();
            case 3:
                return "£"+entity.getAmount();
            case 4:
                return entity.getCategory();
            default:
                return "";
        }
    }

    public String getColumnName(int col) {
        return headerList[col];
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Transaction entity = null;
        entity = transactionList.get(row);
        switch (col) {
            case 4:
                entity.setCategory((String) value);
            default:
                break;

        }
        fireTableCellUpdated(row, col);
    }
}
