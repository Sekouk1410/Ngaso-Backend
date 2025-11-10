package com.ngaso.Ngaso.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModeleEtapeCreateRequest {
    private String nom;
    private String description;
    private Integer ordre;
    private Integer specialiteId;
}
