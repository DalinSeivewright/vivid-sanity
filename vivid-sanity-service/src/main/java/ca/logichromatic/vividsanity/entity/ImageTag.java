package ca.logichromatic.vividsanity.entity;


import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "IMAGE_INFO")
@Data
@EqualsAndHashCode(of = "identifier")
@Accessors(chain=true)
public class ImageTag {
    @Column(name = "IMAGE_TAG_ID")
    @Setter(AccessLevel.NONE)
    @Id
    private UUID identifier;

    @Column(name="IMAGE_ID")
    private UUID imageId;

    @Column(name="TAG")
    private String tag;

    @Column(name="DESCRIPTION")
    private String description;
}
