package ca.logichromatic.vividsanity.entity;

import ca.logichromatic.vividsanity.model.VisibilityStatus;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "IMAGE_INFO")
@Data
@EqualsAndHashCode(of = "identifier")
@Accessors(chain=true)
public class ImageInfo {
    @Id
    @Column(name = "IMAGE_ID")
    @Setter(AccessLevel.NONE)
    @Type(type="uuid-char")
    private UUID identifier;

    @Column(name = "IMAGE_KEY")
    private String imageKey;

    @Column(name="DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "imageId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ImageTag> tags;

    @Column(name="VISIBILITY")
    private VisibilityStatus visibility;

    private ImageInfo() {
    }

    public static ImageInfo newInstance(String imageKey) {
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.identifier = UUID.randomUUID();
        imageInfo.setImageKey(imageKey);
        imageInfo.tags = new ArrayList<>();
        return imageInfo;
    }

    // TODO Maybe not do this?
    public ImageInfo setTags(List<ImageTag> newTags) {
        this.getTags().clear();
        tags.addAll(newTags);
        return this;
    }
}
