package su.reddot.domain.service.catalog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter @Setter
public class AvailableFilters {
    private List<Long> filter = new ArrayList<Long>();

    private List<Long> size = new ArrayList<Long>();

    private List<Long> brand = new ArrayList<Long>();

    private List<Long> productCondition = new ArrayList<Long>();

    /** Только винтажные товары */
    private boolean isVintage = false;

    /** Только товары, которые продаются по сниженной цене */
    private boolean isOnSale = false;

    /** Только товары с отметкой "Наш выбор" */
    private boolean hasOurChoice = false;

    /** Только товары с отметкой "Новая коллекция" */
    private boolean isNewCollection = false;
}
