package com.shreyas.fcmtesting.ui.fragment


import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

import com.shreyas.fcmtesting.R
import com.shreyas.fcmtesting.model.Content
import kotlinx.android.synthetic.main.fragment_content.*

/**
 * A simple [Fragment] subclass.
 */
class ContentFragment(private val contentDetails: Content?) : Fragment(), Player.EventListener {

    var mIvClose: ImageView? = null
    var mIvImage: ImageView? = null

    var playerView: PlayerView? = null
    var exoplayer: SimpleExoPlayer? = null
//    var playbackStateBuilder : PlaybackStateCompat.Builder? = null
//    var mediaSession: MediaSessionCompat? = null

    var dataSourceFactory: DataSource.Factory? = null
    var trackSelector: TrackSelector? = null
    var loadControl: LoadControl? = null
    var mediaSource: MediaSource? = null
    val adaptiveFactory: AdaptiveTrackSelection.Factory = AdaptiveTrackSelection.Factory()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_content, container, false)

        view.findViewById<ImageView>(R.id.iv_frag_close).setOnClickListener {
            closeFrag()
        }

        mIvImage = view.findViewById(R.id.iv_frag_image)
        playerView = view.findViewById(R.id.player_frag_exoplayer)

        contentType(view)

        return view
    }

    private fun contentType(view: View) {
        val type = contentDetails?.type
        if (type.equals("image")) {
            mIvImage?.visibility = View.VISIBLE
            playerView?.visibility = View.GONE
            setImageAndGif(view, contentDetails?.url)
        }else if (type.equals("gif")) {
            mIvImage?.visibility = View.VISIBLE
            playerView?.visibility = View.GONE
            setImageAndGif(view, contentDetails?.url)
        }else if(type.equals("video")){
            playerView?.visibility = View.VISIBLE
            mIvImage?.visibility = View.GONE
            initailizePlayer(view, contentDetails?.url)
        }else {
            Toast.makeText(activity?.applicationContext, "Not a command", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setImageAndGif(view: View, url: String?) {
            Log.d("player_123", "setImageAndGif: "+url+"    "+mIvImage?.isVisible)
        Glide.with(activity!!.applicationContext).load(url).into(mIvImage!!)
    }

    private fun closeFrag() {
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        exoplayer?.release()
    }

    private fun initailizePlayer(view: View, url: String?) {
        exoplayer = ExoPlayerFactory.newSimpleInstance(activity, DefaultTrackSelector())

        loadControl = DefaultLoadControl.Builder().setBufferDurationsMs(
            DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
            DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
            DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
        ).createDefaultLoadControl()

        trackSelector = DefaultTrackSelector(adaptiveFactory)
        dataSourceFactory = DefaultDataSourceFactory(
            activity,
            Util.getUserAgent(activity?.applicationContext, "exo")
        )
        val uri = Uri.parse(url)
        mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri)

        exoplayer?.prepare(mediaSource)
        playerView?.player = exoplayer

        exoplayer?.playWhenReady = true
        exoplayer?.addListener(this)


    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

        Log.d("player_123", "sad: "+playbackState)

        when (playbackState) {
            Player.STATE_ENDED -> {
                closeFrag()
            }
            Player.STATE_IDLE -> {

            }
            Player.STATE_BUFFERING -> {
                frag_pb.visibility = View.VISIBLE
            }
            Player.STATE_READY -> {
                frag_pb.visibility = View.GONE
                playerView?.player?.playWhenReady = true
            }

        }
    }

    override fun onPause() {
        super.onPause()

        exoplayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        exoplayer?.release()
    }


}
