package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import su.reddot.domain.exception.NotFoundException;
import su.reddot.domain.model.staticPage.StaticPage;
import su.reddot.domain.model.staticPage.StaticPageGroup;
import su.reddot.domain.service.staticPage.StaticPageService;
import su.reddot.domain.service.staticPage.StaticPageTagService;
import su.reddot.infrastructure.tilda.TildaImporter;
import su.reddot.infrastructure.tilda.TildaPage;
import su.reddot.infrastructure.tilda.TildaPageRepository;
import su.reddot.presentation.mapper.admin.StaticPageMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class StaticPageController {

	private final StaticPageService staticPageService;
	private final StaticPageTagService staticPageTagService;
	private final StaticPageMapper staticPageMapper;

	//TODO написать на это говно сервис
	private final TildaImporter tildaImporter;
	private final TildaPageRepository tildaPageRepository;

	@GetMapping("/info/{url}")
	public String getStaticPage(@PathVariable("url") String url, Model model) throws NotFoundException {
		Optional<StaticPage> staticPage = staticPageService.getStaticPageByPageGroupAndUrl(StaticPageGroup.INFO.getUrl(), url);
		if (staticPage.isPresent()) {
			model.addAttribute("page", staticPage.get());
			return "static";
		} else {
			throw new NotFoundException("404:   /info/" + url + " are not found");

		}

	}

	@GetMapping("/{url}")
	public String getStaticPage(@PathVariable("url") String url) throws NotFoundException {
		List<String> patterns = Arrays.asList("sell|buy|welcome|app|makeoffer|partner|concierge-service|gift-card".split("\\|"));
		url = url.toLowerCase();
		if (patterns.contains(url)) {
			return "landing/" + url;
		} else {
			throw new NotFoundException("404:  /" + url + " are not found");
		}
	}

	//TODO выпилить как cделаю админку
	@GetMapping("/foo/{id}")
	public String foo(@PathVariable("id") Long id, Model m) throws Exception {
		m.addAttribute("tildaPageId", id);
		tildaImporter.importOrUpdateFullPage(id);
		return "admin/tilda-upd";
	}

	@GetMapping("/blog")
	public String getTildaMainPage(Model m, @RequestParam(value = "webView", defaultValue = "false") Boolean isWebView) throws NotFoundException {
		Optional<TildaPage> optionalTildaPage = tildaPageRepository.findFirstByIsMainPageEquals(true);
		if (optionalTildaPage.isPresent()) {
			TildaPage page = optionalTildaPage.get();
			String pageContent = page.getContent();
			if (isWebView) {
				pageContent = convertTildaPageForWebView(pageContent);
			}

			m.addAttribute("content", pageContent);
			return "tildapage";
		} else {
			throw new NotFoundException("404:  /blog" + " are not found");
		}

	}

	@GetMapping("/blog/{url}")
	public String getTildaPageByUrl(@PathVariable("url") String url,
	                                @RequestParam(value = "webView", defaultValue = "false") Boolean isWebView,
	                                Model m) throws NotFoundException {

		Optional<TildaPage> optionalTildaPage = tildaPageRepository.findFirstByUrl("blog/" + url);
		if (optionalTildaPage.isPresent()) {
			TildaPage page = optionalTildaPage.get();
			String pageContent = page.getContent();
			if (isWebView) {
				pageContent = convertTildaPageForWebView(pageContent);
			}

			m.addAttribute("content", pageContent);
			return "tildapage";
		} else {
			throw new NotFoundException("404:  /blog/" + url + " are not found");
		}

	}

	/**
	 * TODO: форматирование страницы вынести в сервис
	 * Конвертируем tilda-страницу для отображения в webView
	 * @param pageContent html тильда-страницы
	 * @return отформатированный html для отображения в webView
	 */
	private String convertTildaPageForWebView(String pageContent) {
		Document doc = Jsoup.parse(pageContent);

		Element header = doc.body().getElementById("t-header");
		header.remove();

		Element footer = doc.body().getElementById("t-footer");
		footer.remove();

		Elements hrefs = doc.getElementsByTag("a");
		hrefs.stream().filter(a -> a.attr("href").contains("/blog"))
				.forEach(this::convertHrefForWebView);

		return doc.outerHtml();
	}

	/**
	 * Добавляем к ссылке на блог get-параметр webView=true
	 * @param a Jsoup элемент ссылки, ведущей на другую страницу блога
	 */
	private void convertHrefForWebView(Element a) {
		try {
			String href = a.attr("href");
			if (href == null || href.trim().isEmpty()) {
				return;
			}
			URI uri = new URI(href);
			String query = uri.getQuery();
			if (query == null) {
				uri = new URI(uri.toString() + "?webView=true");
			} else if (!query.contains("webView")) {
				uri = new URI(uri.toString() + "&webView=true");
			}
			a.attr("href", uri.toString());
		} catch (URISyntaxException e) {
			log.error(e.getLocalizedMessage(), e);
		}
	}

}
