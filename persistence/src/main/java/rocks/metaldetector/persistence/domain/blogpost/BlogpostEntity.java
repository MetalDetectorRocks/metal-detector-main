package rocks.metaldetector.persistence.domain.blogpost;

import lombok.*;
import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE) // for hibernate and model mapper
@Entity(name = "blogposts")
public class BlogpostEntity extends BaseEntity {

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "slug", nullable = false, length = 120, unique = true)
    private String slug;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "draft_flag")
    private boolean draftFlag;

    @Builder
    public BlogpostEntity(@NonNull String title, @NonNull String slug, @NonNull String text,
                          boolean draftFlag) {
        this.title = title;
        this.slug = slug;
        this.text = text;
        this.draftFlag = draftFlag;
    }
}
