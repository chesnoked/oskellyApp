package su.reddot.domain.service.catalog.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import su.reddot.domain.dao.SizeRepository;
import su.reddot.domain.dao.attribute.AttributeRepository;
import su.reddot.domain.dao.attribute.AttributeValueRepository;
import su.reddot.domain.dao.category.CategoryAttributeBindingRepository;
import su.reddot.domain.dao.category.CategoryRepository;
import su.reddot.domain.model.attribute.Attribute;
import su.reddot.domain.model.attribute.AttributeValue;
import su.reddot.domain.model.attribute.SimpleAttributeValue;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.model.category.CategoryAttributeBinding;
import su.reddot.domain.model.size.Size;
import su.reddot.domain.model.size.SizeType;
import su.reddot.domain.service.catalog.CatalogAttribute;
import su.reddot.domain.service.catalog.CatalogCategory;
import su.reddot.domain.service.catalog.CategoryService;
import su.reddot.domain.service.catalog.size.CatalogSize;
import su.reddot.domain.service.catalog.size.SizeView;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static su.reddot.domain.model.category.QCategory.category;

/**
 * Сервис, предоставляющий данные о каталоге, иерархическая структура которого
 * реализована через вложенные множества.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class NestedSetsCategoryService implements CategoryService {
	private final CategoryRepository categoryRepository;
	private final CategoryAttributeBindingRepository categoryAttributeRepository;
	private final AttributeValueRepository attributeValueRepository;
	private final SizeRepository sizeRepository;

	private final AttributeRepository attributeRepository;
	// TODO удалить после получения эталонного состояния каталога
	@Value(value = "classpath:categories")
	private Resource rawCategories;

	// TODO удалить после получения эталонного состояния каталога
	@Value(value = "classpath:attributes")
	private Resource rawAttributes;

	@Override
	public List<CatalogCategory> getEntireCatalog() {

        /* Пропускаем настоящий корень дерева: он не содержит информации о каталоге
         * и не должен отдаваться клиенту. У корня дерева левый порядок всегда равен 1.
         *
         * Корневые категории каталога, хоть и выглядят "корневыми" для пользователя,
         * в действительности являются дочерними категориями единственного корня дерева.
         */
		List<Category>   rawCategories      = categoryRepository.findAll();
		List<BigInteger> emptyCategoriesIds = categoryRepository.findEmpty();

        /* Храним ссылки на обработанные категории (цель хранения ссылок - см. ниже) */
		Map<Long, CatalogCategory> categoriesTemporaryCache = new HashMap<>();

		List<CatalogCategory> cookedCategories = new ArrayList<>();

		for (Category rawCategory : rawCategories) {
			boolean categoryIsEmpty = emptyCategoriesIds.contains(BigInteger.valueOf(rawCategory.getId()));

			CatalogCategory cookedCategory = new CatalogCategory(
					rawCategory.getId(),
					rawCategory.getDisplayName(),
					rawCategory.hasChildren(),
					categoryIsEmpty)
                .setUrlName(rawCategory.getUrlName());

			Long thisCategoryParentId = rawCategory.getParent().getId();

            /* Если у текущей категории есть родитель
             * (которого мы неизбежно рассмотрели ранее и запомнили ссылку на него в кэше)
             * то добавить текущую категорию в список детей ее родителя.
             */
			CatalogCategory alreadyObtainedParent = categoriesTemporaryCache.get(thisCategoryParentId);
			if (alreadyObtainedParent != null) {
				alreadyObtainedParent.getChildren().add(cookedCategory);
			} else {
				cookedCategories.add(cookedCategory);
			}

            /* Сохраняем категорию в кэше для того, чтобы добавить к ней ее дочерние категории,
             * которые мы получим при следующих итерациях
             */
			if (rawCategory.hasChildren()) {
				categoriesTemporaryCache.put(rawCategory.getId(), cookedCategory);
			}
		}

		return cookedCategories.stream()
				.filter(c -> !Objects.equals(c.getDisplayName(), "Стиль жизни"))
				.collect(Collectors.toList());

	}

	@Override
	public List<CatalogCategory> getDirectChildrenCategories(Long parentCategoryId) {
	    return getDirectChildrenCategories(parentCategoryId, false);
	}

	@Override
	public List<CatalogCategory> getDirectChildrenCategories(Long parentCategoryId, boolean nonEmptyOnly) {

		Stream<Category> childrenCategories = categoryRepository.findChildrenCategories(parentCategoryId).stream();

			/* только те категории, у которых есть товары */
		if (nonEmptyOnly) {
			List<BigInteger> emptyCategories = categoryRepository.findEmpty();
			childrenCategories =  childrenCategories.filter(
					c -> !emptyCategories.contains(BigInteger.valueOf(c.getId())));
		}

		return childrenCategories
				.map(c -> new CatalogCategory(c.getId(), c.getDisplayName(), c.hasChildren())
                    .setUrlName(c.getUrlName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<CatalogCategory> getAllParentCategories(Long categoryId) {
		List<Category> parents = categoryRepository.findAllParents(categoryId);
		return parents.stream()
				.map(c -> new CatalogCategory(c.getId(), c.getDisplayName(), c.hasChildren())
                    .setUrlName(c.getUrlName()))
				.collect(Collectors.toList());
	}

	@Override
	public List<Long> getLeafCategoriesIds(Long parentId) {

		List<Category> leafCategories
				= categoryRepository.findLeafCategories(parentId);

		return leafCategories.isEmpty()?
				Collections.singletonList(parentId)
				: leafCategories.stream().map(Category::getId).collect(Collectors.toList());
	}

	@Override
	public List<CatalogCategory> getLeafCategories(Long parentId) {
		List<Category> leafCategories = parentId != null ? categoryRepository.findLeafCategories(parentId) : categoryRepository.findAllLeafCategories();
		return leafCategories.stream()
				.map(c -> new CatalogCategory(c.getId(), c.getDisplayName(), c.hasChildren())
                    .setUrlName(c.getUrlName()))
				.collect(Collectors.toList());
	}

	@Override
	public Optional<CatalogCategory> findById(Long id) {

        Category c = categoryRepository.findOne(id);
        if (c == null || c.getLeftOrder() == 1) { return Optional.empty(); }

		CatalogCategory foundCategory
				= new CatalogCategory(c.getId(), c.getDisplayName(), c.hasChildren())
                    .setFullName(c.getFullName())
					.setUrlName(c.getUrlName());

		return Optional.of(foundCategory);
	}

	@Override
	public Optional<CatalogCategory> findByUrl(String url) {

		Category c = categoryRepository.findOne(category.urlName.eq(url));
		if (c == null || c.getLeftOrder() == 1) { return Optional.empty(); }

		CatalogCategory foundCategory = new CatalogCategory(c.getId(), c.getDisplayName(), c.hasChildren())
            .setFullName(c.getFullName())
            .setUrlName(c.getUrlName());

		return Optional.of(foundCategory);
	}

	/**
	 * Получить список всех атрибутов для данной категории, включая те,
	 * что наследуются от родительских категорий.
	 *
	 * @param categoryId идентификатор категории
	 * @return список атрибутов и их возможные значения
	 */
	public List<CatalogAttribute> getAllAttributes(Long categoryId) {

		// список всех категорий: родительские категории + данная категория, от старших к младшим
		List<Category> allCategories = categoryRepository.findAllParents(categoryId);
		allCategories.add(categoryRepository.findOne(categoryId));

		List<CatalogAttribute> cookedAttributes = new ArrayList<>();

		List<Attribute> attributes
				= categoryAttributeRepository.findAllAttributesByCategoryInOrderByAttribute(allCategories).stream()
				.map(CategoryAttributeBinding::getAttribute).collect(Collectors.toList());

		// получить возможные значения для данного атрибута
		for (Attribute attribute : attributes) {
			List<SimpleAttributeValue> attributeValues
					= attributeValueRepository.findAllValuesByAttributeOrderById(attribute);

			cookedAttributes.add(new CatalogAttribute(attribute, attributeValues));
		}
		return cookedAttributes;
	}

	@Override
	public List<CatalogSize> getSizesGroupedBySizeType(Long categoryId) {

		List<Category> parentCategoriesAndSelf = categoryRepository.findAllParents(categoryId);
		parentCategoriesAndSelf.add(categoryRepository.findOne(categoryId));
		List<Size> sizes = sizeRepository.findSizes(parentCategoriesAndSelf);

		return cookSizes(sizes);
	}

	@Override
	public Optional<Category> findOne(Long id) {
		return Optional.ofNullable(categoryRepository.findOne(id));
	}

	@Override
	@Transactional
	public Category create(Long parentId, String displayName, String urlName, String singularName) {

		if (parentId == null)    { throw new IllegalArgumentException("[parentId] не может быть null"); }
	    if (displayName == null) { throw new IllegalArgumentException("[displayName] не может быть null"); }
		if (urlName == null) 	 { throw new IllegalArgumentException("[urlName] не может быть null"); }

		// FIXME проверять наличие уже существующих товаров у категории - родителя.
		// Если товары есть, то для сохранения целостности нужно запрещать добавление новой категории
		Category parent = categoryRepository.findOne(parentId);
	    if (parent == null) { throw new IllegalArgumentException("Категоря с id: " + parentId + " не существует"); }

		Integer newCategoryLeftOrder = parent.getLeftOrder() + 1;

	    categoryRepository.shiftLeftOrdersBeforeInsertingNewCategory(newCategoryLeftOrder);
		categoryRepository.shiftRightOrdersBeforeInsertingNewCategory(newCategoryLeftOrder);

		Category newCategory = new Category();
		newCategory.setParent(parent);
		newCategory.setDisplayName(displayName);
		newCategory.setUrlName(urlName);
		newCategory.setLeftOrder(newCategoryLeftOrder);
		newCategory.setRightOrder(newCategoryLeftOrder + 1);
		if (singularName != null) { newCategory.setSingularName(singularName); }

		return categoryRepository.save(newCategory);
	}

	@Override
	@Transactional
	public void temporaryInit() {
		try {
			ObjectMapper om = new ObjectMapper();
			InputStream is = rawCategories.getInputStream();

//			createAttributesWithTheirValues(om);

			List<RawCategory> categories = Arrays.asList(om.readValue(is, RawCategory[].class));

			RawCategory root = new RawCategory("", "", "", categories, null);
			persistCategory(root, categoryRepository.findOne(1L), 1, 2, true);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	private List<CatalogSize> cookSizes(List<Size> sizes) {
		List<CatalogSize> cookedSizes = new ArrayList<>();

		for (SizeType sizeType : SizeType.values()) {

			List<SizeView> sizesOfGivenType = sizes.stream()
					.filter(s -> s.getBySizeType(sizeType) != null)
					.map(s -> new SizeView(s.getId(), s.getBySizeType(sizeType), null))
					.collect(Collectors.toList());

			if (!sizesOfGivenType.isEmpty()) {
				cookedSizes.add(new CatalogSize(sizeType, sizesOfGivenType));
			}
		}

		return cookedSizes;
	}

	private void createAttributesWithTheirValues(ObjectMapper om) throws IOException {
		InputStream is = rawAttributes.getInputStream();

		List<RawAttribute> rawAttributes = Arrays.asList(om.readValue(is, RawAttribute[].class));

		for (RawAttribute rawAttribute : rawAttributes) {
			Attribute a = new Attribute();
			a.setName(rawAttribute.getName());
			attributeRepository.save(a);

		    for (String s : rawAttribute.getValues()) {
				AttributeValue av = new AttributeValue();
				av.setValue(s);
				av.setAttribute(a);
				attributeValueRepository.save(av);
			}
		}
	}

	private Category persistCategory(RawCategory category, Category parent, Integer categoryLeftOrder, Integer categoryRightOrder, boolean isRoot) {
		Category categoryToPersist = new Category();
		categoryToPersist.setDisplayName(category.getDisplayName());
		categoryToPersist.setUrlName(category.getUrlName());
		categoryToPersist.setSingularName(category.getSingularName());
		categoryToPersist.setParent(parent);

		categoryToPersist.setLeftOrder(categoryLeftOrder);
		categoryToPersist.setRightOrder(categoryRightOrder);

		Category persistedCategory;
		if (isRoot) {
			persistedCategory = parent;
		} else {

			categoryRepository.shiftLeftOrdersBeforeInsertingNewCategory(categoryLeftOrder);
			categoryRepository.shiftRightOrdersBeforeInsertingNewCategory(categoryLeftOrder);
			persistedCategory = categoryRepository.save(categoryToPersist);

			if (category.getAttributes() != null) {
				for (String s : category.getAttributes()) {
					Attribute attribute = attributeRepository.findByName(s);
					CategoryAttributeBinding categoryAttributeBinding = new CategoryAttributeBinding();
					categoryAttributeBinding.setAttribute(attribute);
					categoryAttributeBinding.setCategory(persistedCategory);
					categoryAttributeRepository.save(categoryAttributeBinding);
				}
			}
		}

		if (category.getChildren() == null) {
			return persistedCategory;
		}

		Category lastPersistedCategory = null;
		for (int i = 0; i < category.getChildren().size(); i++) {
			RawCategory childCategory = category.getChildren().get(i);

			if (i == 0) {
				lastPersistedCategory = persistCategory(childCategory, persistedCategory,
						persistedCategory.getLeftOrder() + 1,
						persistedCategory.getLeftOrder() + 2, false);
			} else {
				lastPersistedCategory = categoryRepository.findOne(lastPersistedCategory.getId());
				lastPersistedCategory = persistCategory(childCategory, lastPersistedCategory.getParent(),
						lastPersistedCategory.getRightOrder() + 1,
						lastPersistedCategory.getRightOrder() + 2, false);
			}
		}

		return persistedCategory;
	}

	@Getter @Setter @NoArgsConstructor
	private static class RawAttribute {
		private String name;
		private List<String> values;
	}
}

@Data
@AllArgsConstructor
class RawCategory {
	private String displayName;
	private String urlName;
	private String singularName;
	private List<RawCategory> children;
	private List<String> attributes;
}

