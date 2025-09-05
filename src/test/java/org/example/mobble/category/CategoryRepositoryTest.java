package org.example.mobble.category;

import jakarta.persistence.EntityManager;
import org.example.mobble.category.domain.Category;
import org.example.mobble.category.domain.CategoryRepository;
import org.example.mobble.user.domain.User;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({CategoryRepository.class})
public class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    EntityManager em;

    private User newUser(String uname) {
        User u = User.builder()
                .username(uname)
                .password("1234")
                .email(uname + "@test.com")
                .build();
        em.persist(u);      // ManyToOne에는 보통 cascade 안 걸림 → 직접 저장
        return u;
    }

    // 저장 후 (PK 발급 확인) userId+category 조건으로 단건 조회가 되는지 검증
    @Test
    void save_and_findByUserIdAndCategory() {
        // given
        User user = newUser("user2");
        Category c = Category.builder()
                .user(user)             // 기존 .userId(1) → .user(user)
                .category("Dev")
                .build();

        // when
        categoryRepository.save(c);
        em.flush(); // DB 반영

        // then
        assertThat(c.getId()).isNotNull();
        assertThat(categoryRepository.findByUserIdAndCategory(1, "Dev")).isPresent();
    }

    // 특정 유저가 동일 이름의 카테고리를 가지고 있는지 존재여부 체크 검증
    @Test
    void existsByUserIdAndCategory() {
        // given
        User user = newUser("user2");
        categoryRepository.save(Category.builder().user(user).category("Dev").build());
        em.flush();

        // then
        assertThat(categoryRepository.existsByUserIdAndCategory(1, "Dev")).isTrue();
        assertThat(categoryRepository.existsByUserIdAndCategory(1, "Ops")).isFalse();
    }

    // (userId, category) 유니크 제약이 실제로 동작해 예외를 발생시키는지 검증
    @Test
    void uniqueConstraint_user_category_enforced() {
        // given
        User user = newUser("user2");
        categoryRepository.save(Category.builder().user(user).category("Dev").build());

        // when & then
        assertThatThrownBy(() -> {
            categoryRepository.save(Category.builder().user(user).category("Dev").build());
            em.flush(); // flush 시점에 제약 위반 가능
        }).isInstanceOfAny(ConstraintViolationException.class,
                org.springframework.dao.DataIntegrityViolationException.class);
    }


    // PK(id)로 단건 조회가 정상 동작하는지 검증 (존재 케이스)
    @Test
    void findById_returnsCategory_whenExists() {
        // given
        User user = newUser("user2");
        Category c = Category.builder().user(user).category("Dev").build();
        categoryRepository.save(c);
        em.flush(); em.clear();

        // when
        Optional<Category> found = categoryRepository.findById(c.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCategory()).isEqualTo("Dev");
    }

    // 특정 유저의 카테고리 목록이 id 기준 내림차순으로 반환되는지 검증
    @Test
    void findAllByUserIdOrderByIdDesc_returnsCategoriesSorted() {
        // given
        User user = newUser("user2");
        categoryRepository.save(Category.builder().user(user).category("A").build());
        categoryRepository.save(Category.builder().user(user).category("B").build());
        em.flush(); em.clear();

        // when
        List<Category> categories = categoryRepository.findAllByUserIdOrderByIdDesc(2);

        // then
        assertThat(categories).hasSize(2);
        assertThat(categories.get(0).getCategory()).isEqualTo("B"); // desc 정렬 확인
    }

    // 존재여부 체크 메서드가 true를 반환하는지 재확인 (단일 케이스)
    @Test
    void existsByUserIdAndCategory_returnsTrue_whenDuplicateExists() {
        // given
        User user = newUser("user2");
        Category c = Category.builder().user(user).category("Dev").build();
        categoryRepository.save(c);
        em.flush(); em.clear();

        // when
        boolean exists = categoryRepository.existsByUserIdAndCategory(1, "Dev");

        // then
        assertThat(exists).isTrue();
    }

    // PK로 삭제가 정상 동작하는지 검증 (삭제 후 findById가 empty여야 함)
    @Test
    void deleteById_removesCategory() {
        // given
        User user = newUser("user2");
        Category c = Category.builder().user(user).category("Dev").build();
        categoryRepository.save(c);
        em.flush(); em.clear();

        // when
        categoryRepository.deleteById(c.getId());
        em.flush(); em.clear();

        // then
        assertThat(categoryRepository.findById(c.getId())).isEmpty();
    }

}
