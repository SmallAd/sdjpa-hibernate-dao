package guru.springframework.jdbc.dao;

import guru.springframework.jdbc.domain.Author;
import guru.springframework.jdbc.domain.Book;
import javassist.ByteArrayClassPath;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by jt on 8/28/21.
 */
@Component
public class AuthorDaoImpl implements AuthorDao {

    private final EntityManagerFactory emf;

    public AuthorDaoImpl(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public List<Author> listAuthorByLastNameLike(String lastName) {
        EntityManager em = getEntityManager();

        try {
            Query query = em
                    .createQuery("SELECT a FROM Author a WHERE a.lastName like :last_name")
                    .setParameter("last_name", lastName + "%");

            List<Author> authors = query.getResultList();
            return authors;
        } finally {
            em.close();
        }
    }

    @Override
    public Author getById(Long id) {
        EntityManager em = getEntityManager();

        Author author = em.find(Author.class, id);
        em.close();

        return author;
    }

    @Override
    public Author findAuthorByName(String firstName, String lastName) {
        EntityManager em = getEntityManager();
        TypedQuery<Author> query = em.createNamedQuery("find_by_name", Author.class)
                .setParameter("first_name", firstName)
                .setParameter("last_name", lastName);

        Author author = query.getSingleResult();
        em.close();
        return author;
    }

    @Override
    public Author saveNewAuthor(Author author) {
        EntityManager em = getEntityManager();

        em.getTransaction().begin();
        em.persist(author);
        em.flush();
        em.getTransaction().commit();
        em.close();

        return author;
    }

    @Override
    public Author updateAuthor(Author author) {
        EntityManager em = getEntityManager();

        em.joinTransaction();
        em.merge(author);
        em.flush();
        em.clear();

        Author updated = em.find(Author.class, author.getId());
        em.close();
        return updated;
    }

    @Override
    public void deleteAuthorById(Long id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        Author author = em.find(Author.class, id);
        em.remove(author);
        em.flush();
        em.getTransaction().commit();
        em.close();
    }

    @Override
    public List<Author> findAll() {
        EntityManager em = getEntityManager();

        try {
            TypedQuery<Author> query = em.createNamedQuery("author_find_all", Author.class);

            return query.getResultList();
        } finally {
            em.close();
        }
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
}
