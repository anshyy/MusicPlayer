# 🎶 Music Player App - Quick Start (5 Minutes)

## Step 1: Open in Android Studio (1 min)
1. Open Android Studio
2. Click **File → Open**
3. Navigate to `/Users/ansh/AndroidStudioProjects/MusicPlayer`
4. Click **Open**
5. Wait for Gradle sync to complete

## Step 2: Build the Project (2 min)
```
Build → Rebuild Project
```
Or press `Ctrl+B` / `Cmd+B`

## Step 3: Run the App (2 min)
1. Click the green **Run** button (or press `Shift + F10`)
2. Select your device or emulator
3. Click **OK**
4. Wait for installation

## Step 4: Grant Permission
When app launches:
1. Tap "ALLOW" when permission dialog appears
2. App loads all songs from your device

## Step 5: Start Playing! 🎵
1. Tap any song to play
2. Use controls to manage playback
3. Go to "Library" tab to see liked songs and playlists

---

## 🎮 Basic Controls

| Action | How |
|--------|-----|
| **Play/Pause** | Tap center green button |
| **Next Song** | Tap right arrow |
| **Previous Song** | Tap left arrow |
| **Shuffle** | Tap shuffle icon (turns green when on) |
| **Repeat** | Tap repeat icon (turns green when on) |
| **Like Song** | Tap heart icon |
| **Seek** | Drag the progress bar |
| **Search** | Type in search box on home |

---

## 📁 App Structure at a Glance

```
🎵 Music Player App
├─ 🏠 HOME (Default view)
│  ├─ 🔍 Search bar
│  ├─ 🎵 Song list
│  └─ 🎚️  Mini player
│
└─ 📚 LIBRARY
   ├─ 💚 Liked Songs
   └─ 📝 Playlists
      └─ ➕ Create new
```

---

## 🎯 Common Tasks

### Play a Specific Song
1. Go to **Home** tab
2. Scroll to find song
3. Tap the song
4. It starts playing immediately

### Search for a Song
1. At the top, tap search bar
2. Type song name
3. Results filter in real-time
4. Tap result to play

### Like Your Favorite Songs
1. Find song in list
2. Tap heart icon ♥️
3. Heart turns green
4. Go to **Library → Liked Songs** to see all

### Create a Playlist
1. Go to **Library** tab
2. Tap **"+"** next to "Playlists"
3. Enter playlist name
4. (Optional) Tap to add image
5. Tap **"Save Changes"**
6. Playlist appears in list

### Enable Shuffle Mode
1. Open player (tap mini player or song)
2. Tap shuffle icon (✓)
3. Icon turns green
4. Tap next → plays random song

---

## 🐛 Troubleshooting

### "No songs found"
- ✅ Put MP3 files on your device
- ✅ Check permission is granted
- ✅ Verify files in Music app

### App crashes when playing
- ✅ Ensure file isn't corrupted
- ✅ Try another song
- ✅ Check file format (.mp3, .wav, .flac)

### No sound coming out
- ✅ Check device volume
- ✅ Verify audio file has content
- ✅ Restart app

### Permission denied
- ✅ Go to Settings → Apps → Music Player
- ✅ Tap Permissions
- ✅ Enable "Audio Files" or "Storage"

---

## 🎨 UI Overview

### Player Screen
```
┌─────────────────────────┐
│   NOW PLAYING           │ ← Header
├─────────────────────────┤
│                         │
│      🎵 Album Art       │ ← Placeholder
│      (No metadata yet)   │
│                         │
├─────────────────────────┤
│   Song Name             │ ← Song title
│   5 / 100               │ ← Position
├─────────────────────────┤
│  [====●====]            │ ← Seek bar
│  2:34        / 5:00     │ ← Time
├─────────────────────────┤
│  ♥ ✓ ◀ ⏸ ▶ ↻         │ ← Controls
└─────────────────────────┘
```

### Home Screen
```
┌─────────────────────────┐
│   Music Library         │
│   [🔍 Search...]        │ ← Search
├─────────────────────────┤
│  🎵 Song 1        ♡     │ ← Song item
│  🎵 Song 2        ♡     │   (swipeable)
│  🎵 Song 3        ♡     │
│  🎵 Song 4        ♡     │
│  🎵 Song 5        ♡     │
├─────────────────────────┤
│ 🎚️  Now Playing    ⏸     │ ← Mini player
├─────────────────────────┤
│ [🏠 Home]  [📚 Library]  │ ← Nav tabs
└─────────────────────────┘
```

---

## 📊 What's Under the Hood

- **Language**: Kotlin
- **UI Framework**: Android Material Design
- **Database**: In-memory (ArrayList, HashSet)
- **Architecture**: Singleton + Adapter pattern
- **Playback**: Android MediaPlayer API
- **Storage**: Device local files only

---

## ✨ Pro Tips

1. **Faster Search**: Type partial name (e.g., "dua" finds "Dua Lipa")
2. **Mass Like**: Tap multiple hearts to build liked collection
3. **Smart Shuffle**: Works on current list (search results, playlists, all songs)
4. **Mini Player**: Click anywhere to open full player
5. **Back Button**: Works on all screens to navigate

---

## 🚀 Next Steps

After getting comfortable:

1. Read **README.md** for full features
2. Check **IMPLEMENTATION_GUIDE.md** for technical details
3. Review code in Android Studio
4. Try customizing colors or adding features
5. Build APK to share with others

---

## 📚 File Reference

| File | Purpose |
|------|---------|
| MainActivity.kt | Home screen & library |
| PlayerActivity.kt | Full-screen player |
| MusicPlayerManager.kt | Playback control |
| SongAdapter.kt | Song list display |
| PlaylistAdapter.kt | Playlist display |
| README.md | User documentation |
| IMPLEMENTATION_GUIDE.md | Technical guide |
| FEATURE_CHECKLIST.md | Complete features list |

---

## 🎓 Learning Resources

Inside the code you'll learn:
- ✅ Android Activities & Fragments
- ✅ RecyclerView & Adapters
- ✅ MediaPlayer API
- ✅ Intent Navigation
- ✅ Kotlin Coroutines basics
- ✅ Material Design implementation
- ✅ Permission handling
- ✅ Layouts & Views

---

## 📞 Quick Help

**If build fails:**
```
File → Invalidate Caches → Restart
Then: Build → Clean Project → Rebuild
```

**If app crashes:**
```
Logcat (bottom of Android Studio) shows error details
Look for red lines in the log
```

**If gradle hangs:**
```
Top-right corner → Sync Now
Or: File → Sync Project with Gradle Files
```

---

## 🎉 You're All Set!

Your Music Player app is **ready to use**! 

Enjoy the music! 🎶

---

*Questions? Check the docs or review the source code.*

*Happy coding!*

