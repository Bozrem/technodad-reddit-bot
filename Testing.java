import java.io.IOException;
import java.util.List;

public class Testing {
    public static void main(String[] args) throws IOException {
        List<String> posts = UserPullAPI.fetchUserPosts("mrtechnodad");
        for (String post : posts) System.out.println(post + "\n========================================================");
    }

}
