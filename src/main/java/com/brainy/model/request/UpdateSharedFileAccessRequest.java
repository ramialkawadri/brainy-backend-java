package com.brainy.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public record UpdateSharedFileAccessRequest(
		@JsonProperty("can-edit") @NotNull(message = "missing") Boolean canEdit) {
}
