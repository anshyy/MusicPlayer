package com.example.musicplayer

import android.net.Uri

object VisualUtils {
    private val PREMIUM_MUSIC_IMAGES = listOf(
        "https://images.unsplash.com/photo-1614613535308-eb5fbd3d2c17?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1493225255756-d9584f8606e9?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1459749411177-042180ceea72?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1514525253361-bee8718a300a?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1420161900862-9a86fa1f5c79?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1508700115892-45ecd05ae2ad?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1510915361894-db8b60106cb1?q=80\u0026w=1000\u0026auto=format\u0026fit=crop",
        "https://images.unsplash.com/photo-1498038432885-c6f3f1b912ee?q=80\u0026w=1000\u0026auto=format\u0026fit=crop"
    )

    fun getPremiumImageForSeed(seed: String?): String {
        if (seed == null) return PREMIUM_MUSIC_IMAGES[0]
        val hash = seed.hashCode()
        val index = Math.abs(hash) % PREMIUM_MUSIC_IMAGES.size
        return PREMIUM_MUSIC_IMAGES[index]
    }
}
