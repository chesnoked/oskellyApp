package su.reddot.infrastructure.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class ResourceConfiguration extends WebMvcConfigurerAdapter {

    @Value("${resources.images.pathToDir}")
    private String imagePath;

    @Value("${resources.images.urlPrefix}")
    private String imagePrefix;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (!imagePath.startsWith("file:")) {
            imagePath = "file:" + imagePath;
        }
        registry.addResourceHandler(imagePrefix + "**").addResourceLocations(imagePath);
    }
}
