// -----------------------------------------------------------------
// Project: Part 1, Summary
// -----------------------------------------------------------------

// You are going to design an application to allow a user to
// self-study using flash cards. In this part of the project,
// a user will...

// 1. Be prompted to choose from a menu of available flash
//    card decks; this menu will repeat until a valid
//    selection is made.
//
// 2. Proceed through each card in the selected deck,
//    one-by-one. For each card, the front is displayed,
//    and the user is allowed time to reflect; then the
//    back is displayed; and the user is asked if they
//    got the correct answer.
//
// 3. Once the deck is exhausted, the program outputs the
//    number of self-reported correct answers and ends.
//

// Of course, we'll design this program step-by-step, AND
// you've already done pieces of this in homework!!
// (Note: you are welcome to leverage your prior work and/or
// code found in the sample solutions & lecture notes.)
//

// Lastly, here are a few overall project requirements...
// - Since mutation hasn't been covered in class, your design is
//   NOT allowed to make use of mutable variables and/or lists.
// - As included in the instructions, all interactive parts of
//   this program MUST make effective use of the reactConsole
//   framework.
// - Staying consistent with our Style Guide...
//   * All functions must have:
//     a) a preceding comment specifying what it does
//     b) an associated @EnabledTest function with sufficient
//        tests using testSame
//   * All data must have:
//     a) a preceding comment specifying what it represents
//     b) associated representative examples
// - You will be evaluated on a number of criteria, including...
//   * Adherence to instructions and the Style Guide
//   * Correctly producing the functionality of the program
//   * Design decisions that include choice of tests, appropriate
//     application of list abstractions, and task/type-driven
//     decomposition of functions.
//

// -----------------------------------------------------------------
// Data design
// (Hint: see Homework 3, Problem 2)
// -----------------------------------------------------------------

import khoury.CapturedResult
import khoury.EnabledTest
import khoury.captureResults
import khoury.fileExists
import khoury.fileReadAsList
import khoury.isAnInteger
import khoury.linesToString
import khoury.reactConsole
import khoury.runEnabledTests
import khoury.testSame

// TODO 1/2: Design the data type FlashCard to represent a single
//           flash card. You should be able to represent the text
//           prompt on the front of the card as well as the text
//           answer on the back. Include at least 3 example cards
//           (which will come in handy later for tests!).
//

// represents a flashcard with a front string and back string.
data class FlashCard(val front: String, val back: String)

val qEG = "What is the capital of Egypt?"
val aEG = "Cairo"

val qUS = "What is the capital of the USA?"
val aUS = "Washington, D.C."

val qJP = "What is the capital of Japan?"
val aJP = "Tokyo"

val qKR = "What is the capital of Korea?"
val aKR = "Seoul"

val fcEG = FlashCard(qEG, aEG)
val fcUS = FlashCard(qUS, aUS)
val fcJP = FlashCard(qJP, aJP)
val fcKR = FlashCard(qKR, aKR)

// TODO 2/2: Design the data type Deck to represent a deck of
//           flash cards. The deck should have a name, as well
//           as a Kotlin list of flash cards.
//
//           Include at least 2 example decks based upon the
//           card examples above.
//

val nameAsia = "Asian Capitals"
val nameWorld = "World Capitals"

// represents a Deck with a name as a string and a list of flashcards.
data class Deck(val name: String, val cards: List<FlashCard>)

val deckAsia = Deck(nameAsia, listOf(fcJP, fcKR))
val deckWorld = Deck(nameWorld, listOf(fcEG, fcUS, fcJP, fcKR))

// -----------------------------------------------------------------
// Generating flash cards
// -----------------------------------------------------------------

// One benefit of digital flash cards is that sometimes we can
// use code to produce cards that match a known pattern without
// having to write all the fronts/backs by hand!
//

// TODO 1/1: Design the function perfectSquares that takes a
//           count (assumed to be positive) and produces the
//           list of flash cards that tests that number of the
//           first squares.
//
//           For example, the first three perfect squares...
//
//            1. front (1^2 = ?), back (1)
//            2. front (2^2 = ?), back (4)
//            3. front (3^2 = ?), back (9)
//
//           have been supplied as named values.
//
//           Hint: you might consider combining your
//                 kthPerfectSquare function from Homework 1
//                 with the list constructor in Homework 3.
//

