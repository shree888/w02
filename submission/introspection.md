# Design Introspection

## 1. What you built

I wrote two value types in `edu.northeastern.shelter`: `AgeMonths`, which wraps a whole number of
months and guarantees it is always between 0 and `MAX_MONTHS` (480), and `Animal`, which records a
single intake with parameters name, species, age, and intake date, and guarantees all four fields are valid before creating the object. 
`AgeMonths` is also responsible for turning a month count into the
human-readable year/month description that `Animal.toString()` reuses.

## 2. Design decisions

First decision I had to think about: to keep the MAX_MONTHS field in the AgeMonths.java class public or change it ot private.
The choice I made was to keep it public, as the existing test cases use this. An alternative would have been to change the existing test cases so that they don't use the MAX_MONTHS, however I did not want to edit the tests already given to us, incase that would have been against the requirments of this assignment. 
It costed me encapsulation because other classes can directly access or modify it.

Another design decision I had was to keep one exception for either name being null or an empty string, for the constructor of the Animal Class.
Alternatively, i could have created a second if statment, having 2 if statments in total for the name, one seeing if the name is null and the second see if it's an empty string (or a string onyl containing spaces), where each if statment throws it's own exception with seperate error messages.
I decided to have everything in one if statment, as this would make the code simpler and easier to read. 
It costed the error message being long and a bit ambigious if the name is invalid.

## 3. Invariants

`AgeMonths` guarantees that `0 <= months <= MAX_MONTHS` for any instance that exists. This is
enforced entirely in `of()` (`AgeMonths.java:45-50`): both bounds are checked before
`new AgeMonths(months)` is ever called, and the field is `private final` with no setter, so nothing
after construction can push it out of range.

`Animal` guarantees that `name` is non-null and non-blank (and stored trimmed), and that `species`,
`age`, and `intakeDate` are all non-null. All four checks run in the constructor
(`Animal.java:56-66`) before any field is assigned (`this.name = name.trim();` is the first
assignment, at line 68), so the object never exists in a half-valid state partway through
construction. Each guarantee is enforced in exactly one place.

## 4. Testing

The test cases in the `AgeMonthsAdditionalTest` file covers boundary and message cases: one below `MAX_MONTHS`, large negative values (`Integer.MIN_VALUE`), that negative/over-max refusals name the offending value and the limit, that `IntakeException` is unchecked, and `toString()` at the edges, `0`, `MAX_MONTHS` (`"40 years"`), and `MAX_MONTHS - 1` (`"39 years, 11 months"`).

`AnimalAdditionalTest` covers: each null-field refusal naming its own argument (species, age, intakeDate, name), tabs/newlines counting as trimmable whitespace, that `IntakeException` is unchecked, that accessors return the exact same object/enum instance passed in (not a copy), `toString()` across all three species (`DOG`, `BIRD`, plus the whole-year case), the oldest allowed age being accepted, and that when both `name` and `species` are invalid, the exception reports `name` first, confirming validation order.

Reviewing the code made me think of these test cases, as I was able to analize the code to try to see where I can add new test cases.

whenNameAndSpeciesAreBothInvalidTheNameIsReportedFirst test was hardest to design, since it made me realize my constructor's check order (name before species) was an implicit contract no test had actually pinned down before.

What is still untested is name validation against broader Unicode whitespace (e.g. em space \u2003), which trim() doesn't strip, so a name made entirely of such characters would wrongly pass as non-blank. With another hour I'd add a test for that case and switch the implementation from trim() to strip(), which is defined in terms of Character.isWhitespace() and would actually catch it.



## 5. What you would change

Given another day I would have read over the code files more to see how it could scale to the next assignements, and if therew as anything I can do to make my code easier for the next assignemnts, as the next assignments are based on my this one.

## 6. What you found hard

I found it hard to come up with new test cases, as many of the test cases I thought of were already there. However i looked over the code again and analysised the functionality that was missing testing to help.
