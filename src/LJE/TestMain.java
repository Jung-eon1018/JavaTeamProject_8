package LJE;

public class TestMain {
    public static void main(String[] args) {
        //작동 Test
        WrongNotes wrong = new WrongNotes();
        wrong.loadFromFile("data/wrong.txt");
        wrong.printAll();
        CommonWordsNotes common = new CommonWordsNotes();
        common.loadFromFile("data/common.txt");
        wrong.printAll();

        common.retainWords();
        common.printRetainWords();

    }
}
