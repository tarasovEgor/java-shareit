package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;

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
        List<CommentDto> dtos = new ArrayList<>();
        for (Comment c : comments) {
            dtos.add(toCommentDto(c, c.getItem(), c.getAuthor().getName()));
        }
        return dtos;

    }
}
