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
    public String           title;
    public String           album;
    public String           artist;
    public long             albumId;
    public long             artistId;
    public long             duration;
    public long             track;

    public static final String[] FIELDS_PROJECTION = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
    };


    public Track(Cursor cursor){
        id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
        data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
        title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
        album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
        artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
        albumId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
        artistId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));
        duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
        track = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.TRACK));
    }
}
