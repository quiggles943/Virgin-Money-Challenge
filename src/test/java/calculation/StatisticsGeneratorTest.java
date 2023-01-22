package calculation;

import models.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StatisticsGeneratorTest {


    @Test
    public void testGetTransactionsFromList(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction includedTransaction1 = new Transaction(LocalDate.of(2022,12,25),"Tesco","card", BigDecimal.valueOf(56.32),"Shopping");
        Transaction includedTransaction2 = new Transaction(LocalDate.of(2022,12,5),"Morrisons","card", BigDecimal.valueOf(10.99),"Shopping");
        Transaction notIncludedTransaction = new Transaction(LocalDate.of(2022,11,13),"Staples","card", BigDecimal.valueOf(30.99),"Work");
        transactionList.add(includedTransaction1);
        transactionList.add(notIncludedTransaction);
        transactionList.add(includedTransaction2);
        generator.setTransactionList(transactionList);
        List<Transaction> expectedList = new ArrayList<>();
        expectedList.add(includedTransaction1);
        expectedList.add(includedTransaction2);

        assertEquals(expectedList,generator.getTransactionsFromList("Shopping"));
    }

    @Test
    public void testGetTotalSpending(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction includedTransaction1 = new Transaction(LocalDate.of(2022,12,25),"Tesco","card", BigDecimal.valueOf(10.50),"Shopping");
        Transaction includedTransaction2 = new Transaction(LocalDate.of(2022,12,5),"Morrisons","card", BigDecimal.valueOf(9.50),"Shopping");
        transactionList.add(includedTransaction1);
        transactionList.add(includedTransaction2);
        generator.setTransactionList(transactionList);
        assertEquals(BigDecimal.valueOf(20.00d).setScale(2),generator.getTotalSpendingFromTransactions(transactionList));
    }

    @Test
    public void testGetTotalSpendForCategory(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction includedTransaction1 = new Transaction(LocalDate.of(2022,12,25),"Tesco","card", BigDecimal.valueOf(56.32),"Shopping");
        Transaction includedTransaction2 = new Transaction(LocalDate.of(2022,12,5),"Morrisons","card", BigDecimal.valueOf(10.99),"Shopping");
        Transaction notIncludedTransaction1 = new Transaction(LocalDate.of(2022,11,13),"Staples","card", BigDecimal.valueOf(30.99),"Work");
        Transaction notIncludedTransaction2 = new Transaction(LocalDate.of(2022,11,13),"Starbucks","card", BigDecimal.valueOf(4.50));
        transactionList.add(includedTransaction1);
        transactionList.add(notIncludedTransaction1);
        transactionList.add(includedTransaction2);
        transactionList.add(notIncludedTransaction2);
        generator.setTransactionList(transactionList);

        assertEquals(BigDecimal.valueOf(67.31d).setScale(2),generator.getTotalSpendForCategory("Shopping"));
    }

    @Test
    public void testGetAverageSpend(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction1 = new Transaction(LocalDate.of(2022,12,25),"Tesco","card", BigDecimal.valueOf(56.32),"Shopping");
        Transaction transaction2 = new Transaction(LocalDate.of(2022,12,5),"Morrisons","card", BigDecimal.valueOf(10.99),"Shopping");
        Transaction transaction3 = new Transaction(LocalDate.of(2022,11,13),"Staples","card", BigDecimal.valueOf(30.99),"Shopping");
        Transaction transaction4 = new Transaction(LocalDate.of(2022,11,13),"Starbucks","card", BigDecimal.valueOf(4.50));
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        transactionList.add(transaction4);
        generator.setTransactionList(transactionList);

        BigDecimal expected = BigDecimal.valueOf(49.15).setScale(2, RoundingMode.HALF_EVEN);
        assertEquals(expected,generator.getAverageSpend("Shopping"));
    }

    @Test
    public void testGetHighestSpendForYear(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction1 = new Transaction(LocalDate.of(2021,12,25),"Tesco","card", BigDecimal.valueOf(56.32),"Shopping");
        Transaction transaction2 = new Transaction(LocalDate.of(2022,12,5),"Morrisons","card", BigDecimal.valueOf(10.99),"Shopping");
        Transaction transaction3 = new Transaction(LocalDate.of(2022,11,13),"Staples","card", BigDecimal.valueOf(30.99),"Shopping");
        Transaction transaction4 = new Transaction(LocalDate.of(2022,11,13),"Starbucks","card", BigDecimal.valueOf(4.50));
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        transactionList.add(transaction4);
        generator.setTransactionList(transactionList);

        BigDecimal expected = BigDecimal.valueOf(30.99).setScale(2, RoundingMode.HALF_EVEN);
        assertEquals(expected,generator.getHighestSpendForYear("Shopping",2022));
    }

    @Test
    public void testGetLowestSpendForYear(){
        StatisticsGenerator generator = new StatisticsGenerator();
        List<Transaction> transactionList = new ArrayList<>();
        Transaction transaction1 = new Transaction(LocalDate.of(2022,12,25),"Tesco","card", BigDecimal.valueOf(56.32),"Shopping");
        Transaction transaction2 = new Transaction(LocalDate.of(2021,12,5),"Morrisons","card", BigDecimal.valueOf(10.99),"Shopping");
        Transaction transaction3 = new Transaction(LocalDate.of(2022,11,13),"Staples","card", BigDecimal.valueOf(30.99),"Shopping");
        Transaction transaction4 = new Transaction(LocalDate.of(2022,11,13),"Starbucks","card", BigDecimal.valueOf(4.50));
        transactionList.add(transaction1);
        transactionList.add(transaction2);
        transactionList.add(transaction3);
        transactionList.add(transaction4);
        generator.setTransactionList(transactionList);

        BigDecimal expected = BigDecimal.valueOf(30.99).setScale(2, RoundingMode.HALF_EVEN);
        assertEquals(expected,generator.getLowestSpendForYear("Shopping",2022));
    }
}
