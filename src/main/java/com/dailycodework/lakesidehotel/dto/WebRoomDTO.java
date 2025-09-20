package com.dailycodework.lakesidehotel.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Room display in web templates with base64-encoded images
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebRoomDTO {
    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked;
    private String photo; // Base64-encoded photo string

    public WebRoomDTO(Long id, String roomType, BigDecimal roomPrice) {
        this.id = id;
        this.roomType = roomType;
        this.roomPrice = roomPrice;
        this.isBooked = false;
        this.photo = null;
    }
}