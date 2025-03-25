package com.example.sparta_ticketing.domain.seat.entity;

import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "seats")
public class Seat{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long showId;

    @Enumerated(EnumType.STRING)
    private SeatEnum name;

    private int count;

    private int price;

    public  Seat(Long showId, SeatEnum name, int count, int price) {
        this.showId = showId;
        this.name = name;
        this.count = count;
        this.price = price;
    }

}
