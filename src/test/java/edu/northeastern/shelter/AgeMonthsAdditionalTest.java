package edu.northeastern.shelter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Additional tests for {@link AgeMonths}, beyond what {@link AgeMonthsTest} already tests.
 */
@Tag("current")
class AgeMonthsAdditionalTest {

  @Test
  void oneBelowTheMaximumIsAllowed() {
    assertEquals(AgeMonths.MAX_MONTHS - 1, AgeMonths.of(AgeMonths.MAX_MONTHS - 1).months());
  }

  @Test
  void theNegativeRefusalNamesTheOffendingValue() {
    IntakeException tooYoung = assertThrows(IntakeException.class, () -> AgeMonths.of(-7));
    assertTrue(
        tooYoung.getMessage().contains("-7"),
        "the message should name the negative value that was rejected");
  }

  @Test
  void theNegativeRefusalSaysTheAgeCannotBeNegative() {
    IntakeException tooYoung = assertThrows(IntakeException.class, () -> AgeMonths.of(-1));
    assertTrue(
        tooYoung.getMessage().toLowerCase().contains("negative"),
        "the message should describe why the value was rejected, not just restate it");
  }

  @Test
  void theMaximumRefusalSaysTheLimit() {
    IntakeException tooOld =
        assertThrows(IntakeException.class, () -> AgeMonths.of(AgeMonths.MAX_MONTHS + 1));
    assertTrue(
        tooOld.getMessage().contains(String.valueOf(AgeMonths.MAX_MONTHS)),
        "the message should state the maximum that was exceeded, not just the rejected value");
  }

  @Test
  void aLargeNegativeValueIsStillRefused() {
    assertThrows(IntakeException.class, () -> AgeMonths.of(Integer.MIN_VALUE));
  }

  @Test
  void anIntakeExceptionIsUnchecked() {
    assertInstanceOf(IllegalArgumentException.class, assertThrows(IntakeException.class, () -> AgeMonths.of(-1)));
  }

  @Test
  void theMaximumIsExactlyFortyWholeYears() {
    assertEquals("40 years", AgeMonths.of(AgeMonths.MAX_MONTHS).toString());
  }

  @Test
  void oneMonthBelowAYearIsStillMonthsOnly() {
    AgeMonths age = AgeMonths.of(AgeMonths.MAX_MONTHS - 1);
    assertFalse(age.isUnderOneYear());
    assertEquals(39, age.years());
    assertEquals(11, age.remainderMonths());
  }

  @Test
  void zeroMonthsDescribesAsZeroMonthsNotZeroYears() {
    assertEquals("0 months", AgeMonths.of(0).toString());
  }

  @Test
  void toStringCombinesYearsAndMonthsOneBelowTheMaximum() {
    assertEquals("39 years, 11 months", AgeMonths.of(AgeMonths.MAX_MONTHS - 1).toString());
  }
}
