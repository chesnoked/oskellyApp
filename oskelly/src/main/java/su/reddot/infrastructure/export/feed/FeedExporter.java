package su.reddot.infrastructure.export.feed;

import java.io.IOException;

public interface FeedExporter {
    /**
     * Получаем стандартным образом массив данных для экспорта
     * @return
     */
    public String extractData();

    /**
     * Стандартный метод доставки данных
     */
    public void defaultDelivery() throws IOException;
}

