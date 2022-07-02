package su.reddot.domain.service.promo.selection;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.dao.promo.PromoSelectionRepository;
import su.reddot.domain.model.promo.PromoSelection;
import su.reddot.domain.service.promo.PromoObjectService;
import su.reddot.infrastructure.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static su.reddot.domain.model.promo.PromoSelection.*;

@Service
public class PromoSelectionService implements PromoObjectService<PromoSelectionResponse, PromoSelectionRequest> {

	private final PromoSelectionRepository promoSelectionRepository;

	/**
	 * Url директории, в которой находится картинка
	 */
	private String imagePrefix;

	/**
	 * Директория, в которой находится картинка
	 */
	private String imageDirectoryPath;

	@Autowired
	public PromoSelectionService(PromoSelectionRepository promoSelectionRepository,
								 @Value("${resources.images.urlPrefix}") String imageUrlPrefix,
								 @Value("${resources.images.pathToDir}") String imageDirectoryPath) {
		this.promoSelectionRepository = promoSelectionRepository;
		this.imagePrefix = imageUrlPrefix + "promo/selection/";
		this.imageDirectoryPath = imageDirectoryPath + "promo/selection/";
	}

	@Override
	public List<PromoSelectionResponse> getAll() {
		return promoSelectionRepository.findAllByOrderByOrderIndexAsc().stream()
				.map(this::mapSelection)
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @return промоблоки для главной
	 */
	public List<PromoSelectionResponse> getIndexPromo(){
		return promoSelectionRepository.findAllByPromoGroupInOrderByOrderIndexAsc(PromoGroup.INDEX_PROMO)
				.stream()
				.map(this::mapSelection)
				.collect(Collectors.toList());
	}

	/**
	 *
	 * @return  Фавориты на главной
	 */
	public List<PromoSelectionResponse> getIndexFavorites(){
		return promoSelectionRepository.findAllByPromoGroupInOrderByOrderIndexAsc(PromoGroup.FAVORITES)
				.stream()
				.map(this::mapSelection)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<PromoSelectionResponse> getById(Long id) {
		PromoSelection selection = promoSelectionRepository.findOne(id);
		if (selection == null) {
			return Optional.empty();
		}
		return Optional.of(mapSelection(selection));
	}

	private PromoSelectionResponse mapSelection(PromoSelection s) {
		return PromoSelectionResponse.builder()
				.id(s.getId())
				.promoGroup(s.getPromoGroup())
				.orderIndex(s.getOrderIndex())
				.firstLine(s.getFirstLine())
				.secondLine(s.getSecondLine())
				.thirdLine(s.getThirdLine())
				.url(s.getUrl())
				.img(imagePrefix + s.getImageName())
				.alt(s.getAlt())
				.build();
	}

	@Override
	public void create(PromoSelectionRequest obj) throws NullPointerException, TooManyPromoException {

	    boolean maxGroupsCapacityReachedAlready = obj.getPromoGroup() == PromoGroup.INDEX_PROMO
				&& promoSelectionRepository.countByPromoGroup(PromoGroup.INDEX_PROMO) >= 3;

		if (maxGroupsCapacityReachedAlready) { throw new TooManyPromoException(); }

		MultipartFile image = obj.getImage();
		if (image == null) {
			throw new NullPointerException("Отсутствует изображение элемента фотогалереи");
		}
		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageDirectoryPath, image);
		String url = normalizeUrl(obj.getUrl());

		Long orderIndex = obj.getOrderIndex() != null && obj.getOrderIndex() >= 1 ? obj.getOrderIndex() : 1;

		val selection = new PromoSelection();
		selection.setFirstLine(obj.getFirstLine());
		selection.setSecondLine(obj.getSecondLine());
		selection.setThirdLine(obj.getThirdLine());
		selection.setUrl(url);
		selection.setImageName(savedFile.getName());
		selection.setOrderIndex(orderIndex);
		selection.setPromoGroup(obj.getPromoGroup());
		selection.setAlt(obj.getAlt());
		promoSelectionRepository.save(selection);
	}

	@Override
	public void update(PromoSelectionRequest obj) throws NullPointerException {
		Long id = obj.getId();
		if (id == null) {
			throw new NullPointerException("Отсутствует Id промоблока подборки товаров");
		}

		PromoSelection selection = promoSelectionRepository.findOne(id);
		if (selection == null) {
			throw new NullPointerException("Неправильно указан Id промоблока подборки товаров");
		}
		MultipartFile file = obj.getImage();

		/*
		Если мы добавили другую фотографию, удаляем старую
		 */
		if (file != null && !file.isEmpty()) {
			FileUtils.deleteFile(imageDirectoryPath + selection.getImageName());
			File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageDirectoryPath, file);
			selection.setImageName(savedFile.getName());
		}

		Long orderIndex = obj.getOrderIndex() >= 1 ? obj.getOrderIndex() : 1;

		selection.setUrl(normalizeUrl(obj.getUrl()));
		selection.setFirstLine(obj.getFirstLine());
		selection.setSecondLine(obj.getSecondLine());
		selection.setThirdLine(obj.getThirdLine());
		selection.setAlt(obj.getAlt());
		selection.setOrderIndex(orderIndex);
		promoSelectionRepository.save(selection);
	}

	@Override
	public void delete(Long id) throws NullPointerException {
		PromoSelection selection = promoSelectionRepository.findOne(id);
		if (selection == null) {
			throw new NullPointerException();
		}
		FileUtils.deleteFile(imageDirectoryPath + selection.getImageName());
		promoSelectionRepository.delete(selection);
	}
}
