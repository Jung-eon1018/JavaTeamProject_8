package Final;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Word word)) return false;
        return Objects.equals(this.getKor(), word.getKor()) && Objects.equals(this.getEng(), word.getEng());
    }

    @Override
    public int hashCode() {
        return Objects.hash(eng, kor);
    }

    @Override
    public String toString() {
        String str = "=====================\n";
        str += "단어 : " + eng + "\n";
        str += "뜻 : " + kor + "\n";
        return str;
    }


}
