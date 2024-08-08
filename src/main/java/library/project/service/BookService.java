package library.project.service;

import library.project.domain.Book;
import library.project.domain.Publisher;
import library.project.repository.BookRepository;

import java.util.Scanner;

public class BookService {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void menu(int op) {
        switch (op) {
            case 1 -> addBook();
            case 2 -> showBooks();
            default -> throw new IllegalArgumentException("Invalid option");
        }

    }


    public static void addBook() {
        System.out.println("Type the book name: ");
        String bookName = SCANNER.nextLine();
        System.out.println("Type the publication year: ");
        int year = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Type the book price: ");
        String priceString = SCANNER.nextLine();
        String replacedPrice = priceString.replace(",", ".");
        double price = Double.parseDouble(replacedPrice);
        System.out.println("Type the publisher id: ");
        PublisherService.showPublishers();
        int producerId = Integer.parseInt(SCANNER.nextLine());
        Book book = Book.builder().name(bookName)
                .pubDate(year)
                .price(price)
                .publisher(Publisher.builder().id(producerId).build())
                .build();
        BookRepository.InsertBook(book);
    }

    public static void showBooks() {
        BookRepository.showBooks()
                .forEach(b -> System.out.printf("[%d] - '%s' - %d - $%S - %s - [%d]%n", b.getId(), b.getName(),
                        b.getPubDate(), b.getPrice().toString().replace(",", "."), b.getPublisher().getName(), b.getPublisher().getId()));

    }
}
