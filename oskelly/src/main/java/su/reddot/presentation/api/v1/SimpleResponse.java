package su.reddot.presentation.api.v1;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
class SimpleResponse {
    private final String message;
}

