package rocks.metaldetector.persistence.domain.blogpost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogpostRepository extends JpaRepository<BlogpostEntity, Long> {

}