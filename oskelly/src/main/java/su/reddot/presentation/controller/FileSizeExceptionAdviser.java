package su.reddot.presentation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;
import su.reddot.presentation.Utils;

@ControllerAdvice
@Slf4j
public class FileSizeExceptionAdviser {

	@ExceptionHandler(value = MultipartException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<?> handleFileSizeException(Exception e) {
		log.error(e.getLocalizedMessage(), e);
		return ResponseEntity.badRequest().body(Utils.fieldError("file", "Слишком большой размер загружаемого файла"));
	}
}
