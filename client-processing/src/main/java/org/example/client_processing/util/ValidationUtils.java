package org.example.client_processing.util;

import org.example.client_processing.enums.client.DocumentType;
import org.example.client_processing.model.Client;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern CLIENT_ID_PATTERN = Pattern.compile("^\\d{12}$");

    private static final Pattern RU_NAME_PATTERN = Pattern.compile("^[А-Яа-яЁё \\-]{1,100}$");

    // Паспорт РФ: серия 4 цифры (prefix), номер 6 цифр (documentId)
    private static final Pattern RU_PASSPORT_SERIES = Pattern.compile("^\\d{4}$");
    private static final Pattern RU_PASSPORT_NUMBER = Pattern.compile("^\\d{6}$");

    // Загранпаспорт: номер 9 цифр, серия (опц.) до 3 латинских букв/цифр
    private static final Pattern INT_PASSPORT_NUMBER = Pattern.compile("^\\d{9}$");
    private static final Pattern INT_PASSPORT_SERIES = Pattern.compile("^[A-Za-z0-9]{1,3}$");

    // Свидетельство о рождении: серия 1-3 буквы/цифры, номер 6 цифр, регион 2 цифры (suffix)
    private static final Pattern BIRTH_CERT_SERIES = Pattern.compile("^[A-Za-zА-Яа-яЁё0-9]{1,3}$");
    private static final Pattern BIRTH_CERT_NUMBER = Pattern.compile("^\\d{6}$");
    private static final Pattern BIRTH_CERT_REGION = Pattern.compile("^\\d{2}$");

    private ValidationUtils() {}

    public static void validateClient(Client client) {
        // clientId
        if (isBlank(client.getClientId()) || !CLIENT_ID_PATTERN.matcher(client.getClientId()).matches()) {
            throw new IllegalArgumentException("clientId must be a 12-digit number (format XXFFNNNNNNNN)");
        }

        // ФИО
        if (isBlank(client.getFirstName()) || !RU_NAME_PATTERN.matcher(client.getFirstName()).matches()) {
            throw new IllegalArgumentException("Incorrect firstname");
        }
        if (!isBlank(client.getMiddleName()) && !RU_NAME_PATTERN.matcher(client.getMiddleName()).matches()) {
            throw new IllegalArgumentException("Incorrect middlename");
        }
        if (isBlank(client.getLastName()) || !RU_NAME_PATTERN.matcher(client.getLastName()).matches()) {
            throw new IllegalArgumentException("Incorrect lastname");
        }

        // Дата рождения
        if (client.getDateOfBirth() == null || !client.getDateOfBirth().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("The date of birth must be in the past");
        }
        int years = Period.between(client.getDateOfBirth(), LocalDate.now()).getYears();
        if (years < 14) {
            throw new IllegalArgumentException("The minimum age for a customer is 14 years old");
        }

        // Документы
        if (client.getDocumentType() == null) {
            throw new IllegalArgumentException("The document type is required");
        }
        validateDocument(client.getDocumentType(), client.getDocumentId(), client.getDocumentPrefix(), client.getDocumentSuffix());
    }

    private static void validateDocument(DocumentType type, String id, String prefix, String suffix) {
        switch (type) {
            case PASSPORT -> validateRuPassport(id, prefix, suffix);
            case INT_PASSPORT -> validateIntPassport(id, prefix, suffix);
            case BIRTH_CERT -> validateBirthCert(id, prefix, suffix);
            default -> throw new IllegalArgumentException("Unknown document type");
        }
    }

    private static void validateRuPassport(String id, String prefix, String suffix) {
        if (prefix == null || !RU_PASSPORT_SERIES.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Incorrect documentPrefix");
        }
        if (id == null || !RU_PASSPORT_NUMBER.matcher(id).matches()) {
            throw new IllegalArgumentException("Incorrect documentId");
        }
        if (!isBlank(suffix)) {
            throw new IllegalArgumentException("documentSuffix It is not used for a Russian passport");
        }
    }

    private static void validateIntPassport(String id, String prefix, String suffix) {
        if (id == null || !INT_PASSPORT_NUMBER.matcher(id).matches()) {
            throw new IllegalArgumentException("Incorrect documentId");
        }
        if (!isBlank(prefix) && !INT_PASSPORT_SERIES.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Passport series: up to 3 Latin letters/numbers (documentPrefix)");
        }
        if (!isBlank(suffix)) {
            throw new IllegalArgumentException("documentSuffix is not used for a foreign passport");
        }
    }

    private static void validateBirthCert(String id, String prefix, String suffix) {
        if (prefix == null || !BIRTH_CERT_SERIES.matcher(prefix).matches()) {
            throw new IllegalArgumentException("Incorrect documentPrefix");
        }
        if (id == null || !BIRTH_CERT_NUMBER.matcher(id).matches()) {
            throw new IllegalArgumentException("Incorrect documentId");
        }
        if (suffix == null || !BIRTH_CERT_REGION.matcher(suffix).matches()) {
            throw new IllegalArgumentException("Incorrect documentSuffix");
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
