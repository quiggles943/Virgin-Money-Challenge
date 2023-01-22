package calculation;

import calculation.enums.SortType;
import models.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatisticsGenerator {

    private List<Transaction> transactionList = new ArrayList<>();

    /**
     * Used to calculate statistics of a users transactions
     */
    public StatisticsGenerator(){

    }

    public void setTransactionList(List<Transaction> transactions){
        this.transactionList = transactions;
    }

    /**
     * Returns all the transactions for a given category sorted as specified
     * @param category the category to filter by
     * @param sortType the order to sort them
     * @return a list of transactions of a given category
     */
    public List<Transaction> getTransactionsForCategory(String category, SortType sortType){
        Comparator comparator;
        String sortName = sortType.key;
        switch (sortType){
            case DATE_ASC:
                comparator = Comparator.comparing(Transaction::getTransactionDate);
                break;
            case DATE_DESC:
                comparator = Comparator.comparing(Transaction::getTransactionDate).reversed();
                break;
            default:
                comparator = Comparator.comparing(Transaction::getTransactionDate);
        }
        return getTransactionsFromList(category,comparator);
    }

    /**
     * Returns the monthly average amount spent for a given category over all transactions
     * @param category the category to filter on
     * @return the average spent each month for a category
     */
    public BigDecimal getAverageSpend(String category){
        Map<String,BigDecimal> spendingByMonth = new HashMap<>();
        Map<String,List<Transaction>> filteredTransactions = transactionList.stream().filter(x-> x.getCategory().equalsIgnoreCase(category)).collect(Collectors.groupingBy(Transaction::getMonthAndYear));
        for(Map.Entry<String,List<Transaction>> entry : filteredTransactions.entrySet()){
            spendingByMonth.put(entry.getKey(), getTotalSpendingFromTransactions(entry.getValue()));
        }
        return getAverageSpendFromMonths(spendingByMonth);
    }

    /**
     * Returns the highest amount spent in a single transaction for a given year and given category
     * @param category The category to filter by
     * @param year The year you would like to filter by
     * @return The highest amount spent in a single transaction
     */
    public BigDecimal getHighestSpendForYear(String category, int year){
        List<Transaction> filteredTransactions = getTransactionsFromList(category);
        Optional<Transaction> highestSpendTransaction = filteredTransactions.stream().filter(x->x.getTransactionDate().getYear() == year).sorted(Comparator.comparing(Transaction::getAmount).reversed()).findFirst();
        if(highestSpendTransaction.isPresent())
            return highestSpendTransaction.get().getAmount();
        else
            return BigDecimal.ZERO;
    }
    /**
     * Returns the lowest amount spent in a single transaction for a given year and given category
     * @param category The category to filter by
     * @param year The year you would like to filter by
     * @return The lowest amount spent in a single transaction
     */
    public BigDecimal getLowestSpendForYear(String category, int year){
        List<Transaction> filteredTransactions = getTransactionsFromList(category);
        Optional<Transaction> lowestSpendTransaction = filteredTransactions.stream().filter(x->x.getTransactionDate().getYear() == year).sorted(Comparator.comparing(Transaction::getAmount)).findFirst();
        if(lowestSpendTransaction.isPresent())
            return lowestSpendTransaction.get().getAmount();
        else
            return BigDecimal.ZERO;
    }

    /**
     * Returns the total spent for a given category
     * @param category The category to filter by
     * @return The total amount spent
     */
    public BigDecimal getTotalSpendForCategory(String category){
        List<Transaction> filteredTransactions = getTransactionsFromList(category);
        return filteredTransactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);

    }

    /**
     * Calculates the total amount spent for every category and returns a map with the category as the key and the total amount for that category as the value
     * @return The map containing the totals
     */
    public Map<String,BigDecimal> getTotalSpendByCategory(){
        Map<String,BigDecimal> spendingByCategory = new HashMap<>();
        Map<String,List<Transaction>> transactionsByCategory = transactionList.stream().collect(Collectors.groupingBy(Transaction::getCategory));

        for(Map.Entry<String,List<Transaction>> entry : transactionsByCategory.entrySet()){
            BigDecimal categoryTotal = entry.getValue().stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO,BigDecimal::add);
            spendingByCategory.put(entry.getKey(),categoryTotal);
        }
        return spendingByCategory;
    }



    /**
     * Takes a list of transactions and calculates the total spend
     * @param transactions the list of transactions to calculate
     * @return the total spent from all the transactions
     */
    public BigDecimal getTotalSpendingFromTransactions(List<Transaction> transactions){
        BigDecimal monthlySpend = BigDecimal.ZERO;
        for(Transaction transaction : transactions){
            monthlySpend = monthlySpend.add(transaction.getAmount());
        }
        return monthlySpend;
    }

    /**
     * Generates the average spend over a series of months
     * @param spendingByMonth A map representing the month/year identifier and the total spend in each month
     * @return the average over the given months
     */
    public BigDecimal getAverageSpendFromMonths(Map<String,BigDecimal> spendingByMonth){

        BigDecimal total = spendingByMonth.values().stream().reduce(BigDecimal.ZERO,BigDecimal::add);
        if(total.equals(BigDecimal.ZERO)){
            return BigDecimal.ZERO;
        }

        BigDecimal divisor = BigDecimal.valueOf(spendingByMonth.values().size());
        BigDecimal average = total.divide(divisor,RoundingMode.HALF_EVEN);
        return average;
    }

    /**
     * Retrieves the transactions that match the given category
     * @param category The category to filter on
     * @return The transactions that match the category
     */
    public List<Transaction> getTransactionsFromList(String category){
        return getTransactionsFromList(category,null);
    }

    /**
     * Retrueves the transactions that match the given category sorted by the given comparator
     * @param category The category to filter on
     * @param comparator The comparator used to sort the results
     * @return
     */
    public List<Transaction> getTransactionsFromList(String category, Comparator comparator){
        Stream<Transaction> transactionStream = transactionList.stream().filter(x-> x.getCategory().equalsIgnoreCase(category));
        if(comparator != null)
            transactionStream = transactionStream.sorted(comparator);
        return transactionStream.collect(Collectors.toList());
    }
}
