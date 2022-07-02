package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import su.reddot.domain.model.esputink.event.EmailSubscribedEvent;
import su.reddot.infrastructure.esputink.EmailSubscriptionRequest;
import su.reddot.infrastructure.util.ErrorNotification;

import javax.validation.Valid;

import static su.reddot.presentation.Utils.mapErrors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class EmailSubscriptionController {

    private final ApplicationEventPublisher publisher;

    @PostMapping("/subscribe/email")
    public ResponseEntity<?> subscribe(@Valid EmailSubscriptionRequest emailSubscriptionRequest, BindingResult bindingResult){

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(mapErrors(bindingResult));
        }
        ErrorNotification subscribeNotification = new ErrorNotification();
        String email = emailSubscriptionRequest.getEmail();
        String name = emailSubscriptionRequest.getName();

        //logger.debug("Adding event EmailSubscribedEvent ("+email+")");

        publisher.publishEvent(new EmailSubscribedEvent(email, name));

        if (subscribeNotification.hasErrors()) {
            return ResponseEntity.badRequest().body(subscribeNotification.getAll());
        }

        return ResponseEntity.ok("Спасибо! Подписка удачно оформлена!");
    }


}
