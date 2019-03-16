package ca.logichromatic.vividsanity.repository.external;

import ca.logichromatic.vividsanity.entity.ImageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ExternalImageInfoRepository extends JpaRepository<ImageInfo, UUID> {
}
