package su.reddot.presentation.mobile.api.v1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.publication.exception.PublicationException;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class MobileApiAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    SimpleResponse handle(ProductNotFoundException e) {
        return new SimpleResponse("Товар не найден");
    }

    @ExceptionHandler(ProductCanNotBeAddedToCartException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    SimpleResponse handle(ProductCanNotBeAddedToCartException e) {
        return new SimpleResponse(e.getMessage());
    }

    @ExceptionHandler({OrderCreationException.class, IllegalArgumentException.class, PublicationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    SimpleResponse handleClientSideExceptions(Exception e) {
        return new SimpleResponse(e.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    SimpleResponse handle(IllegalStateException e) {
        log.error(e.getMessage(), e);
        return new SimpleResponse("Ошибка сервера");
    }

}