val square1Front = "1^2 = ?"
val square2Front = "2^2 = ?"
val square3Front = "3^2 = ?"

val square1Back = "1"
val square2Back = "4"
val square3Back = "9"

val fcSquare1 = FlashCard(square1Front, square1Back)
val fcSquare2 = FlashCard(square2Front, square2Back)
val fcSquare3 = FlashCard(square3Front, square3Back)

// takes an int and returns a flashcard where the front and back are
// strings asking and answering what the int squared equals.
fun makeSquaredList(numInput: Int): FlashCard {
    val numToSquare = numInput + 1
    val numSquared = numToSquare * numToSquare
    return FlashCard("$numToSquare^2 = ?", "$numSquared")
}

@EnabledTest
fun testMakeSquaredList() {
    testSame(
        makeSquaredList(0),
        FlashCard("1^2 = ?", "1"),
        "(0 + 1) squared is 1",
    )

    testSame(
        makeSquaredList(1),
        FlashCard("2^2 = ?", "4"),
        "(1 + 1) squared is 4",
    )

    testSame(
        makeSquaredList(5),
        FlashCard("6^2 = ?", "36"),
        "(5 + 1) squared is 25",
    )
}

// takes an int and will produce a list of flashcards that tests that number
// of the first squares.
fun perfectSquares(numFC: Int): List<FlashCard> {
    return List<FlashCard>(numFC, ::makeSquaredList)
}

@EnabledTest
fun testPerfectSquares() {
    testSame(
        perfectSquares(0),
        listOf(),
        "empty list",
    )

    testSame(
        perfectSquares(1),
        listOf(fcSquare1),
        "list of one element",
    )

    testSame(
        perfectSquares(3),
        listOf(fcSquare1, fcSquare2, fcSquare3),
        "list of three elements",
    )
}

// -----------------------------------------------------------------
// Files of cards
// -----------------------------------------------------------------

// Consider a simple format for storing flash cards in a file:
// each card is a line in the file, where the front comes first,
// separated by a "pipe" character ('|'), followed by the text
// on the back of the card.
//

val charSep = "|"

// TODO 1/3: Design the function cardToString that takes a flash
//           card as input and produces a string according to the
//           specification above ("front|back"). Make sure to
//           test all your card examples!
//

// takes in a flashcard and converts it into a string where the pipe character
// is in between the front and back strings of the flashcard.
fun cardToString(fc: FlashCard): String {
    return "${fc.front}|${fc.back}"
}

@EnabledTest
fun testCardToString() {
    testSame(
        cardToString(fcJP),
        "What is the capital of Japan?|Tokyo",
        "JP",
    )

    testSame(
        cardToString(fcUS),
        "What is the capital of the USA?|Washington, D.C.",
        "US",
    )
}

// TODO 2/3: Design the function stringToCard that takes a string,
//           assumed to be in the format described above, and
//           produces the corresponding flash card.
//
//           Hints:
//           - look back to how we extracted data from CSV
//             (comma-separated value) files (such as in
//             Homework 3)!
//           - a great way to test: for each of your card
//             examples, pass them through the function in TODO
//             1 to convert them to a string; then, pass that
//             result to this function... you *should* get your
//             original flash card back :)
//

// this function takes the formatted string and produces a corresponding
// flashcard.
fun stringToCard(fcString: String): FlashCard {
    val splitString = fcString.split("|")
    return FlashCard(splitString.first(), splitString.last())
}

@EnabledTest
fun testStringToCard() {
    testSame(
        stringToCard(""),
        FlashCard("", ""),
        "test no input",
    )

    testSame(
        stringToCard(cardToString(fcKR)),
        fcKR,
        "KR",
    )

    testSame(
        stringToCard(cardToString(fcEG)),
        fcEG,
        "EG",
    )
}

// TODO 3/3: Design the function readCardsFile that takes a path
//           to a file and produces the corresponding list of
//           flash cards found in the file.
//
//           If the file does not exist, return an empty list.
//           Otherwise, you can assume that every line is
//           formatted in the string format we just worked with.
//
//           Hint:
//           - Think about how HW3-P1 effectively used an
//             abstraction to process all the lines in a
//             file assuming a known pattern.
//           - We've provided an "example.txt" file that you can
//             use for testing if you'd like; also make sure to
//             test your function when the supplied file does not
//             exist!
//

