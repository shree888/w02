package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for {@link Animal}, beyond what {@link AnimalTest} already tests.
 */
@Tag("current")
class AnimalAdditionalTest {

  private static final LocalDate INTAKE = LocalDate.of(2026, 9, 21);

  @Test
  void theNullSpeciesRefusalNamesSpecies() {
    IntakeException nullSpecies =
        assertThrows(
            IntakeException.class, () -> new Animal("Rex", null, AgeMonths.of(1), INTAKE));
    assertTrue(
        nullSpecies.getMessage().toLowerCase().contains("species"),
        "the message should name the offending argument");
  }

  @Test
  void theNullAgeRefusalNamesAge() {
    IntakeException nullAge =
        assertThrows(
            IntakeException.class, () -> new Animal("Rex", Species.DOG, null, INTAKE));
    assertTrue(
        nullAge.getMessage().toLowerCase().contains("age"),
        "the message should name the offending argument");
  }

  @Test
  void theNullIntakeDateRefusalNamesIntakeDate() {
    IntakeException nullIntakeDate =
        assertThrows(
            IntakeException.class, () -> new Animal("Rex", Species.DOG, AgeMonths.of(1), null));
    assertTrue(
        nullIntakeDate.getMessage().toLowerCase().contains("intake"),
        "the message should name the offending argument");
  }

  @Test
  void theNullNameRefusalNamesName() {
    IntakeException nullName =
        assertThrows(
            IntakeException.class,
            () -> new Animal(null, Species.DOG, AgeMonths.of(1), INTAKE));
    assertTrue(
        nullName.getMessage().toLowerCase().contains("name"),
        "the message should name the offending argument even when the value is null, not just blank");
  }

  @Test
  void tabsAndNewlinesCountAsWhitespaceToTrim() {
    Animal pup = new Animal("\t Pip \n", Species.DOG, AgeMonths.of(1), INTAKE);
    assertEquals("Pip", pup.name());
  }

  @Test
  void aNameOfOnlyTabsAndNewlinesIsRefused() {
    assertThrows(
        IntakeException.class, () -> new Animal("\t\n ", Species.DOG, AgeMonths.of(1), INTAKE));
  }

  @Test
  void anIntakeExceptionIsUnchecked() {
    assertInstanceOf(
        IllegalArgumentException.class,
        assertThrows(IntakeException.class, () -> new Animal("", Species.DOG, AgeMonths.of(1), INTAKE)));
  }

  @Test
  void speciesAccessorReturnsTheSameEnumConstant() {
    Animal pup = new Animal("Pip", Species.BIRD, AgeMonths.of(1), INTAKE);
    assertSame(Species.BIRD, pup.species());
  }

  @Test
  void ageAccessorReturnsTheSameAgeInstance() {
    AgeMonths age = AgeMonths.of(23);
    Animal luna = new Animal("Luna", Species.CAT, age, INTAKE);
    assertSame(age, luna.age());
  }

  @Test
  void toStringOnAWholeYearOldOmitsTheMonthsPart() {
    Animal pup = new Animal("Pip", Species.DOG, AgeMonths.of(12), INTAKE);
    assertEquals("Pip (Dog, 1 year, intake 2026-09-21)", pup.toString());
  }

  @Test
  void toStringUsesTheBirdLabel() {
    Animal tweety = new Animal("Tweety", Species.BIRD, AgeMonths.of(0), INTAKE);
    assertEquals("Tweety (Bird, 0 months, intake 2026-09-21)", tweety.toString());
  }

  @Test
  void theOldestAllowedAgeIsAccepted() {
    Animal elder = new Animal("Elder", Species.DOG, AgeMonths.of(AgeMonths.MAX_MONTHS), INTAKE);
    assertEquals(AgeMonths.MAX_MONTHS, elder.age().months());
  }

  @Test
  void whenNameAndSpeciesAreBothInvalidTheNameIsReportedFirst() {
    IntakeException bothInvalid =
        assertThrows(
            IntakeException.class, () -> new Animal(null, null, AgeMonths.of(1), INTAKE));
    assertTrue(
        bothInvalid.getMessage().toLowerCase().contains("name"),
        "validation should check name before species, so the message should name the name");
  }

  @Test
  void toStringCombinesYearsAndMonthsForADog() {
    Animal pup = new Animal("Pip", Species.DOG, AgeMonths.of(23), INTAKE);
    assertEquals("Pip (Dog, 1 year, 11 months, intake 2026-09-21)", pup.toString());
  }

  @Test
  void toStringCombinesYearsAndMonthsForABird() {
    Animal tweety = new Animal("Tweety", Species.BIRD, AgeMonths.of(25), INTAKE);
    assertEquals("Tweety (Bird, 2 years, 1 month, intake 2026-09-21)", tweety.toString());
  }
}
