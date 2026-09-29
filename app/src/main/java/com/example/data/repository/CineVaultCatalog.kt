package com.example.data.repository

import com.example.data.model.CastMember
import com.example.data.model.ContentItem
import com.example.data.model.ContentType
import com.example.data.model.Episode
import com.example.data.model.Review
import com.example.data.model.Season

object CineVaultCatalog {

    // Royalty-free legal sample test videos for developer media playback
    const val SAMPLE_STREAM_1 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
    const val SAMPLE_STREAM_2 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
    const val SAMPLE_STREAM_3 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
    const val SAMPLE_STREAM_4 = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"

    // High quality thematic cinematic backdrops & posters from Unsplash
    val initialItems: List<ContentItem> = listOf(
        ContentItem(
            id = "cv-movie-01",
            title = "Neon Horizon",
            tagline = "The city never sleeps. Neither do its secrets.",
            synopsis = "In the year 2089, a disgraced neural detective is pulled out of exile to investigate a high-level syndicate infiltrating the metropolis's synthetic consciousness grid. As synthetic and human identities blur, he uncovers a conspiracy that threatens the foundation of human autonomy.",
            type = ContentType.MOVIE,
            releaseYear = 2026,
            contentRating = "TV-MA",
            genres = listOf("Sci-Fi", "Cyberpunk", "Action", "Thriller"),
            durationMinutes = 138,
            rating = 8.9f,
            posterUrl = "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_4,
            trailerUrl = SAMPLE_STREAM_3,
            qualityBadge = "4K UHD",
            audioFormat = "Dolby Atmos 7.1",
            director = "Elena Rostova",
            writers = listOf("Marcus Chen", "Alexander Cross"),
            studio = "CineVault Original Films",
            cast = listOf(
                CastMember("c1", "Alexander Cross", "Detective Thorne", "Lead Actor"),
                CastMember("c2", "Yuki Tanaka", "Aria Vance (Neural Architect)", "Lead Actress"),
                CastMember("c3", "Idris Sterling", "Commander Mercer", "Supporting Actor"),
                CastMember("c4", "Chloe Moreau", "Synthetica Voice Unit", "Supporting Actress")
            ),
            reviews = listOf(
                Review("r1", "Cinema Horizon", "", 9.5f, "Sep 2026", "A masterpiece of modern cyberpunk cinema. The atmospheric worldbuilding and sound design are second to none."),
                Review("r2", "David K.", "", 9.0f, "Aug 2026", "Breathtaking visual effects in 4K. The final confrontation scene left me completely speechless.", isUserReview = false)
            ),
            isFeatured = true,
            isTrending = true,
            isNewRelease = true,
            watchCount = 4280000L
        ),

        ContentItem(
            id = "cv-series-01",
            title = "Last Signal",
            tagline = "Silence is not the absence of sound. It is a warning.",
            synopsis = "Deep in the Arctic wilderness, a remote deep-space tracking observatory intercepts an encoded transmission repeating every 47 minutes. When the communication lines to the outside world go dark, six scientists realize they might not be the only ones listening.",
            type = ContentType.SERIES,
            releaseYear = 2025,
            contentRating = "16+",
            genres = listOf("Sci-Fi", "Mystery", "Thriller", "Drama"),
            totalSeasons = 3,
            rating = 9.1f,
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_1,
            trailerUrl = SAMPLE_STREAM_2,
            qualityBadge = "4K UHD",
            audioFormat = "Dolby Atmos 5.1",
            director = "Soren Lindqvist",
            writers = listOf("Astrid Lindqvist", "Dr. Paul Novak"),
            studio = "Nordic Vault Productions",
            cast = listOf(
                CastMember("c5", "Karin Lindholm", "Dr. Astrid Ward", "Lead Actress"),
                CastMember("c6", "Victor Bradley", "Dr. Raymond Hayes", "Lead Actor"),
                CastMember("c7", "Nils Fjell", "Station Engineer Brogan", "Supporting Actor")
            ),
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1: The Echo",
                    episodeCount = 8,
                    episodes = listOf(
                        Episode("ep1", "cv-series-01", 1, 1, "Episode 1: The 47-Minute Loop", "The observatory crew intercepts an anomalous deep-space burst that disrupts all satellite links.", 56, "Oct 2025", SAMPLE_STREAM_1, "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600"),
                        Episode("ep2", "cv-series-01", 1, 2, "Episode 2: Frostbite Frequency", "As sub-zero temperatures plummet, internal diagnostics reveal tampering with the communications array.", 52, "Oct 2025", SAMPLE_STREAM_2, "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=600"),
                        Episode("ep3", "cv-series-01", 1, 3, "Episode 3: Ghost in the Ice", "An unmapped cavern below the glacier is discovered to be emitting thermal pulses matching the signal.", 58, "Nov 2025", SAMPLE_STREAM_3, "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=600"),
                        Episode("ep4", "cv-series-01", 1, 4, "Episode 4: Blackout Protocol", "Power fails across Sector 4. Brogan risks his life to manually restart the geothermal core.", 54, "Nov 2025", SAMPLE_STREAM_4, "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600")
                    )
                ),
                Season(
                    seasonNumber = 2,
                    title = "Season 2: Deep Polar",
                    episodeCount = 6,
                    episodes = listOf(
                        Episode("ep5", "cv-series-01", 2, 1, "Episode 1: The Descent", "A rescue convoy arrives, but their true mission orders are far from humanitarian.", 60, "Mar 2026", SAMPLE_STREAM_1),
                        Episode("ep6", "cv-series-01", 2, 2, "Episode 2: Resonance", "The decoded signal contains human DNA sequences originating from 12,000 years in the past.", 55, "Mar 2026", SAMPLE_STREAM_2)
                    )
                )
            ),
            reviews = listOf(
                Review("r3", "Streaming Insider", "", 9.6f, "Sep 2026", "A tense, claustrophobic psychological thriller that never lets up. Brilliant pacing and icy tension.")
            ),
            isFeatured = true,
            isTrending = true,
            isRecommended = true,
            watchCount = 6120000L
        ),

        ContentItem(
            id = "cv-movie-02",
            title = "Midnight Protocol",
            tagline = "Trust nobody. Delete everything.",
            synopsis = "When an international cyber defense agency's vault keys are leaked in Geneva, an operative with 24 hours to clear her name races through Berlin and Tokyo, evading black-budget mercenaries and automated hunter-drones.",
            type = ContentType.MOVIE,
            releaseYear = 2026,
            contentRating = "R",
            genres = listOf("Action", "Thriller", "Crime"),
            durationMinutes = 118,
            rating = 8.7f,
            posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1514565131-fce0801e5785?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_3,
            qualityBadge = "4K UHD",
            audioFormat = "Dolby Atmos 5.1",
            director = "Julian Drake",
            writers = listOf("Maya Lin", "Julian Drake"),
            studio = "Velocity Pictures",
            cast = listOf(
                CastMember("c8", "Maya Lin", "Agent Kestrel", "Lead Actress"),
                CastMember("c9", "Devon Cole", "Victor Stone", "Supporting Actor")
            ),
            isTrending = true,
            isNewRelease = true,
            watchCount = 3100000L
        ),

        ContentItem(
            id = "cv-movie-03",
            title = "Beyond the Orbit",
            tagline = "The horizon was only the beginning.",
            synopsis = "A civilian transport crew navigating the Saturnian rings encounters a temporal rift that catapults their vessel into an uncharted sector of the Milky Way, forcing humanity's first genuine contact with an ancient stellar intelligence.",
            type = ContentType.MOVIE,
            releaseYear = 2025,
            contentRating = "PG-13",
            genres = listOf("Sci-Fi", "Adventure", "Drama"),
            durationMinutes = 145,
            rating = 9.0f,
            posterUrl = "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1462331940025-496dfbfc7564?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_1,
            qualityBadge = "4K UHD",
            director = "Talia Sterling",
            writers = listOf("Talia Sterling", "Robert Hayes"),
            studio = "Astraea Media",
            isFeatured = true,
            isRecommended = true,
            watchCount = 5400000L
        ),

        ContentItem(
            id = "cv-series-02",
            title = "Silent City",
            tagline = "Every shadow has a name.",
            synopsis = "Set in a decaying industrial metropolis where rain never stops and crime bosses govern through biometric extortion, an uncorrupted city prosecutor teams with a former street enforcer to dismantle the city's corrupt oligarchy.",
            type = ContentType.SERIES,
            releaseYear = 2024,
            contentRating = "TV-MA",
            genres = listOf("Crime", "Drama", "Mystery", "Thriller"),
            totalSeasons = 2,
            rating = 8.8f,
            posterUrl = "https://images.unsplash.com/photo-1477959858617-67f30bc75b82?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_2,
            qualityBadge = "FHD 1080p",
            director = "Emir Santos",
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 10,
                    episodes = listOf(
                        Episode("ep-sc-1", "cv-series-02", 1, 1, "Pilot: Rain Upon Glass", "The assassination of a federal judge opens a dangerous power vacuum in the docks.", 51, "2024", SAMPLE_STREAM_2),
                        Episode("ep-sc-2", "cv-series-02", 1, 2, "The Ledger", "Elena unearths encrypted offshore accounts tied to the mayoral election.", 49, "2024", SAMPLE_STREAM_3)
                    )
                )
            ),
            isTrending = true,
            watchCount = 2890000L
        ),

        ContentItem(
            id = "cv-movie-04",
            title = "The Final Route",
            tagline = "Across three thousand miles of no-man's land.",
            synopsis = "In a drought-ravaged continental expanse, an armored convoy driver agrees to transport a miraculous solar desalinator prototype across warlord-controlled territories to save the last sovereign sanctuary city.",
            type = ContentType.MOVIE,
            releaseYear = 2026,
            contentRating = "R",
            genres = listOf("Action", "Survival", "Adventure"),
            durationMinutes = 112,
            rating = 8.4f,
            posterUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_3,
            qualityBadge = "4K UHD",
            director = "Zane Gallagher",
            isNewRelease = true,
            watchCount = 1950000L
        ),

        ContentItem(
            id = "cv-series-03",
            title = "Shadow District",
            tagline = "In the heart of Europe, wars are waged with whispered names.",
            synopsis = "A clandestine allied intelligence unit uncovers a rogue military contractor operating autonomous kill-switches in international commercial airspace. The team must navigate treacherous diplomatic traps to disarm the network.",
            type = ContentType.SERIES,
            releaseYear = 2025,
            contentRating = "TV-MA",
            genres = listOf("Action", "Thriller", "Espionage"),
            totalSeasons = 2,
            rating = 8.6f,
            posterUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1478760329108-5c3ed9d495a0?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_4,
            qualityBadge = "4K UHD",
            director = "Charlotte Mercier",
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 6,
                    episodes = listOf(
                        Episode("ep-sd-1", "cv-series-03", 1, 1, "Rendezvous in Vienna", "A dead drop goes violently wrong in a subway terminal.", 55, "2025", SAMPLE_STREAM_4),
                        Episode("ep-sd-2", "cv-series-03", 1, 2, "Shadow Protocol", "The team identifies a mole inside NATO's cybersecurity wing.", 52, "2025", SAMPLE_STREAM_1)
                    )
                )
            ),
            isRecommended = true,
            watchCount = 3450000L
        ),

        ContentItem(
            id = "cv-movie-05",
            title = "Echoes of Tomorrow",
            tagline = "Can you remember what hasn't happened yet?",
            synopsis = "A quantum physicist working on deep gravity wave interference begins experiencing vivid memories of the next 72 hours, forcing him into a desperate chess match against a fate that seems destined to repeat.",
            type = ContentType.MOVIE,
            releaseYear = 2025,
            contentRating = "PG-13",
            genres = listOf("Sci-Fi", "Drama", "Mystery"),
            durationMinutes = 126,
            rating = 8.9f,
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_1,
            qualityBadge = "4K UHD",
            director = "Nolan Vance",
            isFeatured = true,
            isRecommended = true,
            watchCount = 4780000L
        ),

        ContentItem(
            id = "cv-movie-06",
            title = "Solaris Prime",
            tagline = "The golden crown of human exploration.",
            synopsis = "Humanity's first hyper-luminal interstellar expedition arrives at a binary star system, only to discover an immense crystalline megastructure orbiting a dead sun.",
            type = ContentType.MOVIE,
            releaseYear = 2026,
            contentRating = "PG-13",
            genres = listOf("Sci-Fi", "Adventure"),
            durationMinutes = 135,
            rating = 8.5f,
            posterUrl = "https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1506703719100-a0f3a48c0f86?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_2,
            qualityBadge = "4K UHD",
            director = "Cassandra Bloom",
            isNewRelease = true,
            watchCount = 2100000L
        ),

        ContentItem(
            id = "cv-movie-07",
            title = "Lumina: The Forgotten Kingdom",
            tagline = "Light always returns to the brave.",
            synopsis = "In a breathtaking enchanted realm shrouded in perpetual starlight, an adventurous young cartographer and a glowing mechanical spirit set out to rekindle the ancient Celestial Hearth.",
            type = ContentType.MOVIE,
            releaseYear = 2025,
            contentRating = "PG",
            genres = listOf("Animation", "Family", "Adventure", "Fantasy"),
            durationMinutes = 98,
            rating = 8.7f,
            posterUrl = "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_1,
            qualityBadge = "4K UHD",
            director = "Hayao Miyazaki Tribute Studio",
            isTrending = true,
            isRecommended = true,
            watchCount = 5120000L
        ),

        ContentItem(
            id = "cv-series-04",
            title = "Quantum Velocity",
            tagline = "Steal the code. Rewrite reality.",
            synopsis = "A team of former quantum engineers and master digital thieves plan a synchronized heist across three decentralized server fortresses floating in international waters.",
            type = ContentType.SERIES,
            releaseYear = 2026,
            contentRating = "TV-MA",
            genres = listOf("Action", "Sci-Fi", "Crime"),
            totalSeasons = 1,
            rating = 8.5f,
            posterUrl = "https://images.unsplash.com/photo-1526374965328-7f61d4dc18c5?w=800&auto=format&fit=crop&q=80",
            backdropUrl = "https://images.unsplash.com/photo-1508739773434-c26b3d09e071?w=1600&auto=format&fit=crop&q=80",
            videoUrl = SAMPLE_STREAM_3,
            qualityBadge = "4K UHD",
            director = "Simon Lee",
            seasons = listOf(
                Season(
                    seasonNumber = 1,
                    title = "Season 1",
                    episodeCount = 8,
                    episodes = listOf(
                        Episode("ep-qv-1", "cv-series-04", 1, 1, "The Heist at 00:00", "The crew breaches the oceanic server platform.", 48, "2026", SAMPLE_STREAM_3)
                    )
                )
            ),
            isNewRelease = true,
            watchCount = 1890000L
        )
    )
}
