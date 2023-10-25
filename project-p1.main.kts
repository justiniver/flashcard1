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

val nameAsia = "Asian Capitals"
val nameWorld = "World Capitals"

// represents a Deck with a name as a string and a list of flashcards.
data class Deck(val name: String, val cards: List<FlashCard>)

val deckAsia = Deck(nameAsia, listOf(fcJP, fcKR))
val deckWorld = Deck(nameWorld, listOf(fcEG, fcUS, fcJP, fcKR))

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

val charSep = "|"

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
    fun renderDeckOptions(state: Int): String {
        return choicesToText(decks.map(::getDeckName))
    }

    fun transitionOptionChoice(
        ignoredState: Int,
        kbInput: String,
    ): Int {
        return keepIfValid(kbInput, decks.indices)
    }

    fun validChoiceEntered(state: Int): Boolean {
        return state in decks.indices
    }

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

// lets the user choose a deck and study it,
// returning the number self-reported correct
fun chooseAndStudy(): Int {
    val deckOptions =
        listOf(
            Deck("File Deck", readCardsFile("example.txt")),
            Deck("Squares Deck", perfectSquares(5)),
            deckAsia,
            deckWorld,
        )
    val deckChosen = chooseOption(deckOptions)
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

fun main() {
}

runEnabledTests(this)
main()
