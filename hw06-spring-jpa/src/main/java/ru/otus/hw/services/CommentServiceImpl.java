package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public Optional<Comment> findById(long id) {
        return commentRepository.findById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<Comment> findAllByBookId(long bookId) {
        return Set.of((Comment) commentRepository.findAllByBookId(bookId));
    }

    @Transactional
    @Override
    public Comment insert(String text, long bookId) {
        return save(0, text, bookId);
    }

    @Transactional
    @Override
    public Comment update(long id, String text, long bookId) {
        return save(id, text, bookId);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        var comment = commentRepository.findById(id);
        if (comment.isEmpty()) {
            throw new EntityNotFoundException("Comment with id %d not found".formatted(id));
        }
        commentRepository.deleteById(comment.get());
    }

    private Comment save(long id, String text, long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %d not found".formatted(bookId)));
        Comment comment;
        if (commentRepository.findById(id).isEmpty()) {
            comment = new Comment(id, text, book);
        } else {
            comment = commentRepository.findById(id).get();
            comment.setText(text);
        }
        book.getComments().add(comment);
        book.setComments(book.getComments());
        bookRepository.save(book);
        return commentRepository.save(comment);
    }
}
