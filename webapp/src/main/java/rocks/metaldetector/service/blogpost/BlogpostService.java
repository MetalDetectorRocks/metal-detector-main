package rocks.metaldetector.service.blogpost;

import rocks.metaldetector.persistence.domain.blogpost.BlogpostEntity;

import java.util.List;

public interface BlogpostService {

    List<BlogpostEntity> getAllBlogposts();
}
