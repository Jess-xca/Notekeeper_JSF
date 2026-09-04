package com.notekeeper.jsf.dao;

import com.notekeeper.jsf.model.Tag;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class TagDAO {

    public List<Tag> findAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Tag order by name", Tag.class).list();
        }
    }

    public Tag findById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Tag.class, id);
        }
    }

    public Tag findByName(String name) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Tag t where lower(t.name) = :name", Tag.class)
                    .setParameter("name", name == null ? "" : name.trim().toLowerCase())
                    .uniqueResult();
        }
    }

    public void save(Tag tag) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.persist(tag);
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public void update(Tag tag) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(tag);
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }

    public void delete(String id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Tag tag = session.get(Tag.class, id);
            if (tag != null) {
                session.remove(tag);
            }
            transaction.commit();
        } catch (RuntimeException ex) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw ex;
        }
    }
}