// checks whether the filename as a string is valid and if it is will take the contents
// of the file and makes a corresponding flashcard list
fun readCardsFile(fileName: String): List<FlashCard> {
    val fileStrings = fileReadAsList(fileName)
    if (fileExists(fileName)) {
        return fileStrings.map(::stringToCard)
    } else {
        return listOf()
    }
}

@EnabledTest
fun testReadCardsFile() {
    testSame(
        readCardsFile(""),
        listOf(),
        "empty string",
    )

    testSame(
        readCardsFile("example.txt"),
        listOf(FlashCard("front 1", "back 1"), FlashCard("front 2", "back 2")),
        "good file",
    )

    testSame(
        readCardsFile("BADEXAMPLE.txt"),
        listOf(),
        "bad file",
    )
}

// -----------------------------------------------------------------
// Processing a self-report
// (Hint: see Homework 2)
// -----------------------------------------------------------------

// In our program, we will ask for a self-report as to whether
// the user got the correct answer for a card, SO...

// TODO 1/1: Finish designing the function isPositive that
//           determines if the supplied string starts with
//           the letter "y" (either upper or lowercase).
//
//           You've been supplied with a number of tests - make
//           sure you understand what they are doing!
//

// determines if the string starts with y or Y and is no-input (null) safe.
fun isPositive(stringToTest: String): Boolean {
    return if (stringToTest != "") {
        (stringToTest.first().uppercase() == "Y")
    } else {
        false
    }
}

@EnabledTest
fun testIsPositive() {
    fun helpTest(
        str: String,
        expected: Boolean,
    ) {
        testSame(isPositive(str), expected, str)
    }

    helpTest("yes", true)
    helpTest("Yes", true)
    helpTest("YES", true)
    helpTest("yup", true)

    helpTest("nope", false)
    helpTest("NO", false)
    helpTest("nah", false)
    helpTest("not a chance", false)

    // should pass,
    // despite doing the wrong thing
    helpTest("indeed", false)
}

// -----------------------------------------------------------------
// Choosing a deck from a menu
// -----------------------------------------------------------------

// Now let's work on providing a menu of decks from which a user
// can choose what they want to study.

// TODO 1/2: Finish design the function choicesToText that takes
//           a list of strings (assumed to be non-empty) and
//           produces the textual representation of a menu of
//           those options.
//
//           For example, given...
//
//           ["a", "b", "c"]
//
//           The menu would be...
//
//           "1. a
//            2. b
//            3. c
//
//            Enter your choice"
//
//            As you have probably guessed, this will be a key
//            piece of our rendering function :)
//
//            Hints:
//            - Think back to Homework 3 when we used a list
//              constructor to generate list elements based
//              upon an index.
//            - If you can produce a list of strings, the
//              linesToString function in the Khoury library
//              will bring them together into a single string.
//            - Make sure to understand the supplied tests!
//

val promptMenu = "Enter your choice"

// takes a list of strings and returns a menu (formatted string).
fun choicesToText(listToRep: List<String>): String {
    fun makeTextRep(listSize: Int): String {
        val numToShow = listSize + 1
        return "$numToShow. ${listToRep[listSize]}"
    }
    val stringedList =
        linesToString(
            List<String>(
                listToRep.size,
                ::makeTextRep,
            ),
        )
    return "${stringedList}\n\n" + promptMenu
}

@EnabledTest
fun testChoicesToText() {
    val optA = "apple"
    val optB = "banana"
    val optC = "carrot"

    testSame(
        choicesToText(listOf()),
        linesToString(
            "",
            "",
            promptMenu,
        ),
        "empty",
    )

    testSame(
        choicesToText(listOf(optA)),
        linesToString(
            "1. $optA",
            "",
            promptMenu,
        ),
        "one",
    )

    testSame(
        choicesToText(listOf(optA, optB, optC)),
        linesToString(
            "1. $optA",
            "2. $optB",
            "3. $optC",
            "",
            promptMenu,
        ),
        "three",
    )
}

