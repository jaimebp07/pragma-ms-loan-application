package co.com.crediya.consumer.DTO;

import java.util.Set;
import java.util.UUID;

public record GetListUsersRqDTO(
    Set<UUID> listUserIds
) {}
