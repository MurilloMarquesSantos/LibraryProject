package library.project.service;

import library.project.domain.Book;
import library.project.domain.Publisher;
import library.project.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class BookService {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void menu(int op) {
        switch (op) {
            case 1 -> addBook();
            case 2 -> addBookTransaction();
            case 3 -> showBooks();
            case 4 -> showBookByName();
            case 5 -> updateBook();
            case 6 -> deleteBook();
            default -> throw new IllegalArgumentException("Invalid option");
        }

    }

    public static void addBook() {
        System.out.println("Type the book name: ");
        String bookName = SCANNER.nextLine();
        System.out.println("Type the publication date: ");
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

    public static void addBookTransaction() {
        List<Book> bookList = new ArrayList<>();
        while (true) {
            System.out.println("Type the book name: ");
            String bookName = SCANNER.nextLine();
            System.out.println("Type the publication date: ");
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
            bookList.add(book);
            System.out.println("You want to add more? Y/N");
            String choice = SCANNER.nextLine();
            if (choice.equalsIgnoreCase("N")) break;
        }
        BookRepository.InsertBookTransaction(bookList);
    }

    public static void showBooks() {
        BookRepository.showBooks()
                .forEach(b -> System.out.printf("[%d] - '%s' - %d - $%s - %s - [%d]%n", b.getId(), b.getName(),
                        b.getPubDate(), formatPrice(b), b.getPublisher().getName(), b.getPublisher().getId()));

    }

    public static void showBookByName() {
        System.out.println("Type the book name you want to search for: ");
        String name = SCANNER.nextLine();
        List<Book> bookList = BookRepository.showBookByName(name);
        if (!bookList.isEmpty()) {
            bookList.forEach(b -> System.out.printf("[%d] - '%s' - %d - $%s - %s - [%d]%n", b.getId(), b.getName(),
                    b.getPubDate(), formatPrice(b), b.getPublisher().getName(), b.getPublisher().getId()));
        }
        if (bookList.isEmpty()) {
            System.out.printf("No book found with this name: '%s'%n", name);

        }

    }

    public static void updateBook() {
        System.out.println("Books: ");
        showBooks();
        System.out.println("Type the id of the book you want to update: ");
        Optional<Book> bookOptional = BookRepository.findBookById(Integer.parseInt(SCANNER.nextLine()));
        if (bookOptional.isEmpty()) {
            System.out.println("Book not found");
            return;
        }
        Book bookDB = bookOptional.get();
        System.out.println("Book found: " + bookDB);
        System.out.println("Type the new name or enter to keep the same: ");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? bookDB.getName() : name;

        Integer pubDate = bookDB.getPubDate();
        System.out.println("Type the new pubDate or enter do keep the same: ");
        String dateToUpdate = SCANNER.nextLine();
        if (!dateToUpdate.isEmpty()) {
            pubDate = Integer.parseInt(dateToUpdate);
        }

        Double price = bookDB.getPrice();
        System.out.println("Type the new price or enter to keep the same: ");
        String priceToUpdate = SCANNER.nextLine();
        if (!priceToUpdate.isEmpty()) {
            String priceToUpdateFormatted = priceToUpdate.replaceAll(",", ".");
            price = Double.parseDouble(priceToUpdateFormatted);
        }

        Integer id = bookDB.getPublisher().getId();
        System.out.println("Available publishers: ");
        PublisherService.showPublishers();
        System.out.println("Type the new publisher id or enter to keep the same: ");
        String publisherIdToUpdate = SCANNER.nextLine();
        if (!publisherIdToUpdate.isEmpty()) {
            id = Integer.parseInt(publisherIdToUpdate);
        }

        Publisher publisher = Publisher.builder()
                .id(id)
                .build();

        Book book = Book.builder()
                .id(bookDB.getId())
                .name(name)
                .pubDate(pubDate)
                .price(price)
                .publisher(publisher)
                .build();

        BookRepository.updateBook(book);


    }

    public static void deleteBook() {
        System.out.println("Available books: ");
        showBooks();
        System.out.println("Type the book Id to delete: ");
        int id = Integer.parseInt(SCANNER.nextLine());
        Optional<Book> bookOptional = BookRepository.findBookById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            System.out.printf("Are you sure to delete this book? '%s' Y/N%n", book.getName());
            String op = SCANNER.nextLine();
            if (op.equalsIgnoreCase("Y")) {
                BookRepository.deleteBook(id);
            }
        }

    }

    private static String formatPrice(Book b) {
        return b.getPrice().toString().replace(",", ".");
    }

}
