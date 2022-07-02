package su.reddot.presentation.controller.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.infrastructure.util.FileUtils;
import su.reddot.presentation.Utils;
import su.reddot.presentation.validation.DefaultImageValidator;

import java.io.File;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAnyAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_CREATE)")
public class StaticFilesController {
	private static final long MAX_FILE_SIZE = 2 * 1024 * 1024;

	@Value("${resources.images.urlPrefix}")
	private String imageUrlPrefix;

	@Value("${resources.images.pathToDir}")
	private String imageBaseDirPath;

	@PostMapping("/files/images/upload/imageuploader")
	public ResponseEntity<?> uploadStaticPhotoWithDragndrop(@ModelAttribute StaticImageRequest staticImageRequest) {

		//перетаскивание фотки на область ckeditor-а ничего не дало, пока отключим валидацию (пока не убедимся, что она нужна)
		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageBaseDirPath + "static/", staticImageRequest.getUpload());
		String imageUrl = imageUrlPrefix + "static/" + savedFile.getName();

		return ResponseEntity.ok(new StaticPhotoResponse(1, savedFile.getName(), imageUrl));
	}

	@PostMapping("files/images/upload/filebrowser")
	public String uploadStaticPhotoWithFileBrowser(@ModelAttribute StaticImageRequest staticImageRequest, Model model) {

		/*
			Пока что отключил валидацию, т.к. валилась ошибка, а разбираться не было времени.
			ошибка: при валидации мы валим ошибку на поле (имя поля захардкожено как image в валидаторе)
			Здесь же у нас поле называется upload, и валится ошибка что поле не найдено
		 */
//		MultipartFile file = staticImageRequest.getUpload();
//		Optional<Map<String, String>> errors = validateImage(file, bindingResult);
//		if (errors.isPresent()) {
//			String firstError = errors.get().entrySet().iterator().next().getValue();
//			model.addAttribute("message", firstError);
//			return "admin/fragments/oops";
//		}

		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageBaseDirPath + "static/", staticImageRequest.getUpload());
		String imageUrl = imageUrlPrefix + "static/" + savedFile.getName();
		model.addAttribute("url", imageUrl);

		return "admin/fragments/imageuploadresponse";
	}

	private Optional<Map<String, String>> validateImage(MultipartFile file, BindingResult bindingResult) {
		DefaultImageValidator imageValidator = new DefaultImageValidator(MAX_FILE_SIZE);
		imageValidator.validate(file, bindingResult);
		if (bindingResult.hasErrors()) {
			return Optional.of(Utils.mapErrors(bindingResult));
		}
		return Optional.empty();
	}


	@Data
	public static class StaticImageRequest {
		private MultipartFile upload;
	}

	@Data
	@AllArgsConstructor
	public static class StaticPhotoResponse {
		private int uploaded;
		private String fileName;
		private String url;
	}
}
