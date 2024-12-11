
package tech.artadevs.finances.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import tech.artadevs.finances.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

	Optional<User> findByAccountNumber(Long accountNumber);
}
