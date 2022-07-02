package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.staticPage.StaticPageGroup;
import su.reddot.domain.service.staticPage.StaticPageException;
import su.reddot.domain.service.staticPage.StaticPageRequest;
import su.reddot.domain.service.staticPage.StaticPageService;
import su.reddot.domain.service.staticPage.StaticPageTagService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;
import su.reddot.presentation.Utils;
import su.reddot.presentation.mapper.admin.StaticPageMapper;
import su.reddot.presentation.validation.DefaultImageValidator;
import su.reddot.presentation.view.admin.CreatedPageResponse;
import su.reddot.presentation.view.admin.StaticPagePreview;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Controller
@RequestMapping("/admin/static")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_CREATE,"
							+ "T(su.reddot.domain.model.enums.AuthorityName).CONTENT_DELETE)")
public class StaticPageAdminController {

	private final StaticPageService staticPageService;
	private final StaticPageMapper staticPageMapper;
	private final StaticPageTagService staticPageTagService;
	private final DefaultImageValidator imageValidator = new DefaultImageValidator(1024 * 1024 * 2);

	@Setter
	@Value("${resources.images.urlPrefix}")
	private String imageUrlPrefix;

	@GetMapping("/info")
	public String getInfoStaticPages(Model model) {
		List<StaticPage> infoPages = staticPageService.getStaticPageByGroup(StaticPageGroup.INFO);
		val previews = infoPages.stream().map(staticPageMapper::mapStaticPageToPreview).collect(toList());
		model.addAttribute("pages", previews);
		return "admin/content-management/info-pages";
	}

	@GetMapping("/blog")
	public String getBlogPages(Model m) {
		List<StaticPage> infoPages = staticPageService.getStaticPageByGroupDesc(StaticPageGroup.BLOG);
		List<StaticPagePreview> previews = infoPages.stream().map(staticPageMapper::mapStaticPageToPreview).collect(toList());
		m.addAttribute("pages", previews);
		return "admin/content-management/blog/list";
	}

	@GetMapping({"/{pageGroup:info|blog}/editor", "/{pageGroup:info|blog}/editor/", "/{pageGroup:info|blog}/editor/{id}"})
	public String createOrUpdatePage(@PathVariable("pageGroup") String pageGroupUrl,
									 @PathVariable(value = "id", required = false) Long id, Model model) {
		Optional<StaticPageGroup> pageGroup = StaticPageGroup.getGroupByUrl(pageGroupUrl);

		//Если неверно указана категория стат. страниц, выкидываем на окно с ошибкой
		if (!pageGroup.isPresent()) {
			model.addAttribute("errorMessage", "Страница не найдена");
			return "oops";
		}

		StaticPageGroup group = pageGroup.get();
		model.addAttribute("tags", staticPageTagService.findAll());

		//Если Id страницы не указан, создаем страницу с нуля
		if (id == null) {
			model.addAttribute("group", group);
			return "admin/content-management/static-page";
		}

		//Если по указанному Id страница не найдена, кидаем на создание страницы с нуля
		Optional<StaticPage> pageOptional = staticPageService.getStaticPageById(id);
		if (pageOptional == null || !pageOptional.isPresent()) {
			return "redirect:/admin/static/" + group.getUrl() + "/editor";
		}
		model.addAttribute("page", pageOptional.get());
		model.addAttribute("group", pageOptional.get().getPageGroup().equals(StaticPageGroup.BLOG) ? StaticPageGroup.BLOG : StaticPageGroup.INFO); // костыль, переработать
		model.addAttribute("modified", staticPageMapper.getModifierParamsForStaticPage(pageOptional.get()));
		model.addAttribute("imageUrlPrefix", imageUrlPrefix);
		return "admin/content-management/static-page";
	}

	@PostMapping({"/{pageGroup:info|blog}", "/{pageGroup:info|blog}/"})
	public ResponseEntity<?> createStaticPage(@PathVariable("pageGroup") String pageGroup,
	                                          @Valid StaticPageRequest pageRequest, BindingResult result,
	                                          UserIdAuthenticationToken token) {
		if(pageRequest.getImage() != null) {
			imageValidator.validate(pageRequest.getImage(), result);
		}
		Optional<StaticPageGroup> groupOptional = StaticPageGroup.getGroupByUrl(pageGroup);
		if (!groupOptional.isPresent()) {
			result.addError(new FieldError("Error", "Error", "Некорректная группа страниц"));
		}

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(result));
		}

		try {
			StaticPage createdPage = staticPageService.createStaticPage(
					pageRequest, groupOptional.get(), token.getUserId()
			);
			return ResponseEntity.ok().body(new CreatedPageResponse(createdPage.getId()));
		} catch (StaticPageException e) {
			log.error(e.getLocalizedMessage(), e);
			return Utils.badResponseWithFieldError("error", e.getLocalizedMessage());
		}
	}

	@PostMapping({"/{pageGroup:info|blog}/{id}", "/{pageGroup:info|blog}/{id}"})
	public ResponseEntity<?> updateStaticPage(@PathVariable("pageGroup") String pageGroup,
											  @PathVariable("id") Long id,
											  @Valid StaticPageRequest pageRequest,
											  BindingResult result,
											  UserIdAuthenticationToken token) {
		if(pageRequest.getImage() != null) {
			imageValidator.validate(pageRequest.getImage(), result);
		}
		Optional<StaticPageGroup> groupOptional = StaticPageGroup.getGroupByUrl(pageGroup);
		if (!groupOptional.isPresent()) {
			result.addError(new FieldError("Error", "Error", "Некорректная группа страниц"));
		}

		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(Utils.mapErrors(result));
		}

		try {
			staticPageService.updateStaticPage(pageRequest, token.getUserId());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return Utils.badResponseWithFieldError("error", e.getLocalizedMessage());
		}
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/pages/{id}")
	@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).CONTENT_DELETE)")
	public ResponseEntity<?> deleteStaticPage(@PathVariable("id") Long id) {
		try {
			staticPageService.deleteStaticPageById(id);
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(e.getLocalizedMessage());
		}
		return ResponseEntity.ok().build();
	}

	@PutMapping("/pages/publish/{id}")
	public ResponseEntity<?> publish(@PathVariable("id") Long id, UserIdAuthenticationToken token) {
		try {
			staticPageService.publish(id, token.getUserId());
		} catch (StaticPageException e) {
			log.error(e.getLocalizedMessage(), e);
			return ResponseEntity.badRequest().body(e.getLocalizedMessage());
		}
		return ResponseEntity.ok().build();
	}

	@ModelAttribute(name = "activeTab")
	String setActiveTab() { return "static content management"; }

}
