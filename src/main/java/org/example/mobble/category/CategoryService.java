package org.example.mobble.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public List<String> getPopularList(Integer count) {
        return categoryRepository.getPopularList(count);
    }
}
