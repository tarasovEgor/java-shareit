package ru.practicum.shareit.json;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.Assertions.assertThat;

@Disabled
@JsonTest
public class CommentDtoJsonTest {

    @Autowired
    private JacksonTester<CommentDto> json;

    private User itemOwner;
    private Item item;

    @BeforeEach
    void setUp() {

        itemOwner = new User(
                "user",
                "user@mail.com"
        );

        item = new Item(
                "item",
                "desc",
                false,
                itemOwner,
                2L
        );

        itemOwner.setId(1L);
        item.setId(1L);

    }

    @Test
    void testCommentDto() throws Exception {
        // Given
        CommentDto commentDto = new CommentDto(
                1L,
                "text",
                item,
                "author"
        );

        // When
        JsonContent<CommentDto> result = json.write(commentDto);

        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("text");

        assertThat(result).extractingJsonPathValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathValue("$.item.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathValue("$.item.available").isEqualTo(false);
        assertThat(result).extractingJsonPathValue("$.item.owner.id").isEqualTo(1);
        assertThat(result).extractingJsonPathValue("$.item.owner.name").isEqualTo("user");
        assertThat(result).extractingJsonPathValue("$.item.owner.email").isEqualTo("user@mail.com");
        assertThat(result).extractingJsonPathValue("$.item.requestId").isEqualTo(2);

        assertThat(result).extractingJsonPathStringValue("$.authorName").isEqualTo("author");

    }
}
