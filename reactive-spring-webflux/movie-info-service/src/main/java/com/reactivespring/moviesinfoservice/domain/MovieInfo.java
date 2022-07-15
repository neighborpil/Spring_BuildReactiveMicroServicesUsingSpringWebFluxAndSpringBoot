package com.reactivespring.moviesinfoservice.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class MovieInfo {

    @Id
    private String movieInfoId;
    @NotBlank(message = "movieInfo.name must be present")
    private String name;
    @NotNull
    @Positive(message = "movieinfo.year must be a Positive value")
    private Integer year;
    private List<@NotBlank(message = "movieinfo.cast must be present") String> cast;
    private LocalDate release_date;

}
