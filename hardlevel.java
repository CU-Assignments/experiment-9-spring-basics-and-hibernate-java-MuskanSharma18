// Account.java
package com.example.banking;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_number", unique = true)
    private String accountNumber;
    
    @Column(name = "owner_name")
    private String ownerName;
    
    @Column(name = "balance", precision = 19, scale = 2)
    private BigDecimal balance;
    
    // Default constructor
    public Account() {
    }
    
    // Constructor with parameters
    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public BigDecimal getBalance() {
        return balance;
    }
    
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    
    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' +
                ", balance=" + balance +
                '}';
    }
}

// Transaction.java
package com.example.banking;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source_account_id")
    private Long sourceAccountId;
    
    @Column(name = "target_account_id")
    private Long targetAccountId;
    
    @Column(name = "amount", precision = 19, scale = 2)
    private BigDecimal amount;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    @Column(name = "status")
    private String status; // SUCCESS, FAILED
    
    // Default constructor
    public Transaction() {
        this.transactionDate = LocalDateTime.now();
    }
    
    // Constructor with parameters
    public Transaction(Long sourceAccountId, Long targetAccountId, BigDecimal amount) {
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
        this.amount = amount;
        this.transactionDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getSourceAccountId() {
        return sourceAccountId;
    }
    
    public void setSourceAccountId(Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }
    
    public Long getTargetAccountId() {
        return targetAccountId;
    }
    
    public void setTargetAccountId(Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", sourceAccountId=" + sourceAccountId +
                ", targetAccountId=" + targetAccountId +
                ", amount=" + amount +
                ", transactionDate=" + transactionDate +
                ", status='" + status + '\'' +
                '}';
    }
}

// AccountRepository.java
package com.example.banking.repository;

import com.example.banking.Account;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AccountRepository {
    
    private final SessionFactory sessionFactory;
    
    @Autowired
    public AccountRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public Account findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Account.class, id);
    }
    
    public Account findByAccountNumber(String accountNumber) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Account> query = session.createQuery(
                "FROM Account WHERE accountNumber = :accountNumber", Account.class);
        query.setParameter("accountNumber", accountNumber);
        List<Account> results = query.getResultList();
        return results.isEmpty() ? null : results.get(0);
    }
    
    public void save(Account account) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(account);
    }
    
    public List<Account> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Account", Account.class).getResultList();
    }
}

// TransactionRepository.java
package com.example.banking.repository;

import com.example.banking.Transaction;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TransactionRepository {
    
    private final SessionFactory sessionFactory;
    
    @Autowired
    public TransactionRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    public void save(Transaction transaction) {
        Session session = sessionFactory.getCurrentSession();
        session.saveOrUpdate(transaction);
    }
    
    public Transaction findById(Long id) {
        Session session = sessionFactory.getCurrentSession();
        return session.get(Transaction.class, id);
    }
    
    public List<Transaction> findBySourceAccountId(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "FROM Transaction WHERE sourceAccountId = :accountId", 
                Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }
    
    public List<Transaction> findByTargetAccountId(Long accountId) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                "FROM Transaction WHERE targetAccountId = :accountId", 
                Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }
    
    public List<Transaction> findAll() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("FROM Transaction", Transaction.class).getResultList();
    }
}

// BankService.java
package com.example.banking.service;

import com.example.banking.Account;
import com.example.banking.Transaction;
import com.example.banking.repository.AccountRepository;
import com.example.banking.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class BankService {
    
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    
    @Autowired
    public BankService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }
    
    @Transactional
    public Account createAccount(Account account) {
        accountRepository.save(account);
        return account;
    }
    
    @Transactional(readOnly = true)
    public Account getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
    
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    
    @Transactional
    public Transaction transferMoney(String sourceAccountNumber, String targetAccountNumber, BigDecimal amount) {
        Account sourceAccount = accountRepository.findByAccountNumber(sourceAccountNumber);
        Account targetAccount = accountRepository.findByAccountNumber(targetAccountNumber);
        
        // Validate accounts and amount
        if (sourceAccount == null || targetAccount == null) {
            throw new IllegalArgumentException("Source or target account not found");
        }
        
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive");
        }
        
        if (sourceAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds in source account");
        }
        
        // Create transaction record
        Transaction transaction = new Transaction(sourceAccount.getId(), targetAccount.getId(), amount);
        
        try {
            // Update balances
            sourceAccount.setBalance(sourceAccount.getBalance().subtract(amount));
            targetAccount.setBalance(targetAccount.getBalance().add(amount));
            
            // Save updated accounts
            accountRepository.save(sourceAccount);
            accountRepository.save(targetAccount);
            
            // Mark transaction as successful
            transaction.setStatus("SUCCESS");
            
        } catch (Exception e) {
            // Mark transaction as failed
            transaction.setStatus("FAILED");
            // Rethrow to trigger rollback
            throw e;
        } finally {
            // Save transaction record regardless of outcome
            transactionRepository.save(transaction);
        }
        
        return transaction;
    }
    
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}

// ApplicationConfig.java
package com.example.banking.config;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@EnableTransactionManagement
@ComponentScan(basePackages = "com.example.banking")
public class Applic
