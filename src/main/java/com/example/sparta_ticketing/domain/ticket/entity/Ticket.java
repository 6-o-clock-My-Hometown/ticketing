package com.example.sparta_ticketing.domain.ticket.entity;

import com.example.sparta_ticketing.common.entity.BaseEntity;
import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.ticket.enums.TicketStatus;
import com.example.sparta_ticketing.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "tickets")
@NoArgsConstructor
public class Ticket extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket(User user, Seat seat, Show show, TicketStatus status) {
        this.user = user;
        this.seat = seat;
        this.show = show;
        this.status = status;
    }

    public void cancelTicket(){
        this.status = TicketStatus.CANCELED;
    }
}
