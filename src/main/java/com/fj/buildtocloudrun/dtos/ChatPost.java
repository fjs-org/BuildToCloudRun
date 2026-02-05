package com.fj.buildtocloudrun.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

public record ChatPost(
        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        UUID id,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String from,

        @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        LocalDateTime timestamp,

        @Schema(
                description = "The content of the chat message",
                example = "Hello, how can I help you today?",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String message
) {}