package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Genre;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class JpaGenreRepository implements GenreRepository {
    @PersistenceContext
    private final EntityManager em;

    @Override
    public List<Genre> findAll() {
        return em.createQuery("select s from OtusStudent s", Genre.class)
                .getResultList();
    }

    @Override
    public Set<Genre> findAllByIds(Set<Long> ids) {
        TypedQuery<Genre> query = em.createQuery(
                "SELECT g FROM Genre g WHERE g.id IN :ids", Genre.class);
        query.setParameter("ids", ids);
        List<Genre> genres = query.getResultList();
        Set<Genre> resultList = new HashSet<>(genres);
        return resultList;
    }
}
