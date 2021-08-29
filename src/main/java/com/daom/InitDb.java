package com.daom;

import com.daom.domain.Univ;
import com.daom.repository.UnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@RequiredArgsConstructor
@Component
public class InitDb {

    private final UnivRepository univRepository;

    @PostConstruct
    public void univInit(){
        univRepository.save(new Univ("전북대학교"));
    }
}
