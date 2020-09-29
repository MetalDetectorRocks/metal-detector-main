package rocks.metaldetector.service.blogpost;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rocks.metaldetector.persistence.domain.blogpost.BlogpostEntity;
import rocks.metaldetector.persistence.domain.blogpost.BlogpostRepository;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlogpostServiceTest implements WithAssertions {

    @Mock
    private BlogpostRepository blogpostRepository;

    @InjectMocks
    private BlogpostServiceImpl blogpostService;

    @AfterEach
    void tearDown() {
        reset(blogpostRepository);
    }

    @DisplayName("Get blogpost tests")
    @Nested
    class GetBlogpostTest {

        @Test
        @DisplayName("Should return all blogposts")
        void get_all_blogposts() {
            // given
            BlogpostEntity post1 = BlogpostEntityFactory.createBlogpost("abcd", "a-b-c-d", "aaa bbb ccc ddd", true);
            BlogpostEntity post2 = BlogpostEntityFactory.createBlogpost("efgh", "e-f-g-h", "eee fff ggg hhh", true);
            when(blogpostRepository.findAll()).thenReturn(List.of(post1, post2));

            // when
            List<BlogpostEntity> blogpostList = blogpostService.getAllBlogposts();

            // then
            assertThat(blogpostList).hasSize(2);
            assertThat(blogpostList.get(0)).isEqualTo(post1);
            assertThat(blogpostList.get(1)).isEqualTo(post2);
        }

        @Test
        @DisplayName("Should use BlogpostRepository to return a list of all blogposts")
        void get_all_blogposts_uses_blogpost_repository() {
            // given
            when(blogpostRepository.findAll()).thenReturn(Collections.emptyList());

            // when
            blogpostService.getAllBlogposts();

            // then
            verify(blogpostRepository, times(1)).findAll();
        }

    }
}
