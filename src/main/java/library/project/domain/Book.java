package library.project.domain;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Book {
    Integer id;
    String name;
    String pubDate;
    Double price;
    Producer producer;

}
