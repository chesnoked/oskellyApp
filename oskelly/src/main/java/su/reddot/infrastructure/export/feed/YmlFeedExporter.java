package su.reddot.infrastructure.export.feed;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class YmlFeedExporter implements FeedExporter {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.yml.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.yml.filename}")
    private String exportFileName;


    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualFullExportInYML();
    }


    @Override
    public void defaultDelivery() throws IOException {

        log.debug("Генерация выгрузки в формате YML");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Выгрузка в формате YML сохранена");

    }

    private String getActualFullExportInYML() {
        String result = jdbcTemplate.queryForObject(
                "SELECT xmlagg(xmlelement(NAME yml_atalog," +
                        "                         xmlelement(NAME SHOP, " +
                        "                                    xmlelement(NAME \"name\", 'OSKELLY.RU LUXURY RESALE STORE'), " +
                        "                                    xmlelement(NAME \"url\", 'https://oskelly.ru/'), " +
                        "                                    xmlelement(NAME \"categories\", " +
                        "                                               (SELECT xmlagg( " +
                        "                                                   xmlelement(NAME \"category\", XMLATTRIBUTES (categ.id AS \"id\"), " +
                        "                                                              categ.display_name)) " +
                        "                                                FROM category categ) " +
                        "                                    ), " +
                        "                                    xmlelement(NAME \"offers\", " +
                        "                                      (SELECT xmlagg(xmlelement(NAME \"offer\", " +
                        "                                                                         XMLATTRIBUTES (p.id AS \"id\", 'true' AS " +
                        "                                                                         \"available\"), " +
                        "                                                                         xmlelement(NAME \"url\", concat( " +
                        "                                                                             'https://oskelly.ru/products/', p.id)), " +
                        "                                                                         xmlelement(NAME \"price\", " +
                        "                                                                                    (SELECT MAX(pi2.current_price) " +
                        "                                                                                     FROM product_item pi2 " +
                        "                                                                                     WHERE pi2.product_id = p.id AND " +
                        "                                                                                           pi2.delete_time IS NULL)), " +
                        "                                                                         xmlelement(NAME \"currencyId\", 'RUB'), " +
                        "                                                                         xmlelement(NAME \"delivery\", 'true'), " +
                        "                                                                         xmlelement(NAME \"categoryId\", p.category_id), " +
                        "                                                                         xmlelement(NAME \"vendor\", b.name), " +
                        "                                                                         xmlelement(NAME \"name\", " +
                        "                                                                                    concat(c.singular_name, ' ', b.name)), " +
                        "                                                                         xmlelement(NAME \"picture\", " +
                        "                                                                                    (SELECT " +
                        "                                                                                       concat('https://oskelly.ru/img/', " +
                        "                                                                                              'product/', p.id, " +
                        "                                                                                              '/item-', " +
                        "                                                                                              right(img.image_path, 36)) " +
                        "                                                                                     FROM image img " +
                        "                                                                                     WHERE img.product_id = p.id AND " +
                        "                                                                                           is_main = TRUE)) " +
                        "                                                              )) " +
                        "                                                FROM product p " +
                        "                                                  JOIN category c ON p.category_id = c.id " +
                        "                                                  JOIN product_item pi ON p.id = pi.product_id " +
                        "                                                  JOIN brand b ON p.brand_id = b.id " +
                        "                                                  JOIN product_condition pc ON p.product_condition_id = pc.id " +
                        "                                                  JOIN size s ON pi.size_id = s.id " +
                        "                                                WHERE p.product_state = 'PUBLISHED' " +
                        "                                                      AND pi.state = 'INITIAL' AND pi.delete_time IS NULL " +
                        "                                               )" +
                        "                                    )" +
                        "                         )" +
                        "              ))"
                , String.class);

        return result;
    }
}
