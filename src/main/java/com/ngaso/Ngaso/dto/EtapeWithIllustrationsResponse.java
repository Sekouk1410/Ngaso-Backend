package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EtapeWithIllustrationsResponse {
    private Integer etapeId;
    private Integer modeleId;
    private String modeleNom;
    private String modeleDescription;
    private Integer ordre;
    private Boolean estValider;
    private List<IllustrationResponse> illustrations;
}
