package su.reddot.infrastructure.tilda;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

@Component
@Slf4j
public class DefaultTildaImporter implements TildaImporter {

    @Qualifier("tilda-rest-template")
    private final RestTemplate restTemplate;
    private final ObjectMapper mapper;
    private final HttpHeaders headers;
    private final TildaPageRepository tildaPageRepository;

    @Value("${app.integration.tilda.publickey}")
    private String reqPublicKey;
    @Value("${app.integration.tilda.secretkey}")
    private String reqSecretKey;
    @Value("${app.integration.tilda.filepath}")
    private String pathToFile;

    public DefaultTildaImporter(@Qualifier("tilda-rest-template") RestTemplate restTemplate,
                                ObjectMapper mapper, TildaPageRepository tildaPageRepository){
        this.restTemplate = restTemplate;
        this.mapper = mapper;
        this.tildaPageRepository = tildaPageRepository;
        this.headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_OCTET_STREAM));
    }


    @Override
    public void importProjectResources(Long projectId) throws Exception {
        String importResponse = restTemplate.getForObject(buildResourcesUrl(projectId),String.class);
        ProjectExportResponse mappedResponse = mapper.readValue(importResponse, ProjectExportResponse.class);
        if(!mappedResponse.getStatus().equals("FOUND")){
            log.debug("Неожиданный ответ сервера при запросе Tilda ProjectId:{} - {}", projectId, mappedResponse.getStatus());
            return;
        }
        for(ProjectExportResponse.Result.ExportFileMap js: mappedResponse.getResult().getJs()){
            getFile(js.getFrom(), js.getTo(),pathToFile+"/js/");
        }
        for(ProjectExportResponse.Result.ExportFileMap css: mappedResponse.getResult().getCss()){
            getFile(css.getFrom(), css.getTo(),pathToFile+"/css/");
        }
        for(ProjectExportResponse.Result.ExportFileMap image: mappedResponse.getResult().getImages()){
            getFile(image.getFrom(), image.getTo(),pathToFile+"/img/");
        }
    }

    @Override
    public void importOrUpdateFullPage(Long tildaPageId) throws Exception {
        //Запрос к Тильде
        String importResponse = restTemplate.getForObject(buildPageUrl(tildaPageId), String.class);
        //парсим ответ
        PageExportResponse mappedResponse = mapper.readValue(importResponse, PageExportResponse.class);
        if(!mappedResponse.getStatus().equals("FOUND")){
            log.debug("Неожиданный ответ сервера при запросе TildaPageId:{} - {}", tildaPageId, mappedResponse.getStatus());
            return;
        }
        //загружаем изображения страницы
        for(PageExportResponse.Result.ExportFileMap image: mappedResponse.getResult().getImages()){
            getFile(image.getFrom(), image.getTo(),pathToFile+"/img/");
        }
        // Всегда получаем файлы проекта. Если они уже загруженны - ничего не произойдет.
        importProjectResources(mappedResponse.getResult().getProjectid());
        //импорт или update
        Optional<TildaPage> tildaPage = tildaPageRepository.findOneByTildaPageId(tildaPageId);
        if (tildaPage.isPresent()){
                TildaPage tp = tildaPage.get();
                tp.setUrl(mappedResponse.getResult().getAlias());
                tp.setContent(mappedResponse.getResult().getHtml());
                tildaPageRepository.save(tp);
            } else {
            TildaPage page = new TildaPage();
            page.setTildaPageId(mappedResponse.getResult().getId());
            page.setTildaProjectId(mappedResponse.getResult().getProjectid());
            page.setUrl(mappedResponse.getResult().getAlias());
            page.setContent(mappedResponse.getResult().getHtml());

            tildaPageRepository.save(page);
        }
    }

    private String buildResourcesUrl(Long projectId){
        String url = UriComponentsBuilder
                .fromUriString("http://api.tildacdn.info/v1/getprojectexport")
                .queryParam("publickey", reqPublicKey)
                .queryParam("secretkey", reqSecretKey)
                .queryParam("projectid", projectId.toString())
                .build().toUriString();

        return url;
    }

    private String buildPageUrl(Long pageId){
        String url = UriComponentsBuilder
                .fromUriString("http://api.tildacdn.info/v1/getpagefullexport")
                .queryParam("publickey", reqPublicKey)
                .queryParam("secretkey", reqSecretKey)
                .queryParam("pageid", pageId.toString())
                .build().toUriString();

        return url;
    }

    private void getFile(String fromUrl, String name, String path) throws IOException {

        Path filePath = Paths.get(path + name);

        if(Files.exists(filePath)){
            log.debug("Файл с именем {} уже загружен", name);
            return;
        }

        HttpEntity<String> entity = new HttpEntity<String>(headers);
        log.debug("Попытка загрузки файла {}", fromUrl);

        ResponseEntity<byte[]> response = restTemplate.exchange(fromUrl, HttpMethod.GET,
                entity, byte[].class, 1);

        if (response.getStatusCode() == HttpStatus.OK){
            log.debug("Файл {} успено получен", fromUrl);
            Files.createFile(filePath);
            Files.write(filePath, response.getBody());
            log.debug("Файл {} полученный по ссылке {} успешно сохранен в {}", name, fromUrl, path);
        }

    }

}
