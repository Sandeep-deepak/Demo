package com.supreme.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class OutletResponse {
    private Long outletId;
    private String outletName;
    private String mobileNumber;
    private String address;
    private String outletImgName;
    private String outletImgUrl;
}
