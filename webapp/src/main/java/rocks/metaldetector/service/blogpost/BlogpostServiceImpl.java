package rocks.metaldetector.service.blogpost;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rocks.metaldetector.persistence.domain.blogpost.BlogpostEntity;
import rocks.metaldetector.persistence.domain.blogpost.BlogpostRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class BlogpostServiceImpl implements BlogpostService {

    private final BlogpostRepository blogpostRepository;

    @Override
    public List<BlogpostEntity> getAllBlogposts() {
        return blogpostRepository.findAll();
    }
}