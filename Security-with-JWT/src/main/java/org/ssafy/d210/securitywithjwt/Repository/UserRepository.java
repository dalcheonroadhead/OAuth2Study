package org.ssafy.d210.securitywithjwt.Repository;

import io.micrometer.observation.ObservationFilter;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.ssafy.d210.securitywithjwt.entity.User;

import java.util.List;
import java.util.stream.Stream;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"authorities"})
    Stream<UserDetails> findOneWithAuthoritiesByUsername(String username);
}
