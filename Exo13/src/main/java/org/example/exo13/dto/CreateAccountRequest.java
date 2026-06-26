package org.example.exo13.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateAccountRequest {
    @NotBlank
    private String holder;

    public String getHolder() { return holder; }
    public void setHolder(String holder) { this.holder = holder; }
}
