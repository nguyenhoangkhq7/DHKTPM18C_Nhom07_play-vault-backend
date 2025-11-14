package fit.iuh.dtos;

import lombok.Data;

@Data
public class GameSummaryDto {
    private Long id;   // id của GameBasicInfo hoặc Game tùy bạn muốn expose
    private String name;
    private String thumbnail;
}