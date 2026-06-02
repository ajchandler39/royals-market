package com.royalsmarket.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileForm {

    @Size(max = 30)
    private String ign;

    @Size(max = 50)
    private String discordTag;
}
