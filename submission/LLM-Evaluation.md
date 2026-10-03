# LLM Evaluation

## Assessment
The LLM's assessment, in full and unedited:

Confirmed against the actual files on disk: `AgeMonths` is `public final class`, but `Animal` is `public class` (not `final`), and `submission/introspection.md` does not yet exist (only a `.gitkeep` placeholder in that directory). Both facts factor into the review below.

---

## 1. Immutability and encapsulation — 21/25

All four `Animal` fields are `private final` (`Animal.java:27-30`), and `AgeMonths`'s single field is `private final int months` (`AgeMonths.java:18`). No setters exist anywhere. Every accessor either returns an immutable type outright (`String`, `Species`, `AgeMonths`, `LocalDate`) or is documented as safe to hand back directly (`intakeDate()`'s Javadoc correctly reasons that `LocalDate`'s own immutability makes direct return safe) — no caller can reach back in and mutate state.

**Deduction:** `AgeMonths` is declared `public final class AgeMonths` (`AgeMonths.java:14`), but `Animal` is only `public class Animal` (`Animal.java:26`) — not `final`. The class's own Javadoc stakes its whole design on "an `Animal` that exists is an `Animal` that makes sense," but an unmarked class can be subclassed, and a subclass could override `toString()`, add mutable state, or otherwise violate that invariant without touching the fields directly. `AgeMonths` was given to the student specifically as the completed worked example that *is* `final`, which makes the omission on `Animal` a specific, avoidable miss rather than an ambiguous judgment call.

## 2. Constructor validation — 22/25

All five required checks are present, and critically, all four `null`/validity checks run *before* any field is assigned (`Animal.java:50-60`, assignment only begins at line 61) — the "fail before you commit to any state" discipline is followed correctly. Name is stored trimmed (`this.name = name.trim();`, line 61).

- Null name → covered by `if (name == null || name.trim().isEmpty())` (line 50)
- Blank/whitespace-only name → same check
- Null species → line 53
- Null age → line 56
- Null intake date → line 59

**Deduction:** the combined null/blank name check produces a confusing message when `name` is `null`:
`"name must contain at least one non-whitespace character, and must be non-null was " + name` → renders as *"name must contain at least one non-whitespace character, and must be non-null was null"*. This is grammatically broken and doesn't clearly tell a reader whether the problem was nullness or blankness — it will pass the test suite's `.contains("name")` check, but it's a visibly weaker message than every other validation in this codebase (compare `AgeMonths.of`'s clean `"age in months cannot be negative, was " + months`, or `Animal`'s own `"species must be non-null"`). Splitting this into two `if` branches (null vs. blank) would fix it.

## 3. Correctness — 15/15

I traced `AgeMonths.toString()` against all seven specified inputs by hand:

| months | expected | actual |
|---|---|---|
| 0 | `"0 months"` | `"0 months"` ✓ |
| 1 | `"1 month"` | `"1 month"` ✓ |
| 11 | `"11 months"` | `"11 months"` ✓ |
| 12 | `"1 year"` | `"1 year"` ✓ |
| 23 | `"1 year, 11 months"` | `"1 year, 11 months"` ✓ |
| 24 | `"2 years"` | `"2 years"` ✓ |
| 25 | `"2 years, 1 month"` | `"2 years, 1 month"` ✓ |

Singular/plural agreement is handled independently for the years part and months part, and the whole-year branch (`remainder == 0`) correctly omits the months clause. `Animal.toString()` produces exactly `"Luna (Cat, 1 year, 11 months, intake 2026-09-21)"` for the specified inputs — verified by concatenation, not re-derivation. No deductions.

## 4. Testing and coverage — 12/15

Boundaries are genuinely covered: 0 months, the 11/12 edge (`isUnderOneYearIsTrueBelowTwelveMonths` checks 11, 12, *and* 13), the maximum (480), one below it, and one above it (`aboveTheMaximumIsRefused`). Every exception path for both classes has a dedicated test, and — notably — every one of them asserts the *specific* `IntakeException` type via `assertThrows(IntakeException.class, ...)` rather than a bare `Exception.class`, so there are no vacuous assertions that would survive a wrong implementation. `anIntakeExceptionIsUnchecked` in both additional test files specifically pins down that it's an `IllegalArgumentException`, not just "something throwable."

