package rocks.metaldetector.service.blogpost;

import rocks.metaldetector.persistence.domain.blogpost.BlogpostEntity;

public class BlogpostEntityFactory {

    public static BlogpostEntity createBlogpost(String title, String slug, String text, boolean draftFlag) {
        return BlogpostEntity.builder()
                .title(title)
                .slug(slug)
                .text(text)
                .draftFlag(draftFlag)
                .build();
    }
}
