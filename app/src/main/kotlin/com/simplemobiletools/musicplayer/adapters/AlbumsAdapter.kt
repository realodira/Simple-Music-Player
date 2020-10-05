package com.simplemobiletools.musicplayer.adapters

import android.content.ContentUris
import android.net.Uri
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.simplemobiletools.commons.adapters.MyRecyclerViewAdapter
import com.simplemobiletools.commons.extensions.beGone
import com.simplemobiletools.commons.extensions.getFormattedDuration
import com.simplemobiletools.commons.views.MyRecyclerView
import com.simplemobiletools.musicplayer.R
import com.simplemobiletools.musicplayer.activities.SimpleActivity
import com.simplemobiletools.musicplayer.models.Album
import com.simplemobiletools.musicplayer.models.ListItem
import com.simplemobiletools.musicplayer.models.Song
import kotlinx.android.synthetic.main.item_album.view.*
import kotlinx.android.synthetic.main.item_song.view.*
import java.util.*

// we show both albums and individual tracks here
class AlbumsAdapter(activity: SimpleActivity, val items: ArrayList<ListItem>, recyclerView: MyRecyclerView, itemClick: (Any) -> Unit) :
        MyRecyclerViewAdapter(activity, recyclerView, null, itemClick) {

    private val ITEM_ALBUM = 0
    private val ITEM_TRACK = 1

    init {
        setupDragListener(true)
    }

    override fun getActionMenuId() = R.menu.cab_albums

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = if (viewType == ITEM_ALBUM) {
            R.layout.item_album
        } else {
            R.layout.item_song
        }

        return createViewHolder(layout, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.getOrNull(position) ?: return
        holder.bindView(item, true, true) { itemView, layoutPosition ->
            if (item is Album) {
                setupAlbum(itemView, item)
            } else {
                setupTrack(itemView, item as Song)
            }
        }
        bindViewHolder(holder)
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int): Int {
        val item = items[position]
        return if (item is Album) {
            ITEM_ALBUM
        } else {
            ITEM_TRACK
        }
    }

    override fun prepareActionMode(menu: Menu) {}

    override fun actionItemPressed(id: Int) {}

    override fun getSelectableItemCount() = items.size

    override fun getIsItemSelectable(position: Int) = true

    override fun getItemSelectionKey(position: Int) = (items.getOrNull(position))?.hashCode()

    override fun getItemKeyPosition(key: Int) = items.indexOfFirst { it.hashCode() == key }

    override fun onActionModeCreated() {}

    override fun onActionModeDestroyed() {}

    private fun getItemWithKey(key: Int): ListItem? = items.firstOrNull { it.hashCode() == key }

    private fun setupAlbum(view: View, album: Album) {
        view.apply {
            album_frame?.isSelected = selectedKeys.contains(album.hashCode())
            album_title.text = album.title
            album_title.setTextColor(textColor)

            val artworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(artworkUri, album.id.toLong())

            val options = RequestOptions()
                .transform(CenterCrop(), RoundedCorners(16))

            Glide.with(activity)
                .load(albumArtUri)
                .apply(options)
                .into(findViewById(R.id.album_image))
        }
    }

    private fun setupTrack(view: View, track: Song) {
        view.apply {
            song_frame?.isSelected = selectedKeys.contains(track.hashCode())
            song_title.text = track.title
            song_title.setTextColor(textColor)

            song_id.beGone()
            song_duration.text = track.duration.getFormattedDuration()
            song_duration.setTextColor(textColor)
        }
    }
}