// TODO 2/2: Finish designing the program chooseOption that takes
//           a list of decks, produces a corresponding numbered
//           menu (1-# of decks, each showing its name), and
//           returns the deck corresponding to the number entered.
//           (Of course, keep displaying the menu until a valid
//           number is entered.)
//
//           Hints:
//            - Review the "Valid Number Example" of reactConsole
//              as one example of how to validate input. In this
//              case, however, since we know that we have a valid
//              range of integers, we can simplify the state
//              representation significantly :)
//            - To help you get started, the chooseOption function
//              has been written, but you must complete the helper
//              functions; look to the comments below for guidance.
//              You can then play "signature detective" to figure
//              out the parameters/return type of the functions you
//              need to write :)
//            - Lastly, as always, don't forget to sufficiently
//              test all the functions you write in this problem!
//

// retrieves the decks name from a given deck.
fun getDeckName(deck: Deck): String {
    return deck.name
}

@EnabledTest
fun testGetDeckName() {
    testSame(
        getDeckName(deckAsia),
        "Asian Capitals",
        "deck Asia",
    )

    testSame(
        getDeckName(deckWorld),
        "World Capitals",
        "deck World",
    )
}

// takes in user input string string and deck index range, checks if string is an Int and checks if
// given number is a valid input then return typed int - 1 if valid and -1 if not.
fun keepIfValid(
    typedString: String,
    validInd: IntRange,
): Int {
    if ((isAnInteger(typedString)) && ((typedString.toInt() - 1) in validInd)) {
        val typedInt = typedString.toInt()
        return typedInt - 1
    } else {
        return -1
    }
}

@EnabledTest
fun testKeepIfValid() {
    testSame(
        keepIfValid("", 0..1),
        -1,
        "empty string",
    )

    testSame(
        keepIfValid("apples", 0..1),
        -1,
        "not an int",
    )

    testSame(
        keepIfValid("1", 0..1),
        0,
        "deck one",
    )

    testSame(
        keepIfValid("2", 0..1),
        1,
        "deck two",
    )

    testSame(
        keepIfValid("0", 0..1),
        -1,
        "invalid intRange",
    )

    testSame(
        keepIfValid("3", 0..1),
        -1,
        "invalid intRange",
    )

    testSame(
        keepIfValid("4", 0..3),
        3,
        "deck 4",
    )
}

// takes a name and reformats it to make a user friendly string.
fun choiceAnnouncement(name: String): String {
    return "Your chosen deck: $name"
}

@EnabledTest
fun testChoiceAnnouncement() {
    testSame(
        choiceAnnouncement(""),
        "Your chosen deck: ",
        "empty string",
    )

    testSame(
        choiceAnnouncement("Asian Capitals"),
        "Your chosen deck: Asian Capitals",
        "display Asia",
    )

    testSame(
        choiceAnnouncement("World Capitals"),
        "Your chosen deck: World Capitals",
        "display World",
    )
}

// a program to allow the user to interactively select
// a deck from the supplied, non-empty list of decks
fun chooseOption(decks: List<Deck>): Deck {
    // since the event handlers will need some info about
    // the supplied decks, the functions inside
    // chooseOption provide info about them while the
    // parameter is in scope

    // TODO: Above chooseOption, design the function
    //       getDeckName, which returns the name of
    //       a supplied deck.
    fun renderDeckOptions(state: Int): String {
        return choicesToText(decks.map(::getDeckName))
    }

    // TODO: Above chooseOption, design the function
    //       keepIfValid, that takes the typed input
    //       as a string, as well as the valid
    //       indices of the decks; note that the list indices
    //       will be in the range [0, size), whereas the
    //       user will see and work with [1, size].
    //
    //       If the user did not type a valid integer,
    //       or not one in [1, size], return -1; otherwise
    //       return the string converted to an integer, but
    //       subtract 1, which makes it a valid list index.
    fun transitionOptionChoice(
        ignoredState: Int,
        kbInput: String,
    ): Int {
        return keepIfValid(kbInput, decks.indices)
    }

    // TODO: nothing, but understand this :)
    fun validChoiceEntered(state: Int): Boolean {
        return state in decks.indices
    }

    // TODO: Above chooseOption, design the function
    //       choiceAnnouncement that takes the selected
    //       deck name and returns an announcement that
    //       makes you happy. For a simple example, given
    //       "fundies" as the chosen deck name, you might
    //       return "you chose: fundies"
    fun renderChoice(state: Int): String {
        return choiceAnnouncement(getDeckName(decks[state]))
    }

    return decks[
        reactConsole(
            initialState = -1,
            stateToText = ::renderDeckOptions,
            nextState = ::transitionOptionChoice,
            isTerminalState = ::validChoiceEntered,
            terminalStateToText = ::renderChoice,
        ),
    ]
}

