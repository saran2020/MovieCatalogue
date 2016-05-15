package itsme.com.moviecatalogue.Data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by its me on 11-May-16.
 */
public class MovieProvider extends ContentProvider {

    private static UriMatcher matcher = buildUriMatcher();
    private MovieDBHelper mOpenHelper;

    //Constants used in the URI matcher.
    static final int MOVIE = 100;
    static final int MOVIE_WITH_GENRE = 101;
    static final int MOVIE_WITH_ID = 102;
    static final int MOVIE_FAVOURITE = 104;

    private static final String mBuildWithMovieID = MovieContract.Movie.COLUMN_MOVIE_ID + " = ? ";
    private static final String mBuildWithMovieGenre = MovieContract.Movie.COLUMN_GENRE_IDS + " = ? ";
    private static final String mBuildWithMovieFave = MovieContract.Movie.COLUMN_IS_FAVOURITE + " = ?";
    // The coloums to be fetched for the movie.
    // To be used in bulk insert to compare the data before updating the database.
    private final static String MOVIE_COLUMN[] = {
            MovieContract.Movie._ID,
            MovieContract.Movie.COLUMN_MOVIE_ID,
            MovieContract.Movie.COLUMN_RATING,
            MovieContract.Movie.COLUMN_POPULARITY
    };

    // These indices are tied to MOVIE_COLUMN. If FORECAST_COLUMNS changes, these
    // must change.
    static final int ID = 0;
    static final int COLUMN_MOVIE_ID = 1;
    static final int COLUMN_RATING = 2;
    static final int COLUMN_POPULARITY = 3;

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
            // "/movie"
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

            //"/fav"
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

            //"/movie/<id>"
            case MOVIE_WITH_ID: {
                String[] selectionArgs = new String[]{MovieContract.Movie.getIdFromUri(uri)};
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

            //"/genre/<genre_id>"
            case MOVIE_WITH_GENRE: {
                //Todo:Based on the way data is getting stored into the db.
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

        //Todo: make content provider delete data
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

    /**
     * Gets all the data from the Database. And then before inserting it
     * checks if the movie is already preset. If the movie is not present
     * then it will update it into the database with all the details of
     * the movie. If movie is already presented it will only change few
     * fields which are subject to change.
     *
     * @param uri    URI of the table that needs to be updated.
     * @param values A content value array that consist of all the details
     *               that needs to be updated
     * @return It returns the number of rows that has been updated.
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rowsInserted = 0;
        int rowsUpdated = 0;
        db.beginTransaction();

        // Gets the all the database rows to check which row needs to be updated.
        Cursor cursor = db.query(MovieContract.Movie.TABLE_NAME,
                MOVIE_COLUMN,
                null,
                null,
                null,
                null,
                null);

        // If we get the data from cursor means that database has already been initiated.
        // If not we have to initiate the database.
        //
        // If we are able to fetch any data.
        if (cursor.moveToFirst()) {
            try {
                for (ContentValues value : values) {

                    //We move cursor to point to next row in a do while loop.
                    do {
                        String cursorMovieID = Integer.toString(cursor.getInt(COLUMN_MOVIE_ID));
                        String[] whereArgs = {
                                value.getAsString(MovieContract.Movie.COLUMN_MOVIE_ID)
                        };

                        //Check if the movie_id from cursor matches the data fetched from cloud.
                        if (cursorMovieID.equalsIgnoreCase(whereArgs[0])) {

                            String whereClause = MovieContract.Movie.COLUMN_MOVIE_ID + " =? ";
                            //Remove all unnecessary content values so that we can update only what are subject to change.
                            value.remove(MovieContract.Movie.COLUMN_POSTER);
                            value.remove(MovieContract.Movie.COLUMN_GENRE_IDS);
                            value.remove(MovieContract.Movie.COLUMN_POSTER_PATH);
                            value.remove(MovieContract.Movie.COLUMN_IS_FAVOURITE);
                            value.remove(MovieContract.Movie.COLUMN_OVERVIEW);
                            value.remove(MovieContract.Movie.COLUMN_TITLE);
                            value.remove(MovieContract.Movie.COLUMN_RELEASE_DATE);

                            //Update the database and if done increase the rows updated by 1.
                            long id = db.update(MovieContract.Movie.TABLE_NAME,
                                    value,
                                    whereClause,
                                    whereArgs);

                            if (id != -1) {
                                rowsUpdated++;
                            }
                            //If the movie id dont match then update all the values of new movie
                        } else {
                            long id = db.insert(MovieContract.Movie.TABLE_NAME,
                                    null,
                                    value);
                            if (id != -1) {
                                rowsInserted++;
                            }
                        }
                    } while (cursor.moveToNext());
                }
            } finally {
                db.endTransaction();
            }
            // If the cursor is empty directly update the database.
        } else {
            Log.v("Content Provider: ", "fetching data failed");
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
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsInserted;
    }
}