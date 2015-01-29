package net.hogelab.android.androidui.musicplayer.entity;

import android.database.Cursor;
import android.provider.MediaStore;

import java.io.Serializable;

/**
 * Created by kobayasi on 2015/01/22.
 */
public class Track implements Serializable {
    private static final long serialVersionUID = 1L;

    public long             id;

    public String           data;
    public String           size;
    public String           title;

    public String           titleKey;
    public String           artist;
    public String           artistId;
    public String           artistKey;
    public String           album;
    public String           albumId;
    public String           albumKey;
    public String           track;
    public String           duration;

    public static final String[] FIELDS_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.TITLE_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ARTIST_KEY,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DURATION,
    };


    public Track(Cursor cursor){
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        size = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));

        titleKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE_KEY));
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        artistId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
        artistKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_KEY));
        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        albumId = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        albumKey = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_KEY));
        track = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
        duration = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
    }
}
