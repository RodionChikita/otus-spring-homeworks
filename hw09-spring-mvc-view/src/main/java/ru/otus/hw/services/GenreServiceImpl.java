package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dtos.GenreDto;
import ru.otus.hw.mappers.MapperToDto;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream().map(MapperToDto.MAP_TO_GENRES_DTO_FUNCTION)
                .collect(Collectors.toList());
    }
}
