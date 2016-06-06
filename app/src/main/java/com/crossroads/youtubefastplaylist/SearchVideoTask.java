package com.crossroads.youtubefastplaylist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crossroads on 5/31/16.
 */
public class SearchVideoTask extends AsyncTask<String, Void, List<VideoItem>> {

    private final Context mContext;
    private static YouTube youtube;

    public SearchVideoTask(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected List<VideoItem> doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        /*String query = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String dataJsonStr = null;

        String part = "snippet";
        String key = "AIzaSyDwBlteZnIR9QZL6eyxELOgRQARORPt7L0";
        String type = "video";
        String order = "viewCount";
        int maxResults = 5;

        try {

            final String BASE_URL = "https://www.googleapis.com/youtube/v3/search?";
            final String QUERY_PARAM = "q";
            final String PART_PARAM = "part";
            final String TYPE_PARAM = "type";
            final String ORDER_PARAM = "order";
            final String API_KEY_PARAM = "key";
            final String MAXRESULT_PARAM = "maxResults";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(PART_PARAM, part)
                    .appendQueryParameter(TYPE_PARAM, type)
                    .appendQueryParameter(ORDER_PARAM, order)
                    .appendQueryParameter(API_KEY_PARAM, key)
                    .appendQueryParameter(MAXRESULT_PARAM, Integer.toString(maxResults))
                    .build();

            Log.i("uri", builtUri.toString());

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int sc = urlConnection.getResponseCode();
            Log.i("response code ", Integer.toString(sc));

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            dataJsonStr = buffer.toString();
            Log.i("json", dataJsonStr);

        } catch (Exception e) {
            e.printStackTrace();
        }*/

        try {
            // This object is used to make YouTube Data API requests. The last
            // argument is required, but since we don't need anything
            // initialized when the HttpRequest is initialized, we override
            // the interface and provide a no-op function.
            youtube = new YouTube.Builder(new NetHttpTransport(),
                    new JacksonFactory(), new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest hr) throws IOException {}
            }).setApplicationName("YoutubeFastPlaylist").build();

            // Prompt the user to enter a query term.
            String queryTerm = params[0];

            // Define the API request for retrieving search results.
            YouTube.Search.List search = youtube.search().list("id,snippet");

            // Set your developer key from the Google Developers Console for
            // non-authenticated requests. See:
            // https://console.developers.google.com/
            String apiKey = "AIzaSyDwBlteZnIR9QZL6eyxELOgRQARORPt7L0";
            search.setKey(apiKey);
            search.setQ(queryTerm);

            // Restrict the search results to only include videos. See:
            // https://developers.google.com/youtube/v3/docs/search/list#type
            search.setType("video");

            // To increase efficiency, only retrieve the fields that the
            // application uses.
            search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
            search.setMaxResults(5l);

            // Call the API and print results.
            SearchListResponse searchResponse = search.execute();
            List<SearchResult> searchResultList = searchResponse.getItems();

            Log.i("response ", Integer.toString(searchResultList.size()));

            List<VideoItem> items = new ArrayList<VideoItem>();
            for(SearchResult result:searchResultList){
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
//                item.setDescription(result.getSnippet().getDescription());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
            return items;

        } catch (GoogleJsonResponseException e) {
            System.err.println("There was a service error: " + e.getDetails().getCode() + " : "
                    + e.getDetails().getMessage());
        } catch (IOException e) {
            System.err.println("There was an IO error: " + e.getCause() + " : " + e.getMessage());
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
