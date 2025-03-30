package com.example.sparta_ticketing.domain.seat.entity;

import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.show.entity.Show;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seats")
public class Seat{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "show_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Show show;

    @Enumerated(EnumType.STRING)
    private SeatEnum name;

    private int count;

    private int price;

    private int remainSeatCount;

    public Seat(Show show, SeatEnum name, int count, int price) {
        this.show = show;
        this.name = name;
        this.count = count;
        this.price = price;
        this.remainSeatCount = count;
    }

    public void updateSeat(SeatEnum name, int count, int price){
        this.name = name;
        this.count = count;
        this.price = price;
    }

    public void updateRemainSeat(int remainSeatCount){
        this.remainSeatCount = remainSeatCount;
    }

}
