package Ywkung;

public class Word {
    private String eng;
    private String kor;

    public Word(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    public String getEng() { return eng; }
    public String getKor() { return kor; }

    @Override
    public String toString() {
        return eng + " : " + kor;
    }
}
