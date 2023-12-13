package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Item {

//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
  //  private Long id;

    @NotNull(message = "Name is mandatory")
    private String name;

    @NotNull(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Availability status is mandatory")
    private Boolean available;

//    @NotNull(message = "Owner is mandatory")
//    private User owner;

    private Long requestId;

}
