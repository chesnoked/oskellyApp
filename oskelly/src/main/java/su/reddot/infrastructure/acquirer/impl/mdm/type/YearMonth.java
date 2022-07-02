package su.reddot.infrastructure.acquirer.impl.mdm.type;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
class YearMonth {

    /** Месяц от 1 до 12 */
    int month;

    /** Год 4 цифры */
    int year;
}
