package su.reddot.infrastructure.configuration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import su.reddot.infrastructure.cashregister.impl.starrys.StarrysConfiguration;

import javax.net.ssl.SSLContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration {

    private final StarrysConfiguration          starrysConfiguration;
    private final ResourceLoader                resourceLoader;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MessageDigestPasswordEncoder apiPasswordEncoder(){
        return new ShaPasswordEncoder(512);
    }

    @Bean @Qualifier("starrys-rest-template")
    public RestTemplate starrysRestTemplate(RestTemplateBuilder restTemplateBuilder,
                                            StarrysConfiguration conf)
            throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {

        Resource clientCertificate = resourceLoader.getResource(starrysConfiguration.getClientCertificatePath());

        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(clientCertificate.getInputStream(), conf.getClientCertificatePassword().toCharArray());

        SSLContext sslContext = new SSLContextBuilder()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .loadKeyMaterial(keyStore, conf.getClientCertificatePassword().toCharArray()).build();

        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
                sslContext);

        HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory( httpClient);

        return restTemplateBuilder.requestFactory(requestFactory).build();
    }

    @Bean
    public RestTemplate vkRestTemplate(RestTemplateBuilder restTemplateBuilder) {

        /*
         * Дефолтный клиент не понимает атрибут expires полученной от ВК сервера куки,
         * когда время в этом атрибуте указано в формате RFC 1123:
         * Jan, 01 Jan 2000 00:00:00 GMT
         *
         * и добавляет в лог системы запись вида:
         *
         * o.a.h.c.protocol.ResponseProcessCookies:
         * Invalid cookie header: "Set-Cookie: remixlang=0; expires=Thu, 20 Dec 2018 13:04:38 GMT; path=/; domain=.vk.com".
         * Invalid 'expires' attribute: Thu, 20 Dec 2018 13:04:38 GMT
         */
        CloseableHttpClient ignoringCookieErrorsClient = HttpClients.custom()
                .setDefaultRequestConfig(RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build())
                .build();

        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(ignoringCookieErrorsClient);

        return restTemplateBuilder.requestFactory(requestFactory).build();
    }

    @Bean @Qualifier("tilda-rest-template")
    public RestTemplate tildaRestTemplate(RestTemplateBuilder restTemplateBuilder){
        CloseableHttpClient httpClient = HttpClients.custom().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        return restTemplateBuilder.requestFactory(requestFactory).build();
    }

    @Bean
    public Map<String, Map<String,String>> staticFilesConfiguration(ObjectMapper mapper) throws IOException {

        Resource jsonFile =  resourceLoader.getResource("classpath:/static/webpack_assets.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(jsonFile.getInputStream()));

        String jsonConfig = "";
        for(String line;(line = br.readLine())!=null; jsonConfig += line);

        TypeReference<HashMap<String, HashMap<String, String>>> typeRef =
                new TypeReference<HashMap<String, HashMap<String, String>>>() {};


        return mapper.readValue(jsonConfig, typeRef);
    }

}
