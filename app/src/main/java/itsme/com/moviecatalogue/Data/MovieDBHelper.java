package itsme.com.moviecatalogue.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by its me on 08-May-16.
 * This the class which is being helping us in managing the Database.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movie_catalogue.db";
    private static final int DATABASE_VERSION = 4;

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a table to hold movies.  A movie column consists of the string supplied in the
        // Movie id, title, image, overview, release_date, rating, type, genre ids.
        final String CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.Movie.TABLE_NAME + " (" +
                MovieContract.Movie._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.Movie.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.Movie.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_POSTER + " BLOB NOT NULL, " +
                MovieContract.Movie.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_RATING + " REAL NOT NULL, " +
                MovieContract.Movie.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.Movie.COLUMN_GENRE_IDS + " TEXT NOT NULL, " +
                MovieContract.Movie.COLUMN_IS_FAVOURITE + " INTEGER NOT NULL, " +
                MovieContract.Movie.COLUMN_TRAILERS + " TEXT " +
                " );";

        db.execSQL(CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        db.execSQL(" DROP TABLE IF EXISTS " + MovieContract.Movie.TABLE_NAME);
        onCreate(db);
    }
}
