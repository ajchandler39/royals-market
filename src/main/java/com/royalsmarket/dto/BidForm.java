package com.royalsmarket.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BidForm {

    @Min(1)
    private long amount;
}
