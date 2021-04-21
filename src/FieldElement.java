import java.util.Objects;

public class FieldElement {
    int num;
    int prime;
    public FieldElement(int num, int prime) {
        if (num>= prime || num <0) {
            System.out.println(" Element "+num+ " not in 0.."+(prime-1));
            System.exit(-1);
        }
        this.num =num;
        this.prime = prime;
    }

    @Override
    public String toString() {
        return "FieldElement{" +
                "num=" + num +
                ", prime=" + prime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldElement that = (FieldElement) o;
        return num == that.num && prime == that.prime;
    }

    @Override
    public int hashCode() {
        return Objects.hash(num, prime);
    }
}
