import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPullAPI {

    public interface RedditAPI {
        @GET("user/{username}/submitted/.json")
        Call<JsonElement> getPosts(
                @Path("username") String username,
                @Query("limit") int limit,
                @Query("after") String after
        );
    }

    public static List<String> fetchUserPosts(String username) throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RedditAPI api = retrofit.create(RedditAPI.class);
        String after = null;
        List<String> bodies = new ArrayList<>(); // List of body texts

        for (int i = 0; i < 10; i++) {  // Assume 10 pages to get 1000 posts (100 per page)
            Call<JsonElement> call = api.getPosts(username, 100, after);
            JsonElement response = call.execute().body();

            if (response != null) {
                JsonObject data = response.getAsJsonObject()
                        .get("data").getAsJsonObject();
                JsonArray children = data.get("children").getAsJsonArray();

                for (JsonElement child : children) {
                    JsonObject post = child.getAsJsonObject()
                            .get("data").getAsJsonObject();
                    bodies.add(post.get("selftext").getAsString());
                }

                after = data.get("after").isJsonNull() ? null : data.get("after").getAsString();
                if (after == null) {
                    break;  // No more posts to fetch
                }
            }
        }

        return bodies;
    }
}
