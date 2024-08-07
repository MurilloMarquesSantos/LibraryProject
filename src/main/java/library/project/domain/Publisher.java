package library.project.domain;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Publisher {
    Integer id;
    String name;
}
