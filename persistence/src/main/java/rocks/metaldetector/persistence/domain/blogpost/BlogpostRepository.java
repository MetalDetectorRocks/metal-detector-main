package rocks.metaldetector.persistence.domain.blogpost;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogpostRepository extends JpaRepository<BlogpostEntity, Long> {
}
