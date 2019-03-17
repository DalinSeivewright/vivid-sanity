package ca.logichromatic.vividsanity.repository.local;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocalImageInfoRepository extends JpaRepository<ImageInfo, UUID> {
    boolean existsByImageKey(String key);
    ImageInfo findByImageKey(String key);
}
