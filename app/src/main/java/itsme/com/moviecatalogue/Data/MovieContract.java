package itsme.com.moviecatalogue.Data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by its me on 08-May-16.
 */
public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "itsme.com.moviecatalogue.";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";

    //Actual contract for our Movie Database
    public static final class Movie implements BaseColumns {

        public static final Uri FINAL_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        //This DIR type is for a particular gener type filter.
        public static final String CONTENT_DIR_BASE_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_BASE_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        //Table Name
        public static final String TABLE_NAME = "movie";

        //Columns for the table
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_IMAGE = "image";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_data";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_GENRE_IDS = "genres";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(FINAL_URI, id);
        }

        public static Uri buildGenre(int id) {
            return ContentUris.withAppendedId(FINAL_URI, id);
        }
    }
}
