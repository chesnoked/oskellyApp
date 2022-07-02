package su.reddot.presentation.api.v1.admin;

import lombok.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import su.reddot.domain.model.category.Category;
import su.reddot.domain.service.catalog.CategoryService;

@RestController
@RequestMapping("/admin/api/v1/categories")
@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).ADMIN)")
@RequiredArgsConstructor
public class CategoryManagementApi {

    private final CategoryService categoryService;

    @PostMapping
    public CreatedCategory create(@RequestBody NewCategory newCategory) {
        Category category = categoryService.create(newCategory.parentId, newCategory.displayName, newCategory.urlName, newCategory.singularName);

        return new CreatedCategory(category.getId());
    }

    @Getter @Setter
    private static class NewCategory {

        private Long   parentId;
        private String displayName;
        private String urlName;
        private String singularName;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    private class CreatedCategory {
        private Long id;
    }
}
