package su.reddot.domain.model.category;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Getter @Setter
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String urlName;

	/**
	 * Название категории.
	 * Например: платья, брюки, ремни
	 **/
	private String displayName;

	/**
	 * Поле для наименования единицы товара из данной категории (платье, топ, кроссовки)
     *
	 * @apiNote необязательное поле
	 */
	private String singularName;

	/**
	 * Полное название категории.
	 * Как правило, включает в себя название какой-нибудь родительской категории.
	 * Например: женские платья, женская обувь, женское пальто, обувь для девочек.
     *
	 * @apiNote необязательное поле
	 **/
	private String fullName;

	private Integer leftOrder;

	private Integer rightOrder;

	@ManyToOne
	private Category parent;

	/**
	 * @return true если у категории есть дочерние категории, иначе false
	 */
	public boolean hasChildren() {
		return rightOrder - leftOrder > 1;
	}

	public boolean isLeaf() {
		return rightOrder - leftOrder == 1;
	}

	public int getCategoryLevel() {
		if (isRoot()) {
			return 0;
		}
		int level = 1;
		Category parent = this.getParent();
		while (!parent.isRoot()) {
			parent = parent.getParent();
			level++;
		}

		return level;
	}

	/** Родительские категории, от дальних к ближним. */
	public List<Category> getParents() {
		List<Category> parents = new ArrayList<>();

		Category parent = getParent();
		while (!parent.isRoot()) {
			parents.add(parent);
			parent = parent.getParent();
		}

		Collections.reverse(parents);

		return parents;
	}

	public String getNameForProduct() {
		//noinspection ConstantConditions singular_name - необязательное поле в таблице
		return singularName == null? displayName : singularName;
	}

	public boolean isLifeStyle() {
		for (Category c = this; !c.isRoot(); c = c.getParent()) {
			if ("стиль жизни".equals(c.getDisplayName())) { return true; }
		}

		return false;
	}

	public boolean isMan() {
		for (Category c = this; !c.isRoot(); c = c.getParent()) {
			if ("мужское".equals(c.getDisplayName())) { return true; }
		}

		return false;
	}

	public boolean isWoman() {
		for (Category c = this; !c.isRoot(); c = c.getParent()) {
			if ("женское".equals(c.getDisplayName().toLowerCase())) { return true; }
		}

		return false;
	}

	public boolean isKids() {
		for (Category c = this; !c.isRoot(); c = c.getParent()) {
			if ("детское".equals(c.getDisplayName())) { return true; }
		}

		return false;
	}

	@Override
	public String toString() {
		return "Category{" +
				"id=" + id +
				", urlName='" + urlName + '\'' +
				", displayName='" + displayName + '\'' +
				", leftOrder=" + leftOrder +
				", rightOrder=" + rightOrder;
	}

	private boolean isRoot() {
		return leftOrder == 1;
	}
}