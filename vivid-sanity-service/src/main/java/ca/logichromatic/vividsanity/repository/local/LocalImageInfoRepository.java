package ca.logichromatic.vividsanity.repository.local;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.entity.ImageTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocalImageInfoRepository extends JpaRepository<ImageInfo, UUID> {
    boolean existsByImageKey(String key);
    ImageInfo findByImageKey(String key);


    @Query("select DISTINCT imageTag.tag from ImageTag imageTag")
    List<String> findAllDistinctTags();
}
