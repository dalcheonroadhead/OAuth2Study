package spring_with_jwt2.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import spring_with_jwt2.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity,Integer> {

    // 1. By 뒤의 컬럼에 매개변수 값이 있는지 없는지 확인이 가능한 JPA 명령문
    Boolean existsByUsername(String username);


}