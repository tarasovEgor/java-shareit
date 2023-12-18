package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment, Item item, String authorName) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                item,
                authorName
        );
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(comment -> new CommentDto(
                        comment.getId(),
                        comment.getText(),
                        comment.getItem(),
                        comment.getAuthor().getName()
                ))
                .collect(Collectors.toList());
    }
}
