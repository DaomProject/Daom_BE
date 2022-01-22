package com.daom.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class StudentVisitShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_visit_shop_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @Column(nullable = false, name = "visit_date")
    private LocalDateTime visitDate;

    @Builder
    public StudentVisitShop(Student student, Shop shop, LocalDateTime visitDate) {
        this.student = student;
        this.shop = shop;
        this.visitDate = visitDate;
    }
}
