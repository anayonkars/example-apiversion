
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {
    public static void main(String[] args) {
        Pattern pattern = Pattern.compile("/v(\\d+)/");
        test(pattern, "/example/v3/greeting");
        test(pattern, "example/v3/greeting");
        test(pattern, "/example/v3/");
        test(pattern, "/v3/greeting");
    }

    private static void test(Pattern pattern, String path) {
        Matcher matcher = pattern.matcher(path);
        if (matcher.find()) {
            String newPath = matcher.replaceFirst("/");
            System.out.println("Rewrite '" + path + "' -> '" + newPath + "'");
        } else {
            System.out.println("No match for '" + path + "'");
        }
    }
}
