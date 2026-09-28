package com.anisala.chat.server.util;

// import com.anisala.chat.server.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {
    
    private static SessionFactory sessionFactory;

    // Static block initializes the factory when the class is first loaded
    static {
        try {
            Configuration config = new Configuration();
            // You can add classes here programmatically like in the image, 
            // OR rely on the <mapping> tags in your hibernate.cfg.xml
            // config.addAnnotatedClass(User.class);
            config.configure("hibernate.cfg.xml");
            
            sessionFactory = config.buildSessionFactory();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        // Close caches and connection pools
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}