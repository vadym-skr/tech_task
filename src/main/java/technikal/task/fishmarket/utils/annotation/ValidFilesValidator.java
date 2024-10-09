package technikal.task.fishmarket.utils.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class ValidFilesValidator implements ConstraintValidator<ValidFiles, List<MultipartFile>> {

    private long maxSize;
    private String[] allowedTypes;

    @Override
    public void initialize(ValidFiles constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = constraintAnnotation.allowedTypes();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Потрібно добавити хоча б одне фото")
                    .addConstraintViolation();
            return false;
        }

        boolean allFilesValid = true;

        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Потрібно добавити фото")
                        .addConstraintViolation();
                allFilesValid = false;
                continue;
            }

            if (file.getSize() > maxSize * 1024) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.format("Файл '%s' не повинен перевищувати %s KB",
                                file.getOriginalFilename(), maxSize))
                        .addConstraintViolation();
                allFilesValid = false;
                continue;
            }

            String contentType = file.getContentType();
            boolean validType = false;
            for (String allowedType : allowedTypes) {
                if (allowedType.equals(contentType)) {
                    validType = true;
                    break;
                }
            }

            if (!validType) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Дозволені типи файлів: " + String.join(", ", allowedTypes))
                        .addConstraintViolation();
                allFilesValid = false;
            }
        }

        return allFilesValid;
    }
}
