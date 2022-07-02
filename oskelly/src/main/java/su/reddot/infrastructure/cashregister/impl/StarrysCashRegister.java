package su.reddot.infrastructure.cashregister.impl;

import lombok.extern.slf4j.Slf4j;
import net.glxn.qrgen.javase.QRCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.client.RestTemplate;
import su.reddot.infrastructure.cashregister.CashRegister;
import su.reddot.infrastructure.cashregister.Checkable;
import su.reddot.infrastructure.cashregister.impl.starrys.StarrysConfiguration;
import su.reddot.infrastructure.cashregister.impl.starrys.type.BuyerCheckRequest;
import su.reddot.infrastructure.cashregister.impl.starrys.type.Line;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class StarrysCashRegister implements CashRegister {

    private final StarrysConfiguration conf;
    private final RestTemplate restTemplate;

    @Value("${resources.images.urlPrefix}")
    private String urlPrefix;
    @Value("${resources.images.pathToDir}")
    private String pathToDir;

    @Autowired
    public StarrysCashRegister(StarrysConfiguration conf,
                               @Qualifier("starrys-rest-template") RestTemplate restTemplate) {
        this.conf = conf;
        this.restTemplate = restTemplate;
    }

    @Override
    public String requestCheck(Checkable c) {

        List<Line> cookedLines = c.getLines().stream().map(this::cookLine).collect(Collectors.toList());
        List<Long> nonCashAmount = Arrays.asList(getPriceInKopecks(c.getNonCashAmount()), 0L, 0L);

        BuyerCheckRequest buyerCheckRequest = new BuyerCheckRequest(c.getRequestId(),
                conf.getPassword(), conf.getClientId(),
                cookedLines, nonCashAmount);

        buyerCheckRequest.setPlace(conf.getPlace())
                .setFullResponse(true);

        ResponseEntity<String> response = restTemplate.postForEntity(
                conf.getEndpoint(), buyerCheckRequest, String.class);

        return response.getBody();
    }

    @Override
    public String getQrUrl(String s) {

        String qrUrl = null;

        try {
            String imageFilename = hash(s);

            /* Если изображение с qr кодом уже есть в ФС, отдать ссылку на него,
            иначе сгенерировать изображение и затем отдать ссылку. */
            Path pathToGeneratedImage = Paths.get(pathToDir, "qr", imageFilename);
            if (!Files.exists(pathToGeneratedImage)) {
                /* в html шаблоне используется изображение именного такого размера. */
                ByteArrayOutputStream stream = QRCode.from(s).withSize(150, 150).stream();
                try (OutputStream os = new FileOutputStream(pathToGeneratedImage.toString())) {
                    stream.writeTo(os);
                }
            }

            qrUrl =  urlPrefix + "qr/" + hash(s);

        } catch (Exception e) {
            log.error("Ошибка при получении qr изображения для строки: {}, строка: {}", e.getMessage(), s, e);
            throw new RuntimeException();
        }

        return qrUrl;
    }

    private Line cookLine(Checkable.Line rawLine) {

        long quantity = (long) rawLine.getQuantity() * 1000;
        String description = new String(rawLine.getDescription().getBytes(), Charset.forName("cp866"));

        return new Line(quantity, getPriceInKopecks(rawLine.getPrice()),
                conf.getPayAttribute(),
                conf.getTaxId(),
                description);
    }

    private long getPriceInKopecks(BigDecimal price) {
        return price.movePointRight(2).longValue();
    }

    private String hash(String s) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest digest = MessageDigest.getInstance("MD5");

        byte[] hash   = digest.digest(s.getBytes("UTF-8"));
        String encoded = Base64Utils.encodeToUrlSafeString(hash);

        return String.valueOf(encoded);
    }
}