@EnabledTest
fun testChooseOption() {
    // list of deck to test
    val testerDeckList = listOf(deckAsia, deckWorld)

    // makes a captureResults-friendly function :)
    fun helpTest(decks: List<Deck>): () -> Deck {
        fun chooseMyOption(): Deck {
            return chooseOption(decks)
        }
        return ::chooseMyOption
    }

    testSame(
        captureResults(
            helpTest(testerDeckList),
            "1",
        ),
        CapturedResult(
            deckAsia,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: Asian Capitals",
        ),
        "choose deckAsia",
    )

    testSame(
        captureResults(
            helpTest(testerDeckList),
            "2",
        ),
        CapturedResult(
            deckWorld,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: World Capitals",
        ),
        "choose deckWorld",
    )

    testSame(
        captureResults(
            helpTest(testerDeckList),
            "",
            "2",
        ),
        CapturedResult(
            deckWorld,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: World Capitals",
        ),
        "invalid input, then choose deckWorld",
    )

    testSame(
        captureResults(
            helpTest(testerDeckList),
            "3",
            "huh",
            "1",
        ),
        CapturedResult(
            deckAsia,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "1. Asian Capitals",
            "2. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: Asian Capitals",
        ),
        "two invalid input, then choose deckAsia",
    )
}

// -----------------------------------------------------------------
// Studying a deck
// -----------------------------------------------------------------

// Now let's design a program to allow a user to study through a
// supplied deck of flash cards.

// TODO 1/2: Design the data type StudyState to keep track of...
//           - which card you are currently studying in the deck
//           - are you looking at the front or back
//           - how many correct answers have been self-reported
//             thus far
//
//           Create sufficient examples so that you convince
//           yourself that you can represent any situation that
//           might arise when studying a deck.
//
//           Hints:
//           - Look back to the reactConsole problems in HW2 and
//             HW3; the former involved keeping track of a count
//             of loops (similar to the count of correct answers),
//             and the latter involved options for keeping track
//             of where you are in a list with reactConsole.
//

// Enum used as flashcards have exactly two sides, front & back.
enum class WhichSide {
    FRONT,
    BACK,
}

// state used for react console: yesCountrepresents number of self-reported
// correct as Int, frontBack represents if we're on the front or back using the
// WhichSide enum, currentFC is the chosen deck, and deckIndex is used to
// represent flashcard as the deck index Int.
data class StudyState(
    val yesCount: Int,
    val frontBack: WhichSide,
    val currentFC: Deck,
    val deckIndex: Int,
)

// TODO 2/2: Now, using reactConsole, design the program studyDeck
//           that for each card in a supplied deck, allows the
//           user to...
//
//           1. see the front (pause and think)
//           2. see the back
//           3. respond as to whether they got the answer
//
//           At the end, the user is told how many they self-
//           reported as correct (and this number is returned).
//
//           You have been supplied some prompts for steps #1
//           and #2 - feel free to change them if you'd like :)
//
//           Suggestions...
//           - Review the reactConsole videos/examples
//           - Start with studyDeck:
//             * write some tests to convince yourself you know
//               what your program is supposed to do!
//             * figure out how you'll create the initial state
//             * give names to the handlers you'll need
//             * how will you return the number correct?
//             * now comment-out this function, so that you can
//               design/test the handlers without interference :)
//           - For each handler...
//             * Play signature detective: based upon how it's
//               being used with reactConsole, what data will it
//               be given and what does it produce?
//             * Write some tests to convince yourself you know
//               its job.
//             * Write the code and don't move on till your tests
//               pass.
//            - Suggested ordering...
//              1. Am I done studying yet?
//              2. Rendering
//                 - It's a bit simpler to have a separate
//                   function for the terminal state.
//                 - The linesToString function is your friend to
//                   combine the card with the prompts.
//                 - Think about good decomposition when making
//                   the decision about front vs back content.
//              3. Transition
//                 - Start with the two main situations
//                   you'll find yourself in...
//                   > front->back
//                   > back->front
//                 - Then let a helper figure out how to handle
//                   the details of self-report
//
//            You've got this :-)
//

