package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IllustrationResponse {
    private Integer id;
    private String titre;
    private String description;
    private String urlImage;
    private Integer modeleId;
}
