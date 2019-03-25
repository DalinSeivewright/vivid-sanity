package ca.logichromatic.vividsanity.entity;

import ca.logichromatic.vividsanity.model.VisiblityType;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "IMAGE_INFO")
@Data
@EqualsAndHashCode(of = "identifier")
@Accessors(chain=true)
@EntityListeners(AuditingEntityListener.class)
public class ImageInfo {
    @Id
    @Column(name = "IMAGE_ID")
    @Setter(AccessLevel.NONE)
    @Type(type="uuid-char")
    private UUID identifier;

    @Column(name = "IMAGE_KEY")
    private String imageKey;

    @Column(name="TITLE")
    private String title;

    @Column(name="DESCRIPTION")
    private String description;

    @OneToMany(mappedBy = "imageId", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    private List<ImageTag> tags;

    @Column(name="VISIBILITY")
    private VisiblityType visibility;

    @Column(name="PALETTE")
    private String palette;


    @CreatedDate
    @Column(name="CREATED_DATE", nullable = false, updatable = false)
    @Setter(value = AccessLevel.NONE)
    private Date createdDate;

    @LastModifiedDate
    @Column(name="UPDATED_DATE", nullable = false)
    @Setter(value = AccessLevel.NONE)
    private Date updatedDate;

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