val studyThink = "Think of the result? Press enter to continue"
val studyCheck = "Did you answer correctly? (Y)es/(N)o"

// helper that takes in a StudyState and gets the front of the flashcard for
// the current flashcard
fun getFront(stateToFront: StudyState): String {
    return stateToFront.currentFC.cards[stateToFront.deckIndex].front
}

@EnabledTest
fun testGetFront() {
    testSame(
        getFront(StudyState(0, WhichSide.FRONT, deckAsia, 0)),
        qJP,
        "get deck asia index 0 front",
    )

    testSame(
        getFront(StudyState(1, WhichSide.FRONT, deckAsia, 1)),
        qKR,
        "get deck asia index 1 front",
    )

    testSame(
        getFront(StudyState(0, WhichSide.FRONT, deckWorld, 0)),
        qEG,
        "get deck asia index 0 front",
    )

    testSame(
        getFront(StudyState(0, WhichSide.BACK, deckWorld, 0)),
        qEG,
        "impossible scenario",
    )
}

// helper that takes in a StudyState and gets the back of the flashcard for
// the current flashcard
fun getBack(stateToBack: StudyState): String {
    return stateToBack.currentFC.cards[stateToBack.deckIndex].back
}

@EnabledTest
fun testGetBack() {
    testSame(
        getBack(StudyState(0, WhichSide.BACK, deckAsia, 0)),
        aJP,
        "get deck asia index 0 back",
    )

    testSame(
        getBack(StudyState(1, WhichSide.BACK, deckAsia, 1)),
        aKR,
        "get deck asia index 1 back",
    )

    testSame(
        getBack(StudyState(0, WhichSide.BACK, deckWorld, 0)),
        aEG,
        "get deck asia index 0 back",
    )

    testSame(
        getBack(StudyState(0, WhichSide.FRONT, deckWorld, 0)),
        aEG,
        "impossible scenario",
    )
}

// takes in the state and outputs the corresponding flashcard string and study prompt
fun showText(textState: StudyState): String {
    return when (textState.frontBack) {
        WhichSide.FRONT -> linesToString(getFront(textState), studyThink)
        WhichSide.BACK -> linesToString(getBack(textState), studyCheck)
    }
}

@EnabledTest
fun testShowText() {
    testSame(
        showText(StudyState(0, WhichSide.FRONT, deckAsia, 0)),
        linesToString(qJP, studyThink),
        "inital SS Asia",
    )

    testSame(
        showText(StudyState(2, WhichSide.BACK, deckAsia, 1)),
        linesToString(aKR, studyCheck),
        "index 1 KR",
    )

    testSame(
        showText(StudyState(0, WhichSide.FRONT, deckWorld, 0)),
        linesToString(qEG, studyThink),
        "initial SS World",
    )
}

// takes in the state and outputs new state depending on the input state, helper isPositive used
// to keep track of self-reported correct answers.
fun nextStudyState(
    newState: StudyState,
    newText: String,
): StudyState {
    return when (newState.frontBack) {
        WhichSide.FRONT -> StudyState(newState.yesCount, WhichSide.BACK, newState.currentFC, newState.deckIndex)
        WhichSide.BACK ->
            when (isPositive(newText)) {
                true -> StudyState(newState.yesCount + 1, WhichSide.FRONT, newState.currentFC, newState.deckIndex + 1)
                false -> StudyState(newState.yesCount, WhichSide.FRONT, newState.currentFC, newState.deckIndex + 1)
            }
    }
}

@EnabledTest
fun testNextStudyState() {
    testSame(
        nextStudyState(StudyState(0, WhichSide.FRONT, deckAsia, 0), ""),
        StudyState(0, WhichSide.BACK, deckAsia, 0),
        "inital asia state to back",
    )

    testSame(
        nextStudyState(StudyState(0, WhichSide.BACK, deckAsia, 0), ""),
        StudyState(0, WhichSide.FRONT, deckAsia, 1),
        "supplied string empty",
    )

    testSame(
        nextStudyState(StudyState(0, WhichSide.FRONT, deckWorld, 0), "ignore this"),
        StudyState(0, WhichSide.BACK, deckWorld, 0),
        "ignore string",
    )

    testSame(
        nextStudyState(StudyState(2, WhichSide.BACK, deckWorld, 2), "Yes"),
        StudyState(3, WhichSide.FRONT, deckWorld, 3),
        "supplied string yes",
    )

    testSame(
        nextStudyState(StudyState(2, WhichSide.BACK, deckWorld, 2), "no"),
        StudyState(2, WhichSide.FRONT, deckWorld, 3),
        "supplied string no",
    )
}

