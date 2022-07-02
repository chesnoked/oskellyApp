package su.reddot.domain.dao.category;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import su.reddot.domain.model.category.PublicationPhotoSample;

import java.util.List;

public interface PublicationPhotoSampleRepository extends JpaRepository<PublicationPhotoSample, Integer> {

    List<PublicationPhotoSample> findByCategoryId(Long categoryId, Sort s);
}
