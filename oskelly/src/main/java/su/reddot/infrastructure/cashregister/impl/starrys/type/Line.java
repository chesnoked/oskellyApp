package su.reddot.infrastructure.cashregister.impl.starrys.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public class Line {

    private final long qty;

    private final long price;

    private final short payAttribute;

    private final short taxId;

    private final String description;
}
