package su.reddot.domain.service.staticPage.impl;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import su.reddot.domain.dao.StaticPageRepository;
import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.staticPage.StaticPageGroup;
import su.reddot.domain.model.staticPage.StaticPageStatus;
import su.reddot.domain.model.staticPage.StaticPageTag;
import su.reddot.domain.model.user.User;
import su.reddot.domain.service.staticPage.StaticPageException;
import su.reddot.domain.service.staticPage.StaticPageRequest;
import su.reddot.domain.service.staticPage.StaticPageService;
import su.reddot.domain.service.staticPage.StaticPageTagService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.util.FileUtils;
import su.reddot.infrastructure.util.Utils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DefaultStaticPageService implements StaticPageService {

	private final StaticPageRepository staticPageRepository;
	private final UserService userService;
	private final StaticPageTagService tagService;

	@Value("${resources.images.pathToDir}")
	@Setter
	private String imageBaseDirPath;

	@Override
	public List<StaticPage> getAllStaticPages() {
		return staticPageRepository.findAllByOrderByIdAsc();
	}

	@Override
	public Optional<StaticPage> getStaticPageById(Long id) {
		StaticPage staticPage = staticPageRepository.findOne(id);
		return Optional.ofNullable(staticPage);
	}

	@Override
	public StaticPage createStaticPage(StaticPageRequest request, StaticPageGroup pageGroup, Long userId) throws StaticPageException {
		final StaticPage page = new StaticPage();
		page.setContent(request.getContent());
		page.setName(request.getName());
		page.setMetaDescription(request.getMetaDescription());
		page.setMetaKeywords(request.getMetaKeywords());

		Optional<User> user = userService.getUserById(userId);
		if(!user.isPresent()){
			throw new StaticPageException("Ошибка пользователя");
		}
		page.setModifiedBy(user.get());
		page.setModifiedAt(ZonedDateTime.now());

		String url = Utils.normalizeUrl(request.getUrl());
		page.setUrl(url);
		page.setPageGroup(pageGroup);
		saveTagIfNeed(request, page);
		saveImageIfNeed(request, page);
		if(pageGroup.equals(StaticPageGroup.BLOG)) {
			page.setStatus(StaticPageStatus.DRAFT);
			Optional<StaticPage> pageWithSameUrl = staticPageRepository.findByUrl(url);
			if(pageWithSameUrl.isPresent()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
				String newUrl = request.getUrl() + LocalDateTime.now().format(formatter);
				page.setUrl(Utils.normalizeUrl(newUrl));
			}
		}
		return savePageWithExceptionHanding(page);
	}

	private void saveImageIfNeed(StaticPageRequest request, StaticPage page) {
		if(request.getImage() != null) {
			String dir = String.format("static%s/", page.getUrl());
			String fullDir = imageBaseDirPath + dir;
			File savedFile = FileUtils.saveMultipartFileWithGeneratedName(fullDir, request.getImage());
			if(page.getImagePath() != null) {
				String oldPath = imageBaseDirPath + page.getImagePath();
				FileUtils.deleteFile(oldPath);
			}
			page.setImagePath(dir + savedFile.getName());
		}
	}

	private void saveTagIfNeed(StaticPageRequest request, StaticPage page) throws StaticPageException {
		if(request.getTagId() != null) {
			Optional<StaticPageTag> tag = tagService.findOne(request.getTagId());
			if(!tag.isPresent()) {
				throw new StaticPageException("Тэг не найден");
			}
			page.setTag(tag.get());
		}
	}

	@Override
	public void updateStaticPage(StaticPageRequest request, Long userId) throws StaticPageException, NullPointerException {
		Long id = request.getId();
		if (id == null) {
			throw new NullPointerException("Не указан Id статической страницы для обновления");
		}
		StaticPage page = staticPageRepository.findOne(id);
		if (page == null) {
			throw new NullPointerException("Неправильно указан Id статической страницы для обновления");
		}

		String url = Utils.normalizeUrl(request.getUrl());
		if(page.getPageGroup() != StaticPageGroup.BLOG) {
			page.setUrl(url);
		}
		page.setMetaDescription(request.getMetaDescription());
		page.setMetaKeywords(request.getMetaKeywords());
		page.setName(request.getName());
		page.setContent(request.getContent());

		Optional<User> user = userService.getUserById(userId);
		if(!user.isPresent()){
			throw new StaticPageException("Ошибка пользователя");
		}
		page.setModifiedBy(user.get());
		page.setModifiedAt(ZonedDateTime.now());

		saveTagIfNeed(request, page);
		saveImageIfNeed(request, page);

		this.savePageWithExceptionHanding(page);
	}

	private StaticPage savePageWithExceptionHanding(StaticPage page) throws StaticPageException {
		try {
			return staticPageRepository.save(page);
		} catch (DataIntegrityViolationException e) {
			log.error(e.getLocalizedMessage(), e);
			throw new StaticPageException("Данный URL уже принадлежит другой странице");

		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			throw new StaticPageException("Неожиданная ошибка");
		}
	}

	@Override
	public void deleteStaticPageById(Long id) throws NullPointerException {
		StaticPage page = staticPageRepository.findOne(id);
		if (page == null) {
			throw new NullPointerException("Неправильно указан Id для удаления страницы");
		}
		staticPageRepository.delete(page);
	}

	@Override
	public Optional<StaticPage> getStaticPageByPageGroupAndUrl(String pageGroup, String url) {
		pageGroup = Utils.normalizeUrl(pageGroup);
		url = Utils.normalizeUrl(url);

		Optional<StaticPageGroup> staticPageGroup = StaticPageGroup.getGroupByUrl(pageGroup);
		if (!staticPageGroup.isPresent()) {
			log.error("Не найдена группа статических страниц");
			return Optional.empty();
		}
		StaticPage staticPage = staticPageRepository.findByPageGroupAndUrl(staticPageGroup.get(), url);
		return Optional.ofNullable(staticPage);
	}

	@Override
	public List<StaticPage> getStaticPageByGroup(StaticPageGroup staticPageGroup) {
		return staticPageRepository.findByPageGroupAndIsRawHtmlEqualsOrderByIdAsc(staticPageGroup, false);
	}

	@Override
	public List<StaticPage> getStaticPageByGroupDesc(StaticPageGroup staticPageGroup) {
		return staticPageRepository.findByPageGroupOrderByIdDesc(staticPageGroup);
	}

	@Override
	public List<StaticPage> getStaticPagesForBlog() {
		return staticPageRepository.findByPageGroupAndStatusOrderByIdDesc(StaticPageGroup.BLOG, StaticPageStatus.PUBLISHED);
	}

	@Override
	public List<StaticPage> getStaticPagesForBlog(Optional<StaticPageTag> tag){
		if (tag.isPresent()){
			return staticPageRepository.findByPageGroupAndStatusAndTagOrderByIdDesc(StaticPageGroup.BLOG, StaticPageStatus.PUBLISHED, tag.get());
		}
		else { return getStaticPagesForBlog(); }
	}

	@Override
	public void publish(Long id, Long userId) throws StaticPageException {
		StaticPage page = staticPageRepository.findOne(id);
		if (page == null) {
			throw new StaticPageException("Неправильно указан Id статической страницы");
		}
		Optional<User> user = userService.getUserById(userId);
		if(!user.isPresent()){
			throw new StaticPageException("Ошибка пользователя");
		}
		if(page.getStatus() == null || page.getStatus().equals(StaticPageStatus.DRAFT)) {
			page.setStatus(StaticPageStatus.PUBLISHED);
			page.setModifiedAt(ZonedDateTime.now());
			page.setModifiedBy(user.get());
			staticPageRepository.save(page);
		}
	}
}
