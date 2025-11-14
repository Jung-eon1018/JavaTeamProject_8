package Jjh;

public class Word {
    String kor;
    String eng;

    public Word(String eng, String kor) {
        this.eng = eng;
        this.kor = kor;
    }

    public String getKor() {
        return kor;
    }

    public String getEng() {
        return eng;
    }

    public void setKor(String kor) {
        this.kor = kor;
    }
}
