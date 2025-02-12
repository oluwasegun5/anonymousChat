package com.localhost.anonymouschat.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGroupRequest {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    @NotNull
    @Size(min = 1, max = 100)
    private Set<@NotBlank String> participantIds;
}