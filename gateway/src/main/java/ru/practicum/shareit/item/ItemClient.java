package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.exception.InvalidCommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.validation.ItemValidation;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> saveItem(Item item, long ownerId) {
        ItemValidation.isItemDtoValid(item);
        return post("", ownerId, item);
    }

    public ResponseEntity<Object> getItemById(long itemId, long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> updateItem(Item item, long itemId, long ownerId) {
        return patch("/" + itemId, ownerId, item);
    }

    public ResponseEntity<Object> getAllItems(long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> searchItem(String text, long userId) {
        if (text.isEmpty()) {
            return ResponseEntity.of(Optional.of(new ArrayList<>()));
        }
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search?text={text}", userId, parameters);
    }

    public ResponseEntity<Object> saveComment(Comment comment, long itemId, long authorId) {
        if (comment.getText().isEmpty()) {
            throw new InvalidCommentException("Can't create a comment without text.");
        }
        return post("/" + itemId + "/comment", authorId, comment);
    }

}
