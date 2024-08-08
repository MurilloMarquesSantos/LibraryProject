package library.project.domain;


import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class Book {
    Integer id;
    String name;
    Integer pubDate;
    Double price;
    Publisher publisher;

}
