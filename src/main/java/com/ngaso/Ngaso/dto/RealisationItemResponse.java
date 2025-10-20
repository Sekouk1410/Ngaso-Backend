package com.ngaso.Ngaso.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RealisationItemResponse {
    private String id;   // identifiant dérivé (ex: nom de fichier)
    private String url;  // chemin/URL complet stocké
}
