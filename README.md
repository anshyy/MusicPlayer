# Music Player - Complete Android Application

A fully-functional music player application built with Kotlin and Android, featuring a modern Spotify-like interface with dark theme.

## Features

### 🎵 Core Playback Features
- **Play/Pause Control**: Easy-to-use play/pause button with visual feedback
- **Next/Previous**: Navigate between songs in your playlist
- **Shuffle Mode**: Random song playback with visual indicator
- **Repeat Mode**: Loop all songs with visual indicator
- **Seek Bar**: Drag to any position in the current song
- **Time Display**: Current time and total duration display
- **Song Position**: See current song number (e.g., "5 / 100")

### 📚 Library Management
- **Song Search**: Real-time search across all songs on device
- **Liked Songs**: Mark your favorite songs and view them in a dedicated section
- **Playlists**: Create custom playlists with images
  - Add new playlists with custom names and thumbnail images
  - Delete playlists easily
  - View songs in playlists
- **Organized Library View**: Separate tabs for home and library

### 🎨 User Interface
- **Dark Theme**: Beautiful dark Spotify-inspired design with green accents
- **Mini Player**: Floating mini player on home screen showing current song
- **Bottom Navigation**: Quick access between Home and Library tabs
- **Responsive Design**: Material Design components throughout
- **Visual Feedback**: Colors change for active modes (shuffle/repeat/like)

### 🔄 Permissions
- **Audio File Access**: Automatically requests READ_MEDIA_AUDIO permission (Android 13+)
- **Legacy Support**: Compatible with READ_EXTERNAL_STORAGE for older devices
- **Permission Handling**: Graceful handling of denied permissions

## Installation

### Prerequisites
- Android Studio with Kotlin support
- Android SDK 21 or higher
- Kotlin 1.5+

### Setup
1. Clone or download the project
2. Open in Android Studio
3. Grant necessary permissions when running on device
4. Build and run the app

## Project Structure

```
app/src/main/
├── java/com/example/musicplayer/
│   ├── MainActivity.kt           # Main hub with search and navigation
│   ├── PlayerActivity.kt         # Full-screen player with controls
│   ├── PlaylistDetailActivity.kt # Individual playlist view
│   ├── PlaylistEditActivity.kt   # Create/edit playlists
│   ├── MusicPlayerManager.kt     # Singleton for playback control
│   ├── SongAdapter.kt            # RecyclerView adapter for songs
│   ├── PlaylistAdapter.kt        # RecyclerView adapter for playlists
│   ├── Playlist.kt               # Data class for playlist
│   └── [Login/Register Activities]
└── res/
    ├── layout/
    │   ├── activity_main.xml
    │   ├── activity_player.xml
    │   ├── activity_playlist_detail.xml
    │   ├── activity_playlist_edit.xml
    │   ├── item_song.xml
    │   └── item_playlist.xml
    ├── menu/
    │   └── bottom_nav_menu.xml
    └── drawable/
        ├── ic_heart.xml
        ├── ic_shuffle.xml
        └── [other icons]
```

## Key Components

### MusicPlayerManager (Singleton)
Central manager for all music playback:
- `playSong(context, position)`: Start playing a song
- `playNext(context)`: Move to next song
- `playPrevious(context)`: Move to previous song
- `togglePlayPause()`: Toggle play/pause state
- `isShuffle`: Boolean for shuffle mode
- `isRepeat`: Boolean for repeat mode
- Callbacks for state changes

### MainActivity
- Song discovery and loading
- Search functionality
- Liked songs management
- Playlist management
- Mini player display

### PlayerActivity
- Full-screen playback control
- Progress tracking with seek bar
- Shuffle/repeat toggle with visual indicators
- Like button for quick favoriting
- Song position display

## Permissions Required

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
```

## Dependencies

- AndroidX Core KTX
- AndroidX AppCompat
- Material Components
- AndroidX ConstraintLayout
- AndroidX RecyclerView
- AndroidX Activity

## How to Use

### Playing Music
1. Open the app (grant permissions if prompted)
2. All songs on your device are automatically loaded
3. Tap any song to play it
4. Use play/pause button to control playback
5. Swipe to next/previous songs

### Searching Songs
1. Use the search bar at the top
2. Type song name to filter in real-time
3. Tap to play filtered songs

### Liking Songs
1. Tap the heart icon next to any song (or in player)
2. Liked songs appear in Library → Liked Songs
3. Tap heart again to unlike

### Creating Playlists
1. Go to Library tab
2. Tap the "+" button next to Playlists
3. Enter playlist name
4. (Optional) Tap to add playlist image
5. Save playlist
6. Tap playlist to view details

### Using Playback Controls
- **Shuffle** (✓ icon): Randomize song order, tap to toggle (green when active)
- **Repeat** (↻ icon): Loop all songs, tap to toggle (green when active)
- **Like** (♥ icon): Mark favorite songs for quick access

## Future Enhancements

- Song-to-playlist drag & drop
- Playlist sharing
- Song duration sorting
- Genre-based filtering
- Equalizer settings
- Background playback optimization
- Notification player controls
- Widget support
- Scrobbling integration
- Lyrics display

## License

[Specify your license here]

## Support

For issues or feature requests, please create an issue in the repository.

---

**Note**: This app plays music files stored locally on your device. Ensure you have music files downloaded on your Android device for the app to display them.

