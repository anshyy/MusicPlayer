# 🎵 Music Player App - Visual Architecture Guide

## 📱 Screen Flow Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                    APP LAUNCH                               │
│                 (LoginActivity)                             │
│                                                             │
│  Optional: User authentication and login                    │
└──────────────────────┬──────────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────────┐
│                   MAIN ACTIVITY                             │
│            (Home Screen - Primary View)                     │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │ 🎵 Music Library                                      │  │
│  │ ┌─────────────────────────────────────────────────┐   │  │
│  │ │ 🔍 [Search your music...]                       │   │  │
│  │ └─────────────────────────────────────────────────┘   │  │
│  │                                                       │  │
│  │ 🎵 Song 1                               ♡  (gray)   │  │
│  │ 🎵 Song 2                               ♡  (gray)   │  │
│  │ 🎵 Song 3                               ♡  (gray)   │  │
│  │ 🎵 Song 4                               ♡  (gray)   │  │
│  │                                                       │  │
│  │ (tap song → play) (tap heart → like)                │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐  │
│  │  🎚️ Now Playing: "Song Name"              ⏸          │  │
│  │  (Mini Player - tap to open full player)             │  │
│  └───────────────────────────────────────────────────────┘  │
│                                                             │
│  [🏠 HOME]              [📚 LIBRARY]                        │
│  (Current Tab)          (Switchable)                        │
└──────────────────┬──────────────────────────────────────────┘
                   │
        ┌──────────┴──────────┐
        │                     │
        ▼                     ▼
┌──────────────────┐  ┌──────────────────────────────┐
│ TAP SONG         │  │ SWITCH TO LIBRARY TAB        │
└────────┬─────────┘  └──────────┬───────────────────┘
         │                       │
         ▼                       ▼
    ┌─────────────────────────────────────────┐
    │         PLAYER ACTIVITY                 │
    │     (Full-Screen Player)                │
    │                                         │
    │  NOW PLAYING                            │
    │  ┌─────────────────────────────────┐   │
    │  │                                 │   │
    │  │         🎵 Album Art           │   │
    │  │      (Placeholder Icon)         │   │
    │  │                                 │   │
    │  └─────────────────────────────────┘   │
    │                                         │
    │  Song Name (Scrolling if long)         │
    │  5 / 100  (Position)                   │
    │                                         │
    │  ┌─────────────────────────────────┐   │
    │  │[====●====] Seek Bar            │   │
    │  │ 2:34        / 5:00  (Time)     │   │
    │  └─────────────────────────────────┘   │
    │                                         │
    │  ♥  ✓  ◀  ⏸  ▶  ↻                   │
    │  Like Shuf Prev Play Next Repeat       │
    │  (Colors change when active)           │
    │                                         │
    │  ✓=Green (Shuffle ON)                  │
    │  ↻=Green (Repeat ON)                   │
    │  ♥=Green (Song Liked)                  │
    └─────────────────────────────────────────┘
         │
         │ (Back button or tap mini player)
         ▼
    Back to MainActivity

┌──────────────────────────────────────────────┐
│     LIBRARY VIEW (Inside MainActivity)       │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  📝 Playlists          [➕]          │   │
│  │                                      │   │
│  │  📷 My Playlist 1                    │   │
│  │  📷 My Playlist 2                    │   │
│  │  📷 Study Playlist                   │   │
│  └──────────────────────────────────────┘   │
│                                              │
│  ┌──────────────────────────────────────┐   │
│  │  💚 Liked Songs                      │   │
│  │                                      │   │
│  │  🎵 Favorite Song 1         ♡(green)│   │
│  │  🎵 Favorite Song 2         ♡(green)│   │
│  │  🎵 Favorite Song 3         ♡(green)│   │
│  └──────────────────────────────────────┘   │
└──────────────────────────────────────────────┘
         │                    │
         │                    ▼
         │            ┌─────────────────────────┐
         │            │ TAP LIKED SONG          │
         │            │ → Play from Liked list  │
         │            └──────────┬──────────────┘
         │                       │
         │                       ▼
         │              PlayerActivity
         │
         ▼
    ┌─────────────────────────┐
    │ TAP PLAYLIST            │
    │ → Play from Playlist    │
    └──────────┬──────────────┘
               │
               ▼
        PlayerActivity


    ┌──────────────────────────┐
    │ TAP [➕] ADD PLAYLIST     │
    │                          │
    └──────────┬───────────────┘
               │
               ▼
    ┌──────────────────────────────────┐
    │  PlaylistEditActivity            │
    │                                  │
    │  Edit Playlist                   │
    │                                  │
    │  ┌──────────────────────────┐    │
    │  │ 📷                       │    │
    │  │ [Tap to Change Image]    │    │
    │  └──────────────────────────┘    │
    │                                  │
    │  ┌──────────────────────────┐    │
    │  │ Playlist Name            │    │
    │  │ [My Awesome Playlist]    │    │
    │  └──────────────────────────┘    │
    │                                  │
    │  [Save Changes] (Green Button)   │
    │                                  │
    └──────────────────────────────────┘
               │
               ▼
    Playlist Added
    (Appears in Playlists list)
