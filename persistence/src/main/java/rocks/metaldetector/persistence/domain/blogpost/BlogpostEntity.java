package rocks.metaldetector.persistence.domain.blogpost;

import lombok.*;

import rocks.metaldetector.persistence.domain.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // for hibernate and model mapper
@Entity(name = "blogposts")
public class BlogpostEntity extends BaseEntity {

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Column(columnDefinition="TEXT", name = "text", nullable = false)
    private String text;

    @Column(name = "draft", nullable = false)
    private boolean draft;

    @Builder
    public BlogpostEntity(String title, String slug, String text,
                          boolean draft) {
        this.title = title;
        this.slug = slug;
        this.text = text;
        this.draft = draft;
    }
}
