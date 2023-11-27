package com.brainy.model.request;

import jakarta.validation.constraints.NotNull;

public record UpdateSharedFileAccessRequest(@NotNull(message = "missing") Boolean canEdit) {
}
