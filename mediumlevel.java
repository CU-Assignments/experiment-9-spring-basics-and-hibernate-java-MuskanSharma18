// Student.java
package com.example.hibernate;

import javax.persistence.*;

@Entity
@Table(name = "students")
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "age")
    private int age;
    
    // Default constructor required by Hibernate
    public Student() {
    }
    
    // Constructor with parameters
    public Student(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getAge() {
        return age;
    }
    
    public void setAge(int age) {
        this.age = age;
    }
    
    @Override
    public String toString() {
        return "Student [id=" + id + ", name=" + name + ", age=" + age + "]";
    }
}

// StudentDAO.java
package com.example.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class StudentDAO {
    private final SessionFactory sessionFactory;
    
    public StudentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    
    // Create a new student
    public void saveStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.save(student);
            tx.commit();
            System.out.println("Student saved successfully!");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    // Read a student by ID
    public Student getStudentById(int id) {
        Session session = sessionFactory.openSession();
        Student student = null;
        
        try {
            student = session.get(Student.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return student;
    }
    
    // Update a student
    public void updateStudent(Student student) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            session.update(student);
            tx.commit();
            System.out.println("Student updated successfully!");
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    // Delete a student
    public void deleteStudent(int id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            Student student = session.get(Student.class, id);
            if (student != null) {
                session.delete(student);
                System.out.println("Student deleted successfully!");
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    // Get all students
    public List<Student> getAllStudents() {
        Session session = sessionFactory.openSession();
        List<Student> students = null;
        
        try {
            students = session.createQuery("from Student", Student.class).list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        
        return students;
    }
}

// HibernateUtil.java
package com.example.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
    
    public static void shutdown() {
        // Close caches and connection pools
        getSessionFactory().close();
    }
}

// MainApp.java
package com.example.hibernate;

import org.hibernate.SessionFactory;

import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        // Get the SessionFactory
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        
        // Create StudentDAO
        StudentDAO studentDAO = new StudentDAO(sessionFactory);
        
        // Create operation
        System.out.println("Creating students...");
        Student student1 = new Student("Alice Smith", 20);
        Student student2 = new Student("Bob Johnson", 22);
        studentDAO.saveStudent(student1);
        studentDAO.saveStudent(student2);
        
        // Read operation
        System.out.println("\nListing all students...");
        List<Student> students = studentDAO.getAllStudents();
        for (Student student : students) {
            System.out.println(student);
        }
        
        // Update operation
        System.out.println("\nUpdating student...");
        Student firstStudent = students.get(0);
        firstStudent.setAge(21);
        studentDAO.updateStudent(firstStudent);
        
        // Display updated record
        System.out.println("\nStudent after update:");
        Student updatedStudent = studentDAO.getStudentById(firstStudent.getId());
        System.out.println(updatedStudent);
        
        // Delete operation
        System.out.println("\nDeleting student...");
        studentDAO.deleteStudent(students.get(1).getId());
        
        // Display remaining records
        System.out.println("\nRemaining students:");
        students = studentDAO.getAllStudents();
        for (Student student : students) {
            System.out.println(student);
        }
        
        // Close the SessionFactory
        HibernateUtil.shutdown();
    }
}

// hibernate.cfg.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE hibernate-configuration PUBLIC
        "-//Hibernate/Hibernate Configuration DTD 3.0//EN"
        "http://www.hibernate.org/dtd/hibernate-configuration-3.0.dtd">
<hibernate-configuration>
    <session-factory>
        <!-- Database connection settings -->
        <property name="hibernate.connection.driver_class">com.mysql.cj.jdbc.Driver</property>
        <property name="hibernate.connection.url">jdbc:mysql://localhost:3306/hibernate_db?createDatabaseIfNotExist=true&amp;useSSL=false&amp;serverTimezone=UTC</property>
        <property name="hibernate.connection.username">root</property>
        <property name="hibernate.connection.password">password</property>
        
        <!-- JDBC connection pool settings -->
        <property name="hibernate.c3p0.min_size">5</property>
        <property name="hibernate.c3p0.max_size">20</property>
        <property name="hibernate.c3p0.timeout">300</property>
        <property name="hibernate.c3p0.max_statements">50</property>
        <property name="hibernate.c3p0.idle_test_period">3000</property>
        
        <!-- SQL dialect -->
        <property name="hibernate.dialect">org.hibernate.dialect.MySQL8Dialect</property>
        
        <!-- Enable Hibernate's automatic session context management -->
        <property name="hibernate.current_session_context_class">thread</property>
        
        <!-- Echo all executed SQL to stdout -->
        <property name="hibernate.show_sql">true</property>
        <property name="hibernate.format_sql">true</property>
        
        <!-- Drop and re-create the database schema on startup -->
        <property name="hibernate.hbm2ddl.auto">update</property>
        
        <!-- Entity mapping -->
        <mapping class="com.example.hibernate.Student"/>
    </session-factory>
</hibernate-configuration>

// pom.xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>hibernate-crud</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <hibernate.version>5.6.10.Final</hibernate.version>
        <mysql.version>8.0.30</mysql.version>
    </properties>

    <dependencies>
        <!-- Hibernate Core -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-core</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- Hibernate C3P0 Connection Pool -->
        <dependency>
            <groupId>org.hibernate</groupId>
            <artifactId>hibernate-c3p0</artifactId>
            <version>${hibernate.version}</version>
        </dependency>
        
        <!-- MySQL Connector -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>
    </dependencies>
</project>
