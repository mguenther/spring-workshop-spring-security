package workshop.spring.security.resources.data;

public class EmployeeViews {
    public static class Public {
    }

    public static class Internal extends Public {

    }

    public static class Accounting extends Internal {

    }

    public static class Management extends Accounting {

    }
}
