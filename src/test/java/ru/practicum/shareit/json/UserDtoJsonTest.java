package ru.practicum.shareit.json;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoJsonTest {

    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {
        // Given
        UserDto userDto = new UserDto(
                1L,
                "user",
                "user@mail.com"
        );

        // When
        JsonContent<UserDto> result = json.write(userDto);

        // Then
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@mail.com");

    }

}
