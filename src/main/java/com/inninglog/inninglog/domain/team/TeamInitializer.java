package com.inninglog.inninglog.domain.team;

import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class TeamInitializer implements ApplicationRunner {

    private final TeamRepository teamRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (teamRepository.count() == 0) {
            teamRepository.saveAll(List.of(
                    Team.builder().name("KIA").shortCode("HT").build(),
                    Team.builder().name("삼성").shortCode("SS").build(),
                    Team.builder().name("LG").shortCode("LG").build(),
                    Team.builder().name("두산").shortCode("OB").build(),
                    Team.builder().name("KT").shortCode("KT").build(),
                    Team.builder().name("SSG").shortCode("SK").build(),
                    Team.builder().name("롯데").shortCode("LT").build(),
                    Team.builder().name("한화").shortCode("HH").build(),
                    Team.builder().name("NC").shortCode("NC").build(),
                    Team.builder().name("키움").shortCode("WO").build(),
                    Team.builder().name("전체").shortCode("ALL").build()
            ));
        }
    }
}