// once we've indexed through the entire deck isStudyDone is true.
fun isStudyDone(doneState: StudyState): Boolean {
    return doneState.deckIndex == doneState.currentFC.cards.size
}

@EnabledTest
fun testIsStudyDone() {
    testSame(
        isStudyDone(StudyState(0, WhichSide.FRONT, deckAsia, 0)),
        false,
        "deckAsia false",
    )

    testSame(
        isStudyDone(StudyState(2, WhichSide.FRONT, deckWorld, 3)),
        false,
        "deckWorld false",
    )

    testSame(
        isStudyDone(StudyState(0, WhichSide.FRONT, deckWorld, 4)),
        true,
        "deckWorld true",
    )
}

// for the user to see how many they self reported correct out of the maximum possible correct.
fun ansCorrect(endState: StudyState): String {
    return "You answered ${endState.yesCount} of ${endState.currentFC.cards.size} correctly"
}

@EnabledTest
fun testAnsCorrect() {
    testSame(
        ansCorrect(StudyState(2, WhichSide.FRONT, deckAsia, 2)),
        "You answered 2 of 2 correctly",
        "deckAsia all correct",
    )

    testSame(
        ansCorrect(StudyState(2, WhichSide.BACK, deckAsia, 2)),
        "You answered 2 of 2 correctly",
        "deckAsia all correct (giving back of flashcard)",
    )

    testSame(
        ansCorrect(StudyState(4, WhichSide.FRONT, deckWorld, 4)),
        "You answered 4 of 4 correctly",
        "deckWorld all correct",
    )

    testSame(
        ansCorrect(StudyState(0, WhichSide.FRONT, deckWorld, 4)),
        "You answered 0 of 4 correctly",
        "deckWorld none correct",
    )

    testSame(
        ansCorrect(StudyState(0, WhichSide.FRONT, deckWorld, 0)),
        "You answered 0 of 4 correctly",
        "deckWorld impossible scnenario",
    )
}

// runs reactConsole that takes in a deck and returns the count of self-reported correct.
fun studyDeck(chosenDeck: Deck): Int {
    return reactConsole(
        initialState = StudyState(0, WhichSide.FRONT, chosenDeck, 0),
        stateToText = ::showText,
        nextState = ::nextStudyState,
        isTerminalState = ::isStudyDone,
        terminalStateToText = ::ansCorrect,
    ).yesCount
}

@EnabledTest
fun testStudyDeck() {
    // makes a captureResults-friendly function :)
    fun helpTest(chosenDeck: Deck): () -> Int {
        fun studyMyDeck(): Int {
            return studyDeck(chosenDeck)
        }
        return ::studyMyDeck
    }

    testSame(
        captureResults(
            helpTest(deckAsia),
            "",
            "",
            "",
            "",
        ),
        CapturedResult(
            0,
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 0 of 2 correctly",
        ),
        "empty - deck asia",
    )

    testSame(
        captureResults(
            helpTest(deckAsia),
            "",
            "yeah",
            "",
            "Yes",
        ),
        CapturedResult(
            2,
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 2 of 2 correctly",
        ),
        "both yes - deck asia",
    )

    testSame(
        captureResults(
            helpTest(deckAsia),
            "",
            "No",
            "",
            "nah",
        ),
        CapturedResult(
            0,
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 0 of 2 correctly",
        ),
        "both no - deck asia",
    )

    testSame(
        captureResults(
            helpTest(deckWorld),
            "",
            "No",
            "",
            "y",
            "ignore",
            "nope",
            "",
            "Yeah",
        ),
        CapturedResult(
            2,
            qEG,
            studyThink,
            aEG,
            studyCheck,
            qUS,
            studyThink,
            aUS,
            studyCheck,
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 2 of 4 correctly",
        ),
        "mix & ignore - deck world",
    )
}

// -----------------------------------------------------------------
// Final app!
// -----------------------------------------------------------------

// Now you just get to put this all together 💃

