package su.reddot.infrastructure.export.feed.oneass;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import su.reddot.infrastructure.export.feed.FeedExporter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductsExporter implements FeedExporter{

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.oneass.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.oneass.products-filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualProductsFor1S();
    }

    @Override
    public void defaultDelivery() throws IOException{
        log.debug("Генерация выгрузки Products.XML для 1с");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Актуальная выгрузка Products.XML для 1с сохранена");

    }

    private String getActualProductsFor1S() {
        return jdbcTemplate.queryForObject("SELECT xmlelement(NAME \"one-s-export\",\n" +
                "                  xmlelement(NAME \"meta-description\", 'Products'),\n" +
                "                  xmlelement(NAME \"meta-publistimestamp\", to_char(current_timestamp, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                  xmlelement(NAME Products,\n" +
                "                             (SELECT xmlagg(\n" +
                "                                 xmlelement(NAME Product,\n" +
                "                                            --- Это не состояние вещи, это статус на сервере: Опубликован, скрыт, и т.д.\n" +
                "                                            xmlelement(NAME State, p.product_state),\n" +
                "                                            xmlelement(NAME GUID, p.id),\n" +
                "                                            xmlelement(NAME TYPE, cat.singular_name),\n" +
                "                                            xmlelement(NAME brand, brand.name),\n" +
                "                                            -- ID продавца\n" +
                "                                            xmlelement(NAME sellerID, p.seller_id),\n" +
                "                                            xmlelement(NAME productitems,\n" +
                "                                                       --- ВАЖНО: в системе идёт разбивка по каждой уникальной ВЕЩИ. Это они и есть. Они и попадают в сделки\n" +
                "                                                       (SELECT xmlagg(\n" +
                "                                                           xmlelement(NAME productItem,\n" +
                "                                                                      xmlelement(NAME itemId, pri.id),\n" +
                "                                                                      xmlelement(NAME itemState, pri.state),\n" +
                "                                                                      --- Это сосояние вещи в системе: Создана, продана, на веривикации и т.д.\n" +
                "                                                                      xmlelement(NAME price, pri.current_price),\n" +
                "                                                                      --- цена вещи на сайте\n" +
                "                                                                      xmlelement(NAME priceWithoutComission,\n" +
                "                                                                                 pri.current_price_without_commission),\n" +
                "                                                                      --- цена вещи для продавца(сумма к выводу, без учета штрафов)\n" +
                "                                                                      xmlelement(NAME sizeID, pri.size_id)\n" +
                "                                                                      --- В 1С нужно выгрузить спрвочник размеров\n" +
                "                                                           )\n" +
                "                                                       )\n" +
                "                                                        FROM product_item pri\n" +
                "                                                        WHERE pri.product_id = p.id)\n" +
                "                                            )\n" +
                "                                 )\n" +
                "                             )\n" +
                "                              FROM product p\n" +
                "                                JOIN category cat ON p.category_id = cat.id\n" +
                "                                JOIN brand ON p.brand_id = brand.id\n" +
                "                              WHERE p.product_state != 'DRAFT'\n" +
                "                             )\n" +
                "                  ))", String.class);
    }
}