```

---

## 🏗️ Component Architecture

```
┌──────────────────────────────────────────────────────┐
│                 MusicPlayerManager                   │
│              (Singleton - Core Engine)               │
│                                                      │
│  Properties:                                         │
│  ├─ currentSongList: List<String>                   │
│  ├─ currentSongPaths: List<String>                  │
│  ├─ currentPosition: Int                             │
│  ├─ isShuffle: Boolean                              │
│  ├─ isRepeat: Boolean                               │
│  └─ mediaPlayer: MediaPlayer?                       │
│                                                      │
│  Methods:                                            │
│  ├─ playSong(context, position)                     │
│  ├─ playNext(context)                               │
│  ├─ playPrevious(context)                           │
│  ├─ togglePlayPause()                               │
│  └─ getMediaPlayer()                                │
│                                                      │
│  Callbacks:                                          │
│  ├─ onSongChanged: (position) -> Unit               │
│  └─ onPlaybackStatusChanged: (isPlaying) -> Unit    │
└──────────────────────────────────────────────────────┘
              ▲                    ▲
              │                    │
              │                    │
    ┌─────────┴──────┐      ┌──────┴──────────┐
    │                │      │                 │
    ▼                ▼      ▼                 ▼
┌─────────────┐ ┌──────────────────┐ ┌──────────────────┐
│ MainActivity│ │ PlayerActivity   │ │PlaylistEditAct   │
│             │ │                  │ │                  │
│ • Search    │ │ • Play/Pause     │ │ • Create list    │
│ • Display   │ │ • Seek           │ │ • Set name       │
│ • Like      │ │ • Shuffle/Repeat │ │ • Add image      │
│ • Navigate  │ │ • Show controls  │ │ • Save           │
└──────┬──────┘ └─────────┬────────┘ └──────────────────┘
       │                  │
       ▼                  ▼
    ┌────────────────────────────┐
    │ Adapters                   │
    │                            │
    │ • SongAdapter              │
    │ • PlaylistAdapter          │
    │                            │
    │ (Display data in UI)       │
    └────────────────────────────┘
       │
       ▼
    ┌────────────────────────────┐
    │ Data Models                │
    │                            │
    │ • ArrayList<String> songs  │
    │ • HashSet<String> liked    │
    │ • ArrayList<Playlist> list │
    │ • Playlist data class      │
    └────────────────────────────┘
```

---

## 🔄 Data Flow Diagram

```
┌─────────────────────────────────────────────────────────┐
│              DEVICE STORAGE                            │
│         (MediaStore.Audio.Media)                       │
│                                                         │
│  /sdcard/Music/Song1.mp3                               │
│  /sdcard/Music/Song2.mp3                               │
│  /storage/emulated/.../Song3.mp3                       │
└────────────────────┬────────────────────────────────────┘
                     │
                     │ Query + Read
                     ▼
┌─────────────────────────────────────────────────────────┐
│           MainActivity::loadSongs()                     │
│                                                         │
│  Extract TITLE & DATA from cursor                      │
└────────────────────┬────────────────────────────────────┘
                     │
            ┌────────┴────────┐
            ▼                 ▼
    ┌───────────────┐  ┌──────────────┐
    │ songList      │  │ songPaths    │
    │ (Names)       │  │ (File paths) │
    │               │  │              │
    │ "Song 1"      │  │ "/path/to/1" │
    │ "Song 2"      │  │ "/path/to/2" │
    │ "Song 3"      │  │ "/path/to/3" │
    └───────┬───────┘  └──────┬───────┘
            │                 │
            └────────┬────────┘
                     │
                     ▼
    ┌──────────────────────────────┐
    │ SongAdapter                  │
    │ (Display in RecyclerView)    │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │ UI RecyclerView              │
    │ - Shows all songs            │
    │ - Filterable by search       │
    │ - Likeable with heart icon   │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │ USER INTERACTION             │
    │                              │
    │ Tap song → Get position      │
    │ Tap heart → Add to likedSet  │
    │ Type search → Filter list    │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │ MusicPlayerManager           │
    │ Set currentSongList/Paths    │
    │ Call playSong(pos)           │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │ MediaPlayer                  │
    │ setDataSource(Uri)           │
    │ prepare()                    │
    │ start()                      │
    └────────────┬─────────────────┘
                 │
                 ▼
    ┌──────────────────────────────┐
    │ AUDIO OUTPUT                 │
    │ (Device Speaker/Headphones)  │
    │                              │
    │ 🔊 Song playing...           │
    └──────────────────────────────┘
