package su.reddot.infrastructure.export.vk;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class Client {

    private final RestTemplate    restTemplate;
    private final ObjectMapper    mapper;

    private final VkConfiguration conf;

    Client(@Qualifier("vkRestTemplate") RestTemplate restTemplate, ObjectMapper mapper,
            VkConfiguration conf) {

        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.conf = conf;
    }

    /** Загрузить изображение в ВК маркет.
     * @return идентификатор изображения в маркете.
     * @throws Exception если загрузить изображение не получилось. */
    SaveResult uploadImage(File image, boolean imageIsMain) throws Exception {

        String uploadUrl = getUploadUrl(imageIsMain);
        TimeUnit.MILLISECONDS.sleep(200);

        UploadResultResponse uploadResult = uploadImageTo(uploadUrl, image);
        TimeUnit.MILLISECONDS.sleep(200);

        return saveImage(uploadResult);
    }

    /** Обновить сведения о товаре, который уже находится в маркете. */
    void edit(Product p) throws Exception {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                "https://api.vk.com/method/market.edit")

                .queryParam("owner_id", "-" + conf.getGroupId())
                .queryParam("item_id", p.id)
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("name", p.name);
        body.add("description", p.description);
        body.add("category_id", String.valueOf(p.category.id));
        body.add("price", p.price);
        body.add("deleted", p.deleted? "1" : "0");
        body.add("main_photo_id", p.mainPhotoId);

        if (p.photoIds != null && !p.photoIds.isEmpty()) {
            body.add("photo_ids", String.join(",", p.photoIds));
        }

        String url = builder.build().toUriString();

        log.debug("Запрос на обновление товара: {}, параметры: {}", url, body);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        String productEditResult = restTemplate.postForObject(url, request, String.class);
        log.debug("Ответ получен: {}", productEditResult);

        ProductEditResult result = mapper.readValue(productEditResult, ProductEditResult.class);
        if (!result.isResponse()) {
            throw new Exception("Неожиданный ответ сервиса: " + productEditResult);
        }
    }

    /** Добавить товар в маркет.
     * @param p данные о добавляемом товаре
     * @throws Exception если товар добавить в маркет не получилось. */
    ProductAddResult add(Product p) throws Exception {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                "https://api.vk.com/method/market.add")

                /* Требование апи ВК:
                * Идентификатор сообщества в параметре owner_id необходимо указывать со знаком "-"
                * Например, owner_id=-1 соответствует идентификатору сообщества ВКонтакте API (club1)
                * */
                .queryParam("owner_id", "-" + conf.getGroupId())
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69");

        String url = builder.build().toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        LinkedMultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("name", p.name);
        body.add("description", p.description);
        body.add("category_id", String.valueOf(p.category.id));
        body.add("price", p.price);
        body.add("deleted", "0");
        body.add("main_photo_id", p.mainPhotoId);

        if (p.photoIds != null && !p.photoIds.isEmpty()) {
            body.add("photo_ids", String.join(",", p.photoIds));
        }

        HttpEntity<LinkedMultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        log.debug("Запрос на добавление товара: {}, параметры: {}", url, body);
        String productAddResult = restTemplate.postForObject(url, requestEntity, String.class);
        log.debug("Ответ получен: {}", productAddResult);

        ProductAddResult result = mapper.readValue(productAddResult, ProductAddResult.class);
        if (result.getResponse() == null) {
            throw new Exception("Неожиданный ответ сервиса: " + productAddResult);
        }

        return result;
    }

    /** Удалить товар из маркета.
     * @param productId идентификатор товара в маркете.
     * @throws Exception если товар из маркета удалить не получилось. */
    void delete(String productId) throws Exception {

        String deleteEndpoint = UriComponentsBuilder.fromUriString(
                "https://api.vk.com/method/market.delete")

                .queryParam("owner_id", "-" + conf.getGroupId())
                .queryParam("item_id", productId)
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69").build().toUriString();

        log.debug("Запрос на удаление товара: {}", deleteEndpoint);
        String productDeleteResult = restTemplate.getForObject(deleteEndpoint, String.class);
        log.debug("Ответ получен: {}", productDeleteResult);

        GenericResult result = mapper.readValue(productDeleteResult, GenericResult.class);
        if (result.getResponse() != 1) {
            throw new Exception("Неожиданный ответ сервиса: " + result);
        }
    }

    /** Добавить товар в отдельный альбом.
     * @throws Exception если товар не удалось добавить в подборку (например, если тот уже в нее добавлен)
     **/
    void addToAlbum(String productId, Integer albumId) throws Exception {

        String url = UriComponentsBuilder
                .fromUriString("https://api.vk.com/method/market.addToAlbum")
                .queryParam("owner_id", "-" + conf.getGroupId())
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69")

                .queryParam("item_id", productId)
                .queryParam("album_ids", albumId)
                .build().toUriString();

        log.debug("Запрос на добавление товара в альбом: {}", url);
        String addToAlbumResponse = restTemplate.getForObject(url, String.class);
        log.debug("Ответ получен: {}", addToAlbumResponse);

        GenericResult result = mapper.readValue(addToAlbumResponse, GenericResult.class);
        if (result.getResponse() != 1) {
            throw new Exception("Неожиданный ответ сервиса: " + addToAlbumResponse);
        }
    }

    private String getUploadUrl(boolean imageIsMain) throws Exception {

        String url = UriComponentsBuilder
                .fromUriString("https://api.vk.com/method/photos.getMarketUploadServer")
                .queryParam("group_id", conf.getGroupId())
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69")
                .queryParam("main_photo", imageIsMain)
                .build().toUriString();

        log.debug("Запрос ссылки для загрузки изображений: {}", url);
        String uploadUrlResponse = restTemplate.getForObject(url, String.class);
        log.debug("Ответ получен: {}", uploadUrlResponse);

        UploadUrlResponse response = mapper.readValue(uploadUrlResponse, UploadUrlResponse.class);
        if (response.getResponse() == null) {
            throw new Exception("Неожиданный ответ сервиса: " + uploadUrlResponse);
        }

        return response.getResponse().getUploadUrl();
    }

    private UploadResultResponse uploadImageTo(String uploadUrl, File image) throws Exception {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body =  new LinkedMultiValueMap<>();

        /* без явного указания расширения файла апи ВК возвращает ошибку нулевого разрешения изображения,
        * даже если само изображение имеет ненулевое разрешение. */
        body.add("file", new FileSystemResource(image));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String uploadResultResponse = restTemplate.postForObject(
                uploadUrl, requestEntity, String.class);

        UploadResultResponse uploadResult = mapper.readValue(
                uploadResultResponse, UploadResultResponse.class);

        if (uploadResult == null) {
            throw new Exception("Неожиданный ответ сервиса: " + uploadResultResponse);
        }

        return uploadResult;
    }

    private SaveResult saveImage(UploadResultResponse uploadResult) throws Exception {

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString("https://api.vk.com/method/photos.saveMarketPhoto")

                .queryParam("group_id", conf.getGroupId())
                .queryParam("access_token", conf.getAccessToken())
                .queryParam("v", "5.69")

                .queryParam("photo", "{photo}")
                .queryParam("server", uploadResult.getServer())
                .queryParam("hash", uploadResult.getHash());

        /* заданы только для основного изображения */
        if (uploadResult.getCropData() != null) {
            builder.queryParam("crop_data", uploadResult.getCropData());
        }
        if (uploadResult.getCropHash() != null) {
            builder.queryParam("crop_hash", uploadResult.getCropHash());
        }

        String url = builder.build().toUriString();
        String photo = uploadResult.getPhoto();

        log.debug("Запрос на сохранение изображения: {}", url);
        String saveResult = restTemplate.getForObject(url, String.class, photo);
        log.debug("Ответ получен: {}", saveResult);

        SaveResult result = mapper.readValue(saveResult, SaveResult.class);
        if (result.getResponse() == null || result.getResponse().isEmpty()) {
            throw new Exception("Неожиданный ответ сервиса: " + saveResult);
        }

        return result;
    }

    /** * Данные товара в маркете ВК. */
    @Setter @Accessors(chain = true, fluent = true)
    static class Product {

        private String id;

        private String name;
        private String description;
        private Category category;
        private String price;
        private boolean deleted;

        private String mainPhotoId;

        private List<String> photoIds;

    }

    @RequiredArgsConstructor @Getter(AccessLevel.PRIVATE)
    enum Category {
        woman(1), man(2), kids(3);

        private final int id;
    }
}
