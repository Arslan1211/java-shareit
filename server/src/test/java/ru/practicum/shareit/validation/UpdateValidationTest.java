package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateValidationTest {

    private final Validator validator;

    public UpdateValidationTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    // Тестовый класс для проверки валидации
    private static class TestUpdateClass implements UpdateValidation {
        @Null(groups = UpdateValidation.class)
        private Long id;

        @NotBlank(groups = UpdateValidation.class)
        private String name;

        public TestUpdateClass(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void validate_WhenIdIsNull_ShouldPassValidation() {
        TestUpdateClass validObject = new TestUpdateClass(null, "Valid Name");

        Set<ConstraintViolation<TestUpdateClass>> violations = validator.validate(validObject, UpdateValidation.class);

        assertTrue(violations.isEmpty(), "Не должно быть нарушений валидации");
    }

    @Test
    void validate_WhenIdIsNotNull_ShouldFailValidation() {
        TestUpdateClass invalidObject = new TestUpdateClass(1L, "Valid Name");

        Set<ConstraintViolation<TestUpdateClass>> violations = validator.validate(invalidObject, UpdateValidation.class);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("id", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validate_WhenNameIsBlank_ShouldFailValidation() {
        TestUpdateClass invalidObject = new TestUpdateClass(null, "");

        Set<ConstraintViolation<TestUpdateClass>> violations = validator.validate(invalidObject, UpdateValidation.class);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validate_WhenNameIsNull_ShouldFailValidation() {
        TestUpdateClass invalidObject = new TestUpdateClass(null, null);

        Set<ConstraintViolation<TestUpdateClass>> violations = validator.validate(invalidObject, UpdateValidation.class);

        assertFalse(violations.isEmpty(), "Должно быть нарушение валидации");
        assertEquals(1, violations.size());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void validate_WithoutValidationGroup_ShouldIgnoreConstraints() {
        TestUpdateClass object = new TestUpdateClass(1L, null);

        Set<ConstraintViolation<TestUpdateClass>> violations = validator.validate(object);

        assertTrue(violations.isEmpty(), "Валидация без группы не должна проверять аннотации");
    }
}