package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.dtos.CommentDto;
import ru.otus.hw.exceptions.NotFoundException;
import ru.otus.hw.mappers.MapperToDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public CommentDto findById(Long id) {
        return commentRepository.findById(id).map(MapperToDto.MAP_TO_COMMENT_DTO_FUNCTION)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> findAllByBookId(Long bookId) {
        return commentRepository.findAllByBookId(bookId).stream()
                .map(MapperToDto.MAP_TO_COMMENT_DTO_FUNCTION).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto insert(String text, Long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = new Comment(0L, text, book);
        return MapperToDto.MAP_TO_COMMENT_DTO_FUNCTION.apply(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public CommentDto update(Long id, String text, Long bookId) {
        var book = bookRepository.findById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id %d not found".formatted(bookId)));
        var comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Comment with id %d not found".formatted(id)));
        comment.setText(text);
        return MapperToDto.MAP_TO_COMMENT_DTO_FUNCTION.apply(commentRepository.save(comment));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }
}
