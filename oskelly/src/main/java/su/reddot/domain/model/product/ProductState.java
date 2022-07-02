package su.reddot.domain.model.product;

/**
 * Статус товара в системе
 */
public enum ProductState {

	/**
	 * Черновик - товар зарегистрирован в системе, но его характеристики заполнены не окончательно
	 */
	DRAFT("Черновик"),

	/**
	 * Характеристики товара заполнены окончательно, ожидание модерации
	 */
	NEED_MODERATION("На модерации"),

	/**
	 * Товар был отклонен после модерации
	 */
	REJECTED("Отклонен"),

	/**
	 * Товар был успешно опубликован
	 */
	PUBLISHED("Опубликован"),

	/**
	 * Скрыть
	 */
	HIDDEN("Скрыт"),

	/**
	 * Товар был продан (?)
	 */
	SOLD("Продан"),

	/** Удален */
	DELETED("Снят с продажи");

	private String description;

	ProductState(String description) {
		this.description = description;
	};

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name();
	}
}
