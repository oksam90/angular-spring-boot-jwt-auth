package sn.gainde2000.AngularSpringBootJwtAuth.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import sn.gainde2000.AngularSpringBootJwtAuth.models.Tutorial;

public interface TutorialRepository extends JpaRepository<Tutorial, Long> {
	
	Page<Tutorial> findByPublished(boolean published, Pageable pageable);
	Page<Tutorial> findByTitleContaining(String title, Pageable pageable);
	List<Tutorial> findByTitleContaining(String title, Sort sort);

}
