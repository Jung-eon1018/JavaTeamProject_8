package LJE;

public class TestMain {
    public static void main(String[] args) {
        //작동 Test
        WrongNotes wrong = new WrongNotes();

        wrong.markWrong("dog","강아지");
        wrong.markWrong("cat","고양이");

        wrong.save("data/wrong.txt");

        wrong.printAll();
    }
}
