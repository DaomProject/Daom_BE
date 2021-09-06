package com.daom;

import com.daom.domain.Category;
import com.daom.domain.Univ;
import com.daom.repository.CategoryRepository;
import com.daom.repository.UnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class InitDb {

    private final UnivRepository univRepository;
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void univInit() {
        univRepository.save(new Univ("전북대학교"));
        categoryRepository.save(new Category("식당"));
    }
}
