package itsme.com.moviecatalogue.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by its me on 11-May-16.
 */
public class MovieProvider extends ContentProvider {

    //Constants used in the URI matcher.
    static final int MOVIE = 100;
    static final int MOVIE_WITH_GENRE = 101;
    static final int MOVIE_WITH_ID = 102;
    static final int MOVIE_FAVOURITE = 104;
    private static final String mBuildWithMovieID = MovieContract.Movie.COLUMN_MOVIE_ID + " = ? ";
    private static final String mBuildWithMovieGenre = MovieContract.Movie.COLUMN_GENRE_IDS + " = ? ";
    private static final String mBuildWithMovieFave = MovieContract.Movie.COLUMN_IS_FAVOURITE + " = ?";
    private static UriMatcher matcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        //Based on the URL which is received it will decide what the system is actually looking for
        matcher.addURI(authority, MovieContract.PATH_MOVIE +
                MovieContract.Movie.MATCHER_MOVIE_ID + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_MOVIE +
                MovieContract.Movie.MATCHER_MOVIE_GENRE + "/#", MOVIE_WITH_GENRE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE +
                MovieContract.Movie.MATCHER_MOVIE_IS_FAV, MOVIE_FAVOURITE);
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = matcher.match(uri);

        switch (match) {
            case MOVIE_WITH_ID:
                return MovieContract.Movie.CONTENT_ITEM_BASE_TYPE;
            case MOVIE_WITH_GENRE:
                return MovieContract.Movie.CONTENT_DIR_BASE_TYPE;
            case MOVIE_FAVOURITE:
                return MovieContract.Movie.CONTENT_DIR_BASE_TYPE;
            case MOVIE:
                return MovieContract.Movie.CONTENT_DIR_BASE_TYPE;
            default:
                throw new UnsupportedOperationException();
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selectionReceived, String[] selectionArgsReceived, String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (matcher.match(uri)) {
            // "movie"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        projection,
                        selectionReceived,
                        selectionArgsReceived,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE_FAVOURITE: {
                String[] selectionArgs = new String[]{"true"};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        projection,
                        mBuildWithMovieFave,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE_WITH_ID: {
                String[] selectionArgs = new String[]{"true"};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.Movie.TABLE_NAME,
                        projection,
                        mBuildWithMovieID,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            case MOVIE_WITH_GENRE: {
                //Todo:BAsed on the way data is getting stored into the db.
                // Create a query which gets data ffor a particular genre.

            }

            default:
                throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri returnUri;

        long rowId = mOpenHelper.getWritableDatabase().insert(
                MovieContract.Movie.TABLE_NAME,
                null,
                values
        );

        //Check if the row has been updated and return the Uri to the data inserted.
        // If not updated properly throw and exception.
        if (rowId > 0) {
            returnUri = MovieContract.Movie.buildMovieUri(rowId);
        } else {
            throw new UnsupportedOperationException("Unknown Uri " + uri);
        }

        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // this makes delete all rows return the number of rows deleted.
        if (selection == null) selection = "1";
        int rowsDeleted = mOpenHelper.getWritableDatabase().delete(
                MovieContract.Movie.TABLE_NAME,
                selection,
                selectionArgs
        );

        //Because a null deletes all row.
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int rowsUpdated = mOpenHelper.getWritableDatabase().update(
                MovieContract.Movie.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted = 0;
        db.beginTransaction();
        try {
            for (ContentValues value : values) {
                long id = db.insert(MovieContract.Movie.TABLE_NAME,
                        null,
                        value);
                if (id != -1) {
                    rowsInserted++;
                }
            }
        } finally {
            db.endTransaction();
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
    }
}
