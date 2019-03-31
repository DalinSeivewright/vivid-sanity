package ca.logichromatic.vividsanity.repository.local;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import ca.logichromatic.vividsanity.entity.ImageTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LocalImageInfoRepository extends JpaRepository<ImageInfo, UUID> {

    boolean existsByImageKey(String key);
    ImageInfo findByImageKey(String key);


    @Query("select DISTINCT imageTag.tag from ImageTag imageTag")
    List<String> findAllDistinctTags();

    @Query(value = "SELECT similarImage.*  " +
            "FROM IMAGE_INFO similarImage  " +
            "WHERE EXISTS (  " +
            "  SELECT 1 FROM IMAGE_INFO specificImage  " +
            "  JOIN IMAGE_TAG specificImageTag on specificImageTag.IMAGE_ID = specificImage.IMAGE_ID  " +
            "  WHERE specificImage.IMAGE_KEY = (:imageKey) AND  " +
            "  EXISTS (SELECT 1 FROM IMAGE_TAG similarImageTag WHERE similarImageTag.IMAGE_ID = similarImage.IMAGE_ID AND specificImageTag.tag = similarImageTag.tag)  " +
            "  )  " +
            "  AND similarImage.IMAGE_KEY <> (:imageKey)  ", nativeQuery = true)
    List<ImageInfo> findSimilarImages(@Param(value="imageKey") String imageKey);
}
