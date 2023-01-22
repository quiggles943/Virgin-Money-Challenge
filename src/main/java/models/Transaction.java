package models;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {

    private LocalDate transactionDate;
    private String Vendor;
    private String transactionType;
    private BigDecimal amount;
    private String category = "";

    public Transaction(LocalDate date,String vendor,String type,BigDecimal amount){
        this.transactionDate = date;
        this.Vendor = vendor;
        this.transactionType = type;
        this.amount = amount.setScale(2, RoundingMode.HALF_EVEN);
    }

    public Transaction(LocalDate date,String vendor,String type,BigDecimal amount, String category){
        this(date,vendor,type,amount);
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }
    public String getTransactionDateString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        return transactionDate.format(formatter);
    }

    public String getTransactionType() {
        return transactionType;
    }

    public String getVendor() {
        return Vendor;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getMonthAndYear(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM/yyyy");
        return transactionDate.format(formatter);
    }

    public Object[] toArray() {
        Object[] array = new Object[5];
        array[0] = getTransactionDateString();
        array[1] = getVendor();
        array[2] = getTransactionType();
        array[3] = getAmount();
        array[4] = getCategory();
        return array;
    }
}
