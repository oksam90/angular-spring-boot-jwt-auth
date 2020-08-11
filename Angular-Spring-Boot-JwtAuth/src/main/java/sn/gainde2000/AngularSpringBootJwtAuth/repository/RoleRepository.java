package sn.gainde2000.AngularSpringBootJwtAuth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sn.gainde2000.AngularSpringBootJwtAuth.models.ERole;
import sn.gainde2000.AngularSpringBootJwtAuth.models.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(ERole name);
}
