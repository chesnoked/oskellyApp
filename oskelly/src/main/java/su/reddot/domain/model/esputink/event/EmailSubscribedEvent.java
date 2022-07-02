package su.reddot.domain.model.esputink.event;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailSubscribedEvent {
    private final String email;
    private final String name;
}
