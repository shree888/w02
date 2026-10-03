# Design Introspection

## 1. What you built

I wrote two value types in `edu.northeastern.shelter`: `AgeMonths`, which wraps a whole number of
months and guarantees it is always between 0 and `MAX_MONTHS` (480), and `Animal`, which records a
single intake with parameters name, species, age, and intake date, and guarantees all four fields are valid before creating the object. 
`AgeMonths` is also responsible for turning a month count into the
human-readable year/month description that `Animal.toString()` reuses.

## 2. Design decisions

**Representing age as `AgeMonths` instead of a raw `int` on `Animal`.** The alternative was to store
an `int ageMonths` field directly on `Animal` and range-check it in the `Animal` constructor.
I used the `AgeMonths` value type instead, so `Animal`'s constructor can check `age == null` and
stop there. It never has to ask whether the number inside is sensible, because an `AgeMonths` that
exists already guarantees that (`AgeMonths.of`, lines 44–52). The cost is indirection: every call
site that wants the raw number has to go through `.months()`, and tests have to write
`AgeMonths.of(23)` instead of a bare `23`.

**Delegating `Animal.toString()` to `AgeMonths.toString()` instead of re-deriving the years/months
text.** `Animal.toString()` (`Animal.java:130-132`) is built entirely from `this.age`'s own
description rather than recomputing `years()`/`remainderMonths()` and reassembling the
singular/plural logic a second time. This means the formatting rule — singular/plural agreement,
omitting the months clause on a whole year — exists in exactly one place. The cost is coupling:
`Animal`'s output format is now hostage to `AgeMonths`'s; if that format ever changes, `Animal`'s
changes with it whether or not that was intended.

**A private constructor plus a named static factory (`AgeMonths.of`) instead of a public
constructor.** The alternative was a public `AgeMonths(int months)` constructor that validates
inline. I kept the constructor private and validate-then-construct inside `of()` (lines 44–52), so
there is exactly one path into the type and that path is named for what it does. The cost is a
small amount of ceremony: an extra method and a private constructor for what is, underneath, one
field.

## 3. Invariants

`AgeMonths` guarantees that `0 <= months <= MAX_MONTHS` for any instance that exists. This is
enforced entirely in `of()` (`AgeMonths.java:45-50`): both bounds are checked before
`new AgeMonths(months)` is ever called, and the field is `private final` with no setter, so nothing
after construction can push it out of range.

`Animal` guarantees that `name` is non-null and non-blank (and stored trimmed), and that `species`,
`age`, and `intakeDate` are all non-null. All four checks run in the constructor
(`Animal.java:56-66`) before any field is assigned (`this.name = name.trim();` is the first
assignment, at line 68) — so the object never exists in a half-valid state partway through
construction. Each guarantee is enforced in exactly one place; I did not duplicate any check.

## 4. Testing

The test cases in the `AgeMonthsAdditionalTest` file covers boundary and message cases: one below `MAX_MONTHS`, large negative values (`Integer.MIN_VALUE`), that negative/over-max refusals name the offending value and the limit, that `IntakeException` is unchecked, and `toString()` at the edges — `0`, `MAX_MONTHS` (`"40 years"`), and `MAX_MONTHS - 1` (`"39 years, 11 months"`).

`AnimalAdditionalTest` covers: each null-field refusal naming its own argument (species, age, intakeDate, name), tabs/newlines counting as trimmable whitespace, that `IntakeException` is unchecked, that accessors return the exact same object/enum instance passed in (not a copy), `toString()` across all three species (`DOG`, `BIRD`, plus the whole-year case), the oldest allowed age being accepted, and that when both `name` and `species` are invalid, the exception reports `name` first — confirming validation order.

Reviewing the code made me think of these test cases, as I was able to analize the code to try to see where I can add new test cases.

whenNameAndSpeciesAreBothInvalidTheNameIsReportedFirst test was hardest to design, since it made me realize my constructor's check order (name before species) was an implicit contract no test had actually pinned down before.

What is still untested is name validation against broader Unicode whitespace (e.g. em space \u2003), which trim() doesn't strip, so a name made entirely of such characters would wrongly pass as non-blank. With another hour I'd add a test for that case and switch the implementation from trim() to strip(), which is defined in terms of Character.isWhitespace() and would actually catch it.



## 5. What you would change

Given another day I would have read over the code files more to see how it could scale to the next assignements, and if therew as anything I can do to make my code easier for the next assignemnts, as the next assignments are based on my this one.

## 6. What you found hard

I found it hard to come up with new test cases, as many of the test cases I thought of were already there. However i looked over the code again and analysised the functionality that was missing testing to help.
