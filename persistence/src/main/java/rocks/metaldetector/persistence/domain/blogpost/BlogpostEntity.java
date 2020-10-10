package rocks.metaldetector.persistence.domain.blogpost;

import lombok.*;

import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@Entity(name = "blogposts")
public class BlogpostEntity extends BaseEntity {

    @Column(name = "title", nullable = false, unique = true)
    @Size(min = 2, max = 255)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    @Size(min = 2, max = 255)
    private String slug;

    @Column(columnDefinition="TEXT", name = "text", nullable = false)
    private String text;

    @Column(name = "draft_flag", nullable = false)
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
