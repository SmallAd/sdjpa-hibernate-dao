package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

@Component
public class BookDaoImpl implements BookDao {

    private final EntityManagerFactory emf;

    public BookDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Book findByISBN(String isbn) {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Book> query = em.createQuery("SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                    .setParameter("isbn", isbn);

            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book getById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Book.class, id);
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitle(String title) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Book> query = em.createNamedQuery("find_by_title", Book.class)
                    .setParameter("title", title);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleNative(String title) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery("SELECT * FROM book b WHERE b.title = :title", Book.class)
                    .setParameter("title", title);
            return (Book) query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book findBookByTitleCriteria(String title) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Book> criteriaQuery = cb.createQuery(Book.class);

            Root<Book> root = criteriaQuery.from(Book.class);

            ParameterExpression<String> titleParameter = cb.parameter(String.class, "title");

            Predicate titlePredicate = cb.equal(root.get("title"), titleParameter);

            criteriaQuery.select(root).where(titlePredicate);

            TypedQuery<Book> typedQuery = em.createQuery(criteriaQuery)
                    .setParameter(titleParameter, title);
            return typedQuery.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public Book saveNewBook(Book book) {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        em.persist(book);
        em.flush();
        em.getTransaction().commit();
        em.close();
        return book;
    }

    @Override
    public Book updateBook(Book book) {
        EntityManager em = getEntityManager();

        em.joinTransaction();
        em.merge(book);
        em.flush();
        em.clear();
        Book updated = em.find(Book.class, book.getId());
        em.getTransaction().commit();
        em.close();

        return updated;
    }

    @Override
    public void deleteBookById(Long id) {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        Book book = em.find(Book.class, id);
        em.remove(book);
        em.flush();
        em.getTransaction().commit();
        em.close();

    }

    @Override
    public List<Book> findAll() {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Book> query = em.createNamedQuery("book_find_all", Book.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
