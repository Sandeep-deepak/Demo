package com.supreme.payload.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OrderModel {

    private Long distributorId;
    private Long executiveId;
    @NotNull(message = "Outlet must not be null")
    private Long outletId;

    private LocalDateTime dateOfOrder;

    private MultipartFile orderImage;
    @NotNull(message = "Longitude must not be null")
    private String orderLongitude;
    @NotNull(message = "Latitude must not be null")
    private String orderLatitude;

    private Integer panMasalaQty;
    private Integer corianderQty;
    private Integer muttonMasalaQty;
    private Integer chickenMasalaQty;
    private Integer chilliPowderQty;
}
