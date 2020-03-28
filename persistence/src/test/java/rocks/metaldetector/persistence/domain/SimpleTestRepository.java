package rocks.metaldetector.persistence.domain;

import org.springframework.data.repository.CrudRepository;

interface SimpleTestRepository extends CrudRepository<SimpleTestEntity, Long> {
}
