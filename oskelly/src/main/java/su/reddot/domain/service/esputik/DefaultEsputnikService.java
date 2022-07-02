package su.reddot.domain.service.esputik;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import su.reddot.domain.model.esputink.event.EmailSubscribedEvent;
import ua.com.esputnik.dto.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultEsputnikService extends WebServiceGatewaySupport implements EsputnikService {

    @Setter
    @Value("${esputnik.url}")
    private String url;

    @Setter
    @Value("${esputnik.subscribeMethod}")
    private String subscribeMethod;

    @Setter
    @Value("${esputnik.subscribeGroup}")
    private String subscribeGroup;

    @Setter
    @Value("${esputnik.login}")
    private String login;

    @Setter
    @Value("${esputnik.password}")
    private String password;

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final ObjectMapper mapper;

    @Override
    @Async @EventListener
    @Transactional
    public void subscribe(EmailSubscribedEvent event) throws JsonProcessingException {

        SubscribeContact subscribeContact = objectFactory.createSubscribeContact();
        Contact contact = objectFactory.createContact();
        Channel channel = objectFactory.createChannel();

        channel.setType(ChannelType.EMAIL);
        channel.setValue(event.getEmail());

        contact.setFirstName(event.getName());
        contact.getChannels().add(channel);

        subscribeContact.setContact(contact);
        subscribeContact.getGroups().add(subscribeGroup);

        EsputinkRequest esputinkRequest = from(subscribeContact);
        String jsonString = mapper.writeValueAsString(esputinkRequest);

        Client client = ClientBuilder.newClient();
        String usernameAndPassword = login + ":" + password;
        String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );

        Response response = client
                .target(url)
                .path(subscribeMethod)
                .request(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizationHeaderValue)
                .post(Entity.entity(jsonString, MediaType.APPLICATION_JSON));

        int statusCode = response.getStatus();
        String result = response.readEntity(String.class);

        log.debug("Esputnik Response ("+statusCode+"):"+result);



    }


    @Getter
    @Setter @Accessors(chain = true)
    @RequiredArgsConstructor
    private static class EsputinkRequest {
        private EsputnikContact contact;
        private List<String> groups = new ArrayList<>();

        @Getter @Setter @Accessors(chain = true)
        @RequiredArgsConstructor
        private static class EsputikChannel {
            private String type;
            private String value;
        }

        @Getter
        @Setter @Accessors(chain = true)
        @RequiredArgsConstructor
        private static class EsputnikContact {
            private String firstName;
            private String lastName = "";
            private List<EsputikChannel> channels = new ArrayList<>();
        }
    }

    private EsputinkRequest.EsputikChannel from(Channel espChannel){
        return new EsputinkRequest.EsputikChannel()
                .setType(espChannel.getType().value())
                .setValue(espChannel.getValue());


    }

    private EsputinkRequest.EsputnikContact from(Contact contact){
         EsputinkRequest.EsputnikContact req = new EsputinkRequest.EsputnikContact().setFirstName(contact.getFirstName())
                .setLastName(contact.getLastName() != null  ? contact.getLastName() : "").setChannels(contact.getChannels().stream()
                        .map(channel -> from(channel))
                        .collect(Collectors.toList()));

         return req;
    }

    private EsputinkRequest from(SubscribeContact subscribeContact){

        EsputinkRequest.EsputnikContact contact = from(subscribeContact.getContact());
        return new EsputinkRequest()
                .setContact(contact)
                .setGroups(subscribeContact.getGroups());
    }

}
