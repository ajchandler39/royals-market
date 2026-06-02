package com.royalsmarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferForm {

    @Min(1)
    private long amount;

    @Size(max = 500)
    private String message;
}
