package com.example.sparta_ticketing.domain.ticket.dto.response;

import com.example.sparta_ticketing.domain.seat.entity.Seat;
import com.example.sparta_ticketing.domain.seat.enums.SeatEnum;
import com.example.sparta_ticketing.domain.show.entity.Show;
import com.example.sparta_ticketing.domain.show.enums.Category;
import com.example.sparta_ticketing.domain.show.enums.Region;
import com.example.sparta_ticketing.domain.ticket.entity.Ticket;
import com.example.sparta_ticketing.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private Long userId;
    private String nickname;
    private Long showId;
    private String showTitle;
    private Category showCategory;
    private Region showRegion;
    private Long seatId;
    private SeatEnum seatName;
    private Integer price;

    public static TicketResponse toDto(Ticket ticket){
        return new TicketResponse(ticket.getId(),
                ticket.getUser().getId(),
                ticket.getUser().getNickname(),
                ticket.getShow().getId(),
                ticket.getShow().getTitle(),
                ticket.getShow().getCategory(),
                ticket.getShow().getRegion(),
                ticket.getSeat().getId(),
                ticket.getSeat().getName(),
                ticket.getSeat().getPrice()
                );
    }
}
