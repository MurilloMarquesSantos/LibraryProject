package library.project.test;

import library.project.service.BookService;
import library.project.service.PublisherService;

import java.util.Scanner;

public class Test {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void main(String[] args) {
        int op;
        while (true) {
            menu();
            op = Integer.parseInt(SCANNER.nextLine());
            if (op == 0) break;
            switch (op) {
                case 1 -> {
                    publisherMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    if (op == 9) continue;
                    PublisherService.menu(op);
                }
                case 2 -> {
                    bookMenu();
                    op = Integer.parseInt(SCANNER.nextLine());
                    if (op == 9) continue;
                    BookService.menu(op);
                }


            }
        }
    }

    private static void menu() {
        System.out.println("Type the number of your operation: ");
        System.out.println("1. Publisher");
        System.out.println("2. Book");
        System.out.println("0. Exit");
    }

    private static void publisherMenu() {
        System.out.println("1. Add publisher");
        System.out.println("2. Add more than one publisher");
        System.out.println("3. Show all publishers");
        System.out.println("4. Update publisher");
        System.out.println("5. Delete publisher");
        System.out.println("9. Go back");
    }

    private static void bookMenu() {
        System.out.println("1. Add book");
        System.out.println("2. Add more than one book");
        System.out.println("3. Show all books");
        System.out.println("4. Show specific book");
        System.out.println("5. Update book");
        System.out.println("6. Delete book");
        System.out.println("9. Go back");
    }

}