// TODO 1/1: Design the function chooseAndStudy, where you'll
//           follow the comments in the supplied code to leverage
//           your prior work to allow the user to choose a deck,
//           study it, and return the number of correct self-
//           reports.
//
//           Your deck options MUST include at least one from each
//           of the following categories...
//
//           - Coded by hand (such as an example in data design)
//           - Read from a file (ala readCardsFile)
//           - Generated by code (ala perfectSquares)
//
//           Note: while this is an interactive program, you won't
//                 directly use reactConsole - instead, just call
//                 the programs you already designed above :)
//
//           And of course, don't forget to test at least two runs
//           of this completed program!
//
//           (And, consider adding this to main so you can see the
//           results of all your hard work so far this semester!)
//

// lets the user choose a deck and study it,
// returning the number self-reported correct
fun chooseAndStudy(): Int {
    // 1. Construct a list of options
    // (ala the instructions above)
    val deckOptions =
        listOf(
            // TODO: at least...
            // deck from file via readCardsFile,
            Deck("File Deck", readCardsFile("example.txt")),
            // deck from code via perfectSquares
            Deck("Squares Deck", perfectSquares(5)),
            // deck hand-coded
            deckAsia,
            deckWorld,
        )

    // 2. Use chooseOption to let the user
    //    select a deck
    val deckChosen = chooseOption(deckOptions)

    // 3. Let the user study, return the
    //    number correctly answered
    return studyDeck(deckChosen)
}

@EnabledTest
fun testChooseAndStudy() {
    // makes a captureResults-friendly function :)
    fun helpTest(): () -> Int {
        fun chosenAndStudied(): Int {
            return chooseAndStudy()
        }
        return ::chosenAndStudied
    }

    testSame(
        captureResults(
            helpTest(),
            "3",
            "",
            "",
            "",
            "",
        ),
        CapturedResult(
            0,
            "1. File Deck",
            "2. Squares Deck",
            "3. Asian Capitals",
            "4. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: Asian Capitals",
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 0 of 2 correctly",
        ),
        "empty - deck asia",
    )

    testSame(
        captureResults(
            helpTest(),
            "2",
            "ignore",
            "yes",
            "IGNORE",
            "YES",
            "huh",
            "",
            "",
            "nope",
            "",
            "y",
        ),
        CapturedResult(
            3,
            "1. File Deck",
            "2. Squares Deck",
            "3. Asian Capitals",
            "4. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: Squares Deck",
            "1^2 = ?",
            studyThink,
            "1",
            studyCheck,
            "2^2 = ?",
            studyThink,
            "4",
            studyCheck,
            "3^2 = ?",
            studyThink,
            "9",
            studyCheck,
            "4^2 = ?",
            studyThink,
            "16",
            studyCheck,
            "5^2 = ?",
            studyThink,
            "25",
            studyCheck,
            "You answered 3 of 5 correctly",
        ),
        "mix & ignore - deck squares",
    )

    testSame(
        captureResults(
            helpTest(),
            "1",
            "",
            "y",
            "",
            "Y",
        ),
        CapturedResult(
            2,
            "1. File Deck",
            "2. Squares Deck",
            "3. Asian Capitals",
            "4. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: File Deck",
            "front 1",
            studyThink,
            "back 1",
            studyCheck,
            "front 2",
            studyThink,
            "back 2",
            studyCheck,
            "You answered 2 of 2 correctly",
        ),
        "all yes - deck file",
    )

    testSame(
        captureResults(
            helpTest(),
            "",
            "4",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
        ),
        CapturedResult(
            0,
            "1. File Deck",
            "2. Squares Deck",
            "3. Asian Capitals",
            "4. World Capitals",
            "",
            promptMenu,
            "1. File Deck",
            "2. Squares Deck",
            "3. Asian Capitals",
            "4. World Capitals",
            "",
            promptMenu,
            "Your chosen deck: World Capitals",
            qEG,
            studyThink,
            aEG,
            studyCheck,
            qUS,
            studyThink,
            aUS,
            studyCheck,
            qJP,
            studyThink,
            aJP,
            studyCheck,
            qKR,
            studyThink,
            aKR,
            studyCheck,
            "You answered 0 of 4 correctly",
        ),
        "repeat prompt, empty - deck world",
    )
}

// -----------------------------------------------------------------

fun main() {
}

runEnabledTests(this)
main()
