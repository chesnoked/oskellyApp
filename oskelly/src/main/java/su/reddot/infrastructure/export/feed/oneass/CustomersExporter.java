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
public class CustomersExporter implements FeedExporter {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.feedExport.oneass.defaultPathToExportFile}")
    private String defaultExportPath;

    @Value("${app.feedExport.oneass.customers-filename}")
    private String exportFileName;

    @Override
    public String extractData() {
        String formatString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        return formatString + this.getActualCustomersFor1S();
    }

    @Override
    public void defaultDelivery() throws IOException{
        log.debug("Генерация выгрузки Customers.XML для 1с");
        String exportData = this.extractData();

        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(
                        new FileOutputStream(defaultExportPath + exportFileName),
                        StandardCharsets.UTF_8));
        writer.write(exportData);
        writer.close();
        log.debug("Актуальная выгрузка Customers.XML для 1с сохранена");

    }

    private String getActualCustomersFor1S(){
        return jdbcTemplate.queryForObject("SELECT xmlelement(NAME \"one-s-export\",\n" +
                "                  xmlelement(NAME \"meta-description\", 'Customers'),\n" +
                "                  xmlelement(NAME \"meta-publistimestamp\", to_char(current_timestamp, 'DD.MM.YYYY  HH24:MI:SS')),\n" +
                "                  xmlelement(NAME Customers,\n" +
                "                             (SELECT xmlagg(xmlelement(NAME Customer,\n" +
                "                                                       xmlelement(NAME Description,\n" +
                "                                                                  CONCAT(\"user\".company_name, \"user\".last_name, ' ',\n" +
                "                                                                         \"user\".first_name)),\n" +
                "                                                       -- Правильно называть это ID, т.к. идентификатор не глобальный\n" +
                "                                                       xmlelement(NAME ID, \"user\".id),\n" +
                "                                                       xmlelement(NAME RegTime, \"user\".registration_time),\n" +
                "                                                       xmlelement(NAME Nick, \"user\".nickname),\n" +
                "                                                       xmlelement(NAME Phone, \"user\".delivery_phone),\n" +
                "                                                       xmlelement(NAME DeliveryAddress,\n" +
                "                                                                  xmlelement(NAME PostCode, \"user\".delivery_zip_code),\n" +
                "                                                                  xmlelement(NAME City, \"user\".delivery_city),\n" +
                "                                                                  xmlelement(NAME Address, \"user\".delivery_address)),\n" +
                "                                                       xmlelement(NAME INN, \"user\".inn),\n" +
                "                                                       xmlelement(NAME KPP, \"user\".kpp),\n" +
                "                                                       xmlelement(NAME OGRN, \"user\".ogrn),\n" +
                "                                                       xmlelement(NAME OGRNIP, \"user\".ogrnip),\n" +
                "                                                       xmlelement(NAME PRO, \"user\".pro_status_time),\n" +
                "                                                       xmlelement(NAME VIP, \"user\".vip_status_time)\n" +
                "                                            ))\n" +
                "                              FROM \"user\"\n" +
                "                             )\n" +
                "                  ))", String.class);

    }
}
