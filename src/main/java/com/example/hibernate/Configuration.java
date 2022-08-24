package com.example.hibernate;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.persistence.*;
import javax.sql.DataSource;
import java.util.Properties;

public class Configuration {
    private DataSource getDataSource() {
        final BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUsername("root");
        dataSource.setPassword("ChunkyRoot15241?");
        dataSource.setUrl("jdbc:mysql://localhost:3306/mydb");
        return dataSource;
    }

    private Properties getProperties() {
        final Properties properties = new Properties();
        properties.put( "hibernate.dialect", "org.hibernate.dialect.MySQLDialect" );
        properties.put( "hibernate.connection.driver_class", "org.MySQL.Driver" );
        properties.put("hibernate.show_sql", "true");
        return properties;
    }

    private EntityManagerFactory entityManagerFactory(DataSource dataSource, Properties hibernateProperties ){
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com/example/hibernate");
        em.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        em.setJpaProperties( hibernateProperties );
        em.setPersistenceUnitName( "demo-unit" );
        em.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        em.afterPropertiesSet();
        return em.getObject();
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        DataSource dataSource = configuration.getDataSource();
        Properties properties = configuration.getProperties();
        EntityManagerFactory entityManagerFactory = configuration.entityManagerFactory(dataSource, properties);
        EntityManager em = entityManagerFactory.createEntityManager();
        PersistenceUnitUtil unitUtil = entityManagerFactory.getPersistenceUnitUtil();
        getStudentById(em);
    }

    private static void insertBook(EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Book b = new Book();
        b.setName("Lord of the Rings");
        b.setId(1);
        em.merge(b);
        tx.commit();
    }
    private static void getStudentById(EntityManager em) {
        Query query = em.createQuery("select b from book b where b.book_id = 1");
        Book b = (Book)query.getSingleResult();
        System.out.println(b);
    }
    static void addBook_Author(EntityManager em, Book book, Author author) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (book == null || author == null) {
            tx.rollback();
            return;
        }
        Author_Book authorbook = new Author_Book();
        authorbook.setAuthor(author);
        authorbook.setBook(book);
        em.merge(authorbook);
        tx.commit();
    }
    static void removeAuthor(EntityManager em, int id) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        em.createQuery("DELETE FROM author a WHERE a.id="+id).executeUpdate();
        em.createQuery("DELETE FROM authorbook a WHERE a.authorid="+id).executeUpdate();

        tx.commit();
    }

}
