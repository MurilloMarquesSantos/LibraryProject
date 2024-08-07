package library.project.service;

import library.project.domain.Publisher;
import library.project.repository.PublisherRepository;

import java.util.Optional;
import java.util.Scanner;

public class PublisherService {
    private static final Scanner SCANNER = new Scanner(System.in);

    public static void menu(int op) {
        switch (op) {
            case 1 -> addPublisher();
            case 2 -> showPublishers();
            case 3 -> updatePublisher();
            case 4 -> deletePublisher();
            default -> throw new IllegalArgumentException("Not a valid option");
        }
    }

    public static void addPublisher() {
        System.out.println("Insert the publisher to add: ");
        String publisherName = SCANNER.nextLine();
        Publisher publisher = Publisher.builder().name(publisherName).build();
        PublisherRepository.insertPublisher(publisher);
    }

    public static void showPublishers() {
        PublisherRepository.showPublishers()
                .forEach(p -> System.out.printf("[%d] - [%s]%n", p.getId(), p.getName()));
    }

    public static void deletePublisher() {
        System.out.println("Publishers: ");
        showPublishers();
        System.out.println("Type the id of publisher you want to delete: ");
        int id = Integer.parseInt(SCANNER.nextLine());
        System.out.println("Are you sure? Y/N ");
        String op = SCANNER.nextLine();
        if (op.equalsIgnoreCase("y")) {
            PublisherRepository.deletePublisher(id);
        }
    }

    public static void updatePublisher() {
        System.out.println("Publishers: ");
        showPublishers();
        System.out.println("Type the id of the publisher you want to update: ");
        Optional<Publisher> publisherOptional = PublisherRepository.findById(Integer.parseInt(SCANNER.nextLine()));
        if (publisherOptional.isEmpty()) {
            System.out.println("Id not found");
            return;
        }
        Publisher publisherDb = publisherOptional.get();
        System.out.println("Publisher found: " + publisherDb);
        System.out.println("Type the new publisher name or empty to continue the same:");
        String name = SCANNER.nextLine();
        name = name.isEmpty() ? publisherDb.getName() : name;

        Publisher produceToUpdate = Publisher.builder().id(publisherDb.getId())
                .name(name)
                .build();

        PublisherRepository.updatePublisher(produceToUpdate);
    }

}
