package com.anisala.chat.server.repository.impl;

import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


import com.anisala.chat.server.model.User;
import com.anisala.chat.server.repository.UserDao;

public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public void save(User user) {
        Transaction transaction = null;
        try{
            Session session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) 
                transaction.rollback(); // prevents nullpointer exception in case the try block fails before transaction is initialised
            e.printStackTrace();
        }
    }

    @Override
    public User findByUserName(String userName) {
        User user = null;
        Transaction transaction = null;
        try{
            Session session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            // Query<User> query = session.createQuery("FROM User WHERE userName = :userName", User.class);
            // query.setParameter("userName", userName);
            // user = query.uniqueResult();
            user = session.byNaturalId(User.class).using("userName",userName).load();
            transaction.commit();

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User findByUserID(String userId) {
        User user = null;
        Transaction transaction = null;
        try{
            Session session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            // Query<User> query = session.createQuery("FROM User WHERE userId = :userId", User.class);
            // query.setParameter("userId", userId);
            // user = query.uniqueResult();
            user = session.get(User.class, userId);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User update(User user) {
        Transaction transaction = null;
        try{
            Session session = sessionFactory.getCurrentSession();
            transaction = session.beginTransaction();
            user = session.merge(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) 
                transaction.rollback();
            e.printStackTrace();
        }
        return user;
    }

    // @Override
    // public void setOnline(String userId, boolean isOnline) {
    //     Transaction transaction = null;
    //     try{
    //         Session session = sessionFactory.getCurrentSession();
    //         transaction = session.beginTransaction();
    //         // Query<User> query = session.createQuery("FROM User WHERE userId = :userId", User.class);
    //         // query.setParameter("userId", userId);
    //         // User user = query.uniqueResult();
    //         Us
    //         if (user != null) {
    //             user.setIsOnline(isOnline);
    //             session.update(user);
    //         }
    //         transaction.commit();
    //     } catch (Exception e) {
    //         if (transaction != null) 
    //             transaction.rollback();
    //         e.printStackTrace();
    //     }
    // }

    // @Override
    // public boolean isOnline(String userName) {
    //     Transaction transaction = null;
    //     try{
    //         Session session = sessionFactory.getCurrentSession();
    //         transaction = session.beginTransaction();
    //         Query<User> query = session.createQuery("FROM User WHERE userId = :userId", User.class);
    //         query.setParameter("userId", userId);
    //         User user = query.uniqueResult();
    //         if (user != null) {
    //             user.setIsOnline(true);
    //             session.update(user);
    //         }
    //         transaction.commit();
    //     } catch (Exception e) {
    //         if (transaction != null) 
    //             transaction.rollback();
    //         e.printStackTrace();
    //         return false;
    //     }
    // }
}