```

---

## 🎨 UI Layout Hierarchy

```
MainActivity (FrameLayout)
│
├─ llHeader (LinearLayout - Top)
│  └─ Search Bar
│
├─ contentFrame (FrameLayout - Middle)
│  │
│  ├─ homeView (LinearLayout - VISIBLE)
│  │  └─ recyclerView (Song List)
│  │
│  └─ libraryView (LinearLayout - GONE initially)
│     ├─ rvPlaylists (Playlists)
│     └─ rvLikedSongs (Liked Songs)
│
└─ playerContainer (LinearLayout - Bottom)
   ├─ miniPlayer (CardView)
   │  └─ (Song name, play/pause button)
   │
   └─ bottomNavigation (BottomNavigationView)
      ├─ nav_home
      └─ nav_library


PlayerActivity (RelativeLayout - Full Screen)
│
├─ tvNowPlaying (Text - Top)
│
├─ cvAlbumArt (CardView - Center)
│  └─ Album Art Placeholder (🎵)
│
├─ tvSongName (Text - Song Title)
│
├─ tvSongPosition (Text - Position)
│
├─ seekBar (SeekBar - Progress)
│
├─ tvCurrentTime + tvTotalTime (Time Display)
│
└─ llControls (LinearLayout - Bottom)
   ├─ btnLike (Like Button)
   ├─ btnShuffle (Shuffle Button)
   ├─ btnPrev (Previous)
   ├─ btnPlay (Play/Pause - Center)
   ├─ btnNext (Next)
   └─ btnRepeat (Repeat Button)
```

---

## 🔀 Playback State Transitions

```
┌─────────────────┐
│   STOPPED       │ (Initial state)
└────────┬────────┘
         │
         │ playSong(pos)
         ▼
┌─────────────────────────┐
│   PLAYING               │◄─────────┐
│ (Song is playing)       │          │
└────────┬────────┬───────┘          │
         │        │                  │
    pause│        │play next/prev    │
         │        │                  │
         ▼        ▼                  │
    ┌────────────────┐         ┌─────────┐
    │   PAUSED       │         │ NEXT    │
    │ (Music paused) │         │ Previous│
    └────────┬───────┘         └────┬────┘
             │                      │
             │ play()               │
             └──────────┬───────────┘
                        │
                        ▼
    Auto advances on completion
    (unless repeat=one)
    
Shuffle Mode:
  OFF → playNext goes to (currentPos + 1) % size
  ON  → playNext goes to random position

Repeat Mode:
  OFF → Stops at end of list
  ON  → Loops back to start
```

---

## 🎯 Permission Flow

```
App Launch
│
├─ Check hasPermission()
│  │
│  ├─ Android 13+ ? Check READ_MEDIA_AUDIO
│  └─ Android 12- ? Check READ_EXTERNAL_STORAGE
│
├─ Permission Granted?
│  │
│  ├─ YES → loadSongs() → Display library
│  │
│  └─ NO → requestPermission()
│     │
│     ├─ User grants → onRequestPermissionsResult()
│     │  └─ loadSongs() → Display library
│     │
│     └─ User denies → Show Toast "Permission denied!"
│        └─ Empty library shown
```

---

## 📊 State Management

```
MainActivity State:
├─ songList: ArrayList<String>        (All songs)
├─ songPaths: ArrayList<String>       (All paths)
├─ filteredSongList: ArrayList        (Search results)
├─ filteredSongPaths: ArrayList       (Search paths)
├─ likedSongPaths: HashSet<String>    (Liked songs)
└─ playlists: ArrayList<Playlist>     (User playlists)

MusicPlayerManager State:
├─ currentSongList: List<String>      (Now playing list)
├─ currentSongPaths: List<String>     (File paths)
├─ currentPosition: Int               (Now playing index)
├─ isShuffle: Boolean                 (Toggle state)
├─ isRepeat: Boolean                  (Toggle state)
└─ mediaPlayer: MediaPlayer?          (Audio engine)

UI State:
├─ homeView: Visible/Gone
├─ libraryView: Visible/Gone
├─ miniPlayer: Visible/Gone
└─ bottomNav: Selected item
```

---

**Visual Architecture Complete! ✅**

*Use this guide to understand how the app flows and communicates.*

