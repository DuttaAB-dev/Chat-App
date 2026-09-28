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
            Query<User> query = session.createQuery("FROM User WHERE userName = :userName", User.class);
            query.setParameter("userName", userName);
            user = query.uniqueResult();
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
            Query<User> query = session.createQuery("FROM User WHERE userId = :userId", User.class);
            query.setParameter("userId", userId);
            user = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }
}