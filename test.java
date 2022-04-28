import java.util.ArrayList;

public class test {

    public static class A {
        public A() {

        }
    }

    public static class B extends A {
        public B() {

        }
    }

    public static void main(String[] args) {
        ArrayList<A> objs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            objs.add(new A());
        }

        for (int i = 0; i < 5; i++) {
            objs.add(new B());
        }
    }
}