**Three specific untested cases:**
1. No test exercises what happens when *multiple* constructor arguments are invalid at once (e.g., `new Animal(null, null, AgeMonths.of(1), INTAKE)`) — nothing confirms the validation order (name checked first) actually holds when more than one field is bad simultaneously.
2. `oneMonthBelowAYearIsStillMonthsOnly` checks `years()`/`remainderMonths()` at `MAX_MONTHS - 1` (479), but no test calls `.toString()` at that value to confirm the combined-format rendering (`"39 years, 11 months"`) at the boundary adjacent to the max.
3. The combined "years, months" `toString()` format is only ever exercised through `Species.CAT` (Luna, 23 months). Every other species in the Animal tests uses either a whole-year age (12) or an under-a-year age (0, 1) — there's no test confirming the `"X years, Y months"` branch renders correctly for a `DOG` or `BIRD`.

## 5. Code quality and style — 10/10

Every public member — including `MAX_MONTHS`, the private constructor (documented despite being private, which is a nice touch beyond the requirement), and both `toString()` overrides — carries a Javadoc statement that adds information beyond the signature (e.g., `intakeDate()`'s note on *why* returning the raw `LocalDate` is safe, which is exactly the kind of "why, not what" the rubric is checking for). `Animal.toString()` is built entirely from `this.name`, `this.species`, `this.age`, `this.intakeDate` via string concatenation and contains no arithmetic or the literal words "year"/"month" — it fully delegates to `AgeMonths.toString()`. If the age format changes, this method needs zero edits.

## 6. Scope discipline and code walk — 10/10, with one item I can't verify

No `equals`/`hashCode`, no collections, no inheritance added — `AnimalTest.twoIdenticalAnimalsAreDifferentObjects` even explicitly pins down the deliberate absence of value equality, which shows the student read and respected the lab's stated boundary rather than reflexively adding `equals` out of habit.

**I cannot assess the `Species` introspection check**: `submission/introspection.md` does not exist in the repo yet (the `submission/` directory contains only a `.gitkeep` placeholder). I'd need that file before I can evaluate whether the student can explain why `Species` is an enum-with-a-field-and-method rather than three constants.

---

**Total: 90/100** (pending the introspection.md review, which could move category 6).

**The single most important thing to do differently next time:** mark `Animal` as `final`, the same way `AgeMonths` was given to you already marked. You clearly understand *why* immutability matters — the Javadoc on `intakeDate()` proves it — but the class declaration itself is the one place that guarantee isn't actually enforced by the compiler, and it's an easy, mechanical fix.

**One thing done genuinely well:** the test suite's discipline around specific assertions — every `assertThrows` pins down `IntakeException.class` (and separately verifies it's unchecked via `assertInstanceOf`), rather than the weaker, easier-to-write `assertThrows(Exception.class, ...)` that would silently pass even if the wrong exception type were thrown.


---

## Your response


### Where it is right


I accept the criticism name 'must contain at least one non-whitespace character, and must be non-null was " + name' string message for the exception being too vauge. I would change the exception to have a specific error messaged based on whether it was null or an empty string. I agree with this criticism becuase the error message is too vauge and can be changed to provide more information about what error occured.

I also agree about the criticism about the missing test cases and I would add additional test to cover the three areas I missed. I agree with this criticism becuase it will help better test the code with these new tests added.



### Where it is wrong

If I would make the MAX_MONTHS fields private, then the existing tests fail, as the tests use this field. I would not do this for that reason.

### What it missed

My blank-name check uses name.trim().isEmpty(), but trim() only strips ASCII whitespace (<= U+0020) — it misses broader Unicode whitespace like \u2003 (em space), so a name made entirely of those characters would wrongly pass as "non-blank," contradicting my own stated rule. Using String.strip() instead would fix this, since it's based on Character.isWhitespace().

### What you changed

I added some test cases to add more coverage to my code.

---

## Declaration

- Which LLM and version you used: Sonnet 5
- Confirm you understand every line you submitted, regardless of who or what wrote it: yes
