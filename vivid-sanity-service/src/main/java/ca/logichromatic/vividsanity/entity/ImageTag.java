package ca.logichromatic.vividsanity.entity;


import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Type;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "IMAGE_TAG")
@Data
@EqualsAndHashCode(of = "identifier")
@Accessors(chain=true)
@EntityListeners(AuditingEntityListener.class)
public class ImageTag {
    @Id
    @Column(name = "IMAGE_TAG_ID")
    @Type(type="uuid-char")
    @Setter(AccessLevel.NONE)
    private UUID identifier;

    @Column(name="IMAGE_ID")
    @Type(type="uuid-char")
    @Setter(AccessLevel.NONE)
    private UUID imageId;

    @Column(name="TAG")
    private String tag;

    @Column(name="DESCRIPTION")
    private String description;

    @CreatedDate
    @Column(name="CREATED_DATE")
    private Date createdDate;

    @LastModifiedDate
    @Column(name="UPDATED_DATE")
    private Date updatedDate;

    private ImageTag() { }

    public static ImageTag newInstance(UUID imageInfoId, String tagName) {
        ImageTag imageTag = new ImageTag();
        imageTag.identifier = UUID.randomUUID();
        imageTag.imageId = imageInfoId;
        imageTag.setTag(StringUtils.trim(tagName));
        return imageTag;
    }
}
