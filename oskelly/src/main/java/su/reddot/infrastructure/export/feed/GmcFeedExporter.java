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
public class GmcFeedExporter implements FeedExporter {
    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.gmc.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.gmc.filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualFullExportInGMC();
    }

    @Override
    public void defaultDelivery() throws IOException {

        log.debug("Генерация выгрузки в формате Google Merchant Center");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Выгрузка в формате Google Merchant Center сохранена");

    }

    private String getActualFullExportInGMC(){
        String result = jdbcTemplate.queryForObject("SELECT xmlagg(xmlelement(NAME \"rss\", XMLATTRIBUTES ('2.0' AS \"version\", 'http://base.google.com/ns/1.0' AS \"xmlns:g\"),\n" +
                "                         (SELECT xmlagg(xmlelement(NAME channel,\n" +
                "                                                   xmlelement(NAME \"title\", 'OSKELLY.RU LUXURY RESALE STORE'),\n" +
                "                                                   xmlelement(NAME link, 'https://oskelly.ru/'),\n" +
                "                                                   xmlelement(NAME \"description\", 'OSKELLY.RU LUXURY RESALE STORE'),\n" +
                "                                                   (SELECT xmlagg(xmlelement(NAME item,\n" +
                "                                                                             xmlelement(NAME link,\n" +
                "                                                                                        concat(\n" +
                "                                                                                            'https://oskelly.ru/products/',\n" +
                "                                                                                            p.id)),\n" +
                "                                                                             xmlelement(NAME \"g:id\",\n" +
                "                                                                                        concat(p.id, '-', pi.id)),\n" +
                "                                                                             xmlelement(NAME \"g:price\", concat(\n" +
                "                                                                                 cast(pi.current_price AS INTEGER), ' ',\n" +
                "                                                                                 'RUB')),\n" +
                "                                                                             xmlelement(NAME \"g:condition\", 'used'),\n" +
                "                                                                             xmlelement(NAME \"g:product_type\",\n" +
                "                                                                                        c.display_name),\n" +
                "                                                                             xmlelement(NAME \"g:availability\",\n" +
                "                                                                                        'in stock'),\n" +
                "                                                                             xmlelement(NAME \"g:image_link\",\n" +
                "                                                                                        (SELECT concat(\n" +
                "                                                                                            'https://oskelly.ru/img/',\n" +
                "                                                                                            'product/', p.id, '/item-',\n" +
                "                                                                                            right(img.image_path, 36))\n" +
                "                                                                                         FROM image img\n" +
                "                                                                                         WHERE img.product_id = p.id AND\n" +
                "                                                                                               is_main = TRUE)),\n" +
                "                                                                             xmlelement(NAME \"title\",\n" +
                "                                                                                        concat(c.singular_name, ' ',\n" +
                "                                                                                               b.name)),\n" +
                "                                                                             (SELECT xmlagg(\n" +
                "                                                                                 xmlelement(NAME \"g:material\",\n" +
                "                                                                                            attrv.value))\n" +
                "                                                                              FROM\n" +
                "                                                                                product_attribute_value_binding attbind\n" +
                "                                                                                JOIN attribute_value attrv\n" +
                "                                                                                  ON attbind.attribute_value_id =\n" +
                "                                                                                     attrv.id\n" +
                "                                                                                JOIN attribute att\n" +
                "                                                                                  ON attrv.attribute_id = att.id\n" +
                "                                                                              WHERE attbind.product_id = p.id AND\n" +
                "                                                                                    att.id IN\n" +
                "                                                                                    (1, 2, 3, 4, 5, 6, 7, 8, 9, 14, 15)\n" +
                "                                                                             ),\n" +
                "                                                                             (SELECT xmlagg(xmlelement(NAME \"g:color\",\n" +
                "                                                                                                       attrv.value))\n" +
                "                                                                              FROM\n" +
                "                                                                                product_attribute_value_binding attbind\n" +
                "                                                                                JOIN attribute_value attrv\n" +
                "                                                                                  ON attbind.attribute_value_id =\n" +
                "                                                                                     attrv.id\n" +
                "                                                                                JOIN attribute att\n" +
                "                                                                                  ON attrv.attribute_id = att.id\n" +
                "                                                                              WHERE attbind.product_id = p.id AND\n" +
                "                                                                                    att.id = 10\n" +
                "                                                                             ),\n" +
                "                                                                             xmlelement(NAME \"description\",\n" +
                "                                                                                        concat(c.singular_name, ' ',\n" +
                "                                                                                               b.name))\n" +
                "                                                                  ))\n" +
                "                                                    FROM product p\n" +
                "                                                      JOIN product_item pi ON pi.product_id = p.id\n" +
                "                                                      JOIN category c ON p.category_id = c.id\n" +
                "                                                      JOIN brand b ON p.brand_id = b.id\n" +
                "                                                      JOIN product_condition pc ON p.product_condition_id = pc.id\n" +
                "                                                      JOIN size s ON pi.size_id = s.id\n" +
                "                                                    WHERE p.product_state = 'PUBLISHED'\n" +
                "                                                          AND pi.state = 'INITIAL' AND pi.delete_time IS NULL\n" +
                "                                                   )\n" +
                "\n" +
                "                                        )))))", String.class);
        return result;
    }
}
