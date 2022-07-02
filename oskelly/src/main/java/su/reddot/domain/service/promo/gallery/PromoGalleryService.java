package su.reddot.domain.service.promo.gallery;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import su.reddot.domain.dao.promo.PromoGalleryDao;
import su.reddot.domain.model.promo.PromoGallery;
import su.reddot.domain.service.promo.PromoObjectService;
import su.reddot.infrastructure.util.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PromoGalleryService implements PromoObjectService<PromoGalleryResponse, PromoGalleryRequest> {

	private final PromoGalleryDao promoGalleryDao;

	/**
	 * Url директории, в которой находится картинка
	 */
	private String imagePrefix;

	/**
	 * Директория, в которой находится картинка
	 */
	private String imageDirectoryPath;

	@Autowired
	public PromoGalleryService(PromoGalleryDao promoGalleryDao,
							   @Value("${resources.images.urlPrefix}") String imageUrlPrefix,
							   @Value("${resources.images.pathToDir}") String imageDirectoryPath) {
		this.promoGalleryDao = promoGalleryDao;
		this.imagePrefix = imageUrlPrefix + "promo/gallery/";
		this.imageDirectoryPath = imageDirectoryPath + "promo/gallery/";
	}

	@Override
	public List<PromoGalleryResponse> getAll() {
		List<PromoGallery> promoGalleries = promoGalleryDao.findAllByOrderByOrderIndexAsc();
		return promoGalleries.stream()
				.map(this::mapGallery)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<PromoGalleryResponse> getById(Long id) {
		PromoGallery gallery = promoGalleryDao.findOne(id);
		if (gallery == null) {
			return Optional.empty();
		} else {
			return Optional.of(mapGallery(gallery));
		}
	}

	private PromoGalleryResponse mapGallery(PromoGallery g) {
		return PromoGalleryResponse.builder().id(g.getId()).orderIndex(g.getOrderIndex())
				.url(g.getUrl()).img(imagePrefix + g.getImageName()).build();
	}

	@Override
	public void create(PromoGalleryRequest obj) throws NullPointerException {
		MultipartFile image = obj.getImage();
		if (image == null) {
			throw new NullPointerException("Отсутствует изображение элемента фотогалереи");
		}
		File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageDirectoryPath, image);
		String url = normalizeUrl(obj.getUrl());

		Long orderIndex = obj.getOrderIndex() >= 1 ? obj.getOrderIndex() : 1;

		val gallery = new PromoGallery(url, savedFile.getName(), orderIndex);
		promoGalleryDao.save(gallery);
	}

	@Override
	public void update(PromoGalleryRequest obj) throws NullPointerException {
		Long id = obj.getId();
		if (id == null) {
			throw new NullPointerException("Отсутствует Id элемента фотогалереи");
		}

		PromoGallery gallery = promoGalleryDao.findOne(id);
		if (gallery == null) {
			throw new NullPointerException("Неправильно указан Id элемента фотогалереи");
		}
		MultipartFile file = obj.getImage();

		/*
		Если мы добавили другую фотографию, удаляем старую
		 */
		if (file != null && !file.isEmpty()) {
			FileUtils.deleteFile(imageDirectoryPath + gallery.getImageName());
			File savedFile = FileUtils.saveMultipartFileWithGeneratedName(imageDirectoryPath, file);
			gallery.setImageName(savedFile.getName());
		}

		Long orderIndex = obj.getOrderIndex() >= 1 ? obj.getOrderIndex() : 1;

		gallery.setUrl(normalizeUrl(obj.getUrl()));
		gallery.setOrderIndex(orderIndex);
		promoGalleryDao.save(gallery);
	}

	@Override
	public void delete(Long id) throws NullPointerException {
		PromoGallery gallery = promoGalleryDao.findOne(id);
		if (gallery == null) {
			throw new NullPointerException();
		}
		FileUtils.deleteFile(imageDirectoryPath + gallery.getImageName());
		promoGalleryDao.delete(gallery);
	}
}
