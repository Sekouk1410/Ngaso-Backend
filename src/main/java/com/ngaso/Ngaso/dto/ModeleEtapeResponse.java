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
public class ModeleEtapeResponse {
    private Integer id;
    private String nom;
    private String description;
    private Integer ordre;
    private List<Integer> specialiteIds;
    private List<String> specialiteLibelles;
    private Long nombreIllustrations;
}
