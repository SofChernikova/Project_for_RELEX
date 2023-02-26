package vsu.ru.market.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import vsu.ru.market.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {
}
