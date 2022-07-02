package su.reddot.presentation.api.v1;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.order.OrderException;
import su.reddot.domain.service.cart.exception.ProductCanNotBeAddedToCartException;
import su.reddot.domain.service.cart.exception.ProductNotFoundException;
import su.reddot.domain.service.order.exception.DiscountIsAlreadyUsedException;
import su.reddot.domain.service.order.exception.OrderCreationException;
import su.reddot.domain.service.product.PriceNegotiationException;
import su.reddot.domain.service.publication.exception.PublicationException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
public class ApiAdvice {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    SimpleResponse handle(Exception e) {
        return new SimpleResponse("Товар не найден");
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    SimpleResponse requestsForNonexistentStuff(Exception e) {
        return new SimpleResponse(e.getMessage());
    }

    @ExceptionHandler(ProductCanNotBeAddedToCartException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    SimpleResponse handle(ProductCanNotBeAddedToCartException e) {
        return new SimpleResponse(e.getMessage());
    }

    @ExceptionHandler({
            OrderCreationException.class,   OrderException.class,
            IllegalArgumentException.class, PriceNegotiationException.class,
            PublicationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    SimpleResponse wrongClientRequests(Exception e) {
        return new SimpleResponse(e.getMessage());
    }

    @ExceptionHandler(DiscountIsAlreadyUsedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    SimpleResponse wrongClientRequests(DiscountIsAlreadyUsedException e) {
        Long orderId = e.getOrder().getId();
        return new SimpleResponse(String.format("Эта скидка уже использована в заказе <a href='/orders/%s'>%s</a>",
                orderId, orderId));
    }


    /** Общая оБработка запросов с невалидными данными,
     * которые не проходият валидацию javax.validation */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    FieldErrorsResponse handle(MethodArgumentNotValidException e) {
        FieldErrorsResponse response = new FieldErrorsResponse();
        BindingResult bindingResult = e.getBindingResult();

        /* Ошибки по каждому из невалидных полей. */
        if (!bindingResult.getAllErrors().isEmpty()) {
            response.setErrors(bindingResult.getFieldErrors().stream()
                    .map(fe -> new FieldError(fe.getField(), fe.getDefaultMessage()))
                    .collect(Collectors.toList()));
        }

        /* Общая ошибка запроса - когда все поля по отдельности верны, но вместе содержат невалидные данные. */
        ObjectError globalError = e.getBindingResult().getGlobalError();
        if (globalError != null) {
            response.setGlobalError(globalError.getDefaultMessage());
        }

        return response;
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    SimpleResponse handle(IllegalStateException e) {
        log.error(e.getMessage(), e);
        return new SimpleResponse("Ошибка сервера. Попробуйте позже.");
    }

    /** Список полей запроса, которые содержат ошибочные данные */
    @Getter @Setter
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private class FieldErrorsResponse {

        /** Ошибки по каждому из полей. */
        private List<FieldError> errors;

        /** Пo отдельности каждое из полей может иметь правильное значение,
         * но в совокупности они содержат неправильные данные. */
        private String globalError;
    }

    @Getter @Setter @RequiredArgsConstructor
    private class FieldError {

        /* Название поля запроса, которое содержит ошибочне данные */
        private final String field;

        /* Сообщение об ошибке в человекочитаемом виде */
        private final String message;
    }
}
