package itsme.com.moviecatalogue.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by its me on 11-May-16.
 */
public class MovieProvider extends ContentProvider {

    //Constants used in the URI matcher.
    //static final int MOVIE = 100;
    static final int MOVIE_WITH_GENRE = 101;
    static final int MOVIE_WITH_ID = 102;
    static final int MOVIE_TYPE = 103;
    static final int MOVIE_FAVOURITE = 104;
    private static final String mBuildWithMovieID = MovieContract.Movie.COLUMN_MOVIE_ID + " = ? ";
    private static final String mBuildWithMovieGenre = MovieContract.Movie.COLUMN_GENRE_IDS + " = ? ";
    private static final String mBuildWithMovieType = MovieContract.Movie.COLUMN_TYPE + " = ?";
    private static final String mBuildWithMovieFave = MovieContract.Movie.COLOUMN_IS_FAVOURATE + " = ?";
    private static UriMatcher matcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
