package com.arlabs.uncloud.domain.manager

import com.arlabs.uncloud.domain.model.Quote
import com.arlabs.uncloud.domain.repository.UserRepository
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.first

@Singleton
class QuoteManager @Inject constructor(private val userRepository: UserRepository) {
    private val quotes =
            listOf(
                    Quote("The best way out is always through.", "Robert Frost"),
                    Quote("It always seems impossible until it is done.", "Nelson Mandela"),
                    Quote("Fall seven times, stand up eight.", "Japanese Proverb"),
                    Quote(
                            "It does not matter how slowly you go as long as you do not stop.",
                            "Confucius"
                    ),
                    Quote(
                            "Discipline is choosing between what you want now and what you want most.",
                            "Abraham Lincoln"
                    ),
                    Quote("If you are going through hell, keep going.", "Winston Churchill"),
                    Quote(
                            "The pain of discipline is far less than the pain of regret.",
                            "Jim Rohn"
                    ),
                    Quote(
                            "We are what we repeatedly do. Excellence, then, is not an act, but a habit.",
                            "Aristotle"
                    ),
                    Quote(
                            "Success is the sum of small efforts, repeated day in and day out.",
                            "Robert Collier"
                    ),
                    Quote(
                            "Strength does not come from physical capacity. It comes from an indomitable will.",
                            "Mahatma Gandhi"
                    ),
                    Quote("Tough times never last, but tough people do.", "Robert H. Schuller"),
                    Quote("Believe you can and you're halfway there.", "Theodore Roosevelt"),
                    Quote(
                            "Motivation is what gets you started. Habit is what keeps you going.",
                            "Jim Ryun"
                    ),
                    Quote("Don't watch the clock; do what it does. Keep going.", "Sam Levenson"),
                    Quote("You must do the thing you think you cannot do.", "Eleanor Roosevelt"),
                    Quote(
                            "The best time to plant a tree was 20 years ago. The second best time is now.",
                            "Chinese Proverb"
                    ),
                    Quote(
                            "Mastering others is strength. Mastering yourself is true power.",
                            "Lao Tzu"
                    ),
                    Quote("It is never too late to be what you might have been.", "George Eliot"),
                    Quote(
                            "Perseverance is not a long race; it is many short races one after the other.",
                            "Walter Elliot"
                    ),
                    Quote(
                            "You have power over your mind - not outside events. Realize this, and you will find strength.",
                            "Marcus Aurelius"
                    ),
                    Quote(
                            "Change is hardest at the beginning, messiest in the middle and best at the end.",
                            "Robin Sharma"
                    ),
                    Quote("Don't let yesterday take up too much of today.", "Will Rogers"),
                    Quote(
                            "I am the master of my fate, I am the captain of my soul.",
                            "William Ernest Henley"
                    ),
                    Quote("Act as if what you do makes a difference. It does.", "William James"),
                    Quote("A year from now you will wish you had started today.", "Karen Lamb"),
                    Quote(
                            "What you get by achieving your goals is not as important as what you become by achieving your goals.",
                            "Zig Ziglar"
                    ),
                    Quote("Only I can change my life. No one can do it for me.", "Carol Burnett"),
                    Quote("He who conquers himself is the mightiest warrior.", "Confucius"),
                    Quote("Turn your wounds into wisdom.", "Oprah Winfrey"),
                    Quote("Dream big and dare to fail.", "Norman Vaughan"),
                    Quote(
                            "The man who moves a mountain begins by carrying away small stones.",
                            "Confucius"
                    ),
                    Quote(
                            "Success isn't always about greatness. It's about consistency.",
                            "Dwayne Johnson"
                    ),
                    Quote(
                            "You are never too old to set another goal or to dream a new dream.",
                            "C.S. Lewis"
                    ),
                    Quote(
                            "It is not because things are difficult that we do not dare; it is because we do not dare that they are difficult.",
                            "Seneca"
                    ),
                    Quote(
                            "Great things are not done by impulse, but by a series of small things brought together.",
                            "Vincent Van Gogh"
                    ),
                    Quote(
                            "To keep the body in good health is a duty... otherwise we shall not be able to keep our mind strong and clear.",
                            "Buddha"
                    ),
                    Quote(
                            "If you want to fly, you have to give up the things that weigh you down.",
                            "Toni Morrison"
                    ),
                    Quote(
                            "Every morning we are born again. What we do today matters most.",
                            "Buddha"
                    ),
                    Quote(
                            "Take care of your body. It's the only place you have to live.",
                            "Jim Rohn"
                    ),
                    Quote(
                            "Courage is resistance to fear, mastery of fear, not absence of fear.",
                            "Mark Twain"
                    ),
                    Quote(
                            "What lies behind us and what lies before us are tiny matters compared to what lies within us.",
                            "Ralph Waldo Emerson"
                    ),
                    Quote("Life begins at the end of your comfort zone.", "Neale Donald Walsch"),
                    Quote(
                            "Small daily improvements over time lead to stunning results.",
                            "Robin Sharma"
                    ),
                    Quote(
                            "Don't be pushed around by the fears in your mind. Be led by the dreams in your heart.",
                            "Roy T. Bennett"
                    ),
                    Quote("There is nothing permanent except change.", "Heraclitus"),
                    Quote(
                            "Success consists of going from failure to failure without loss of enthusiasm.",
                            "Winston Churchill"
                    ),
                    Quote(
                            "It’s not whether you get knocked down, it’s whether you get up.",
                            "Vince Lombardi"
                    ),
                    Quote("Don't count the days, make the days count.", "Muhammad Ali"),
                    Quote(
                            "When everything seems to be going against you, remember that the airplane takes off against the wind, not with it.",
                            "Henry Ford"
                    ),
                    Quote(
                            "Hardships often prepare ordinary people for an extraordinary destiny.",
                            "C.S. Lewis"
                    ),
                    Quote("The secret of getting ahead is getting started.", "Mark Twain"),
                    Quote(
                            "Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle.",
                            "Christian D. Larson"
                    ),
                    Quote("Action is the foundational key to all success.", "Pablo Picasso"),
                    Quote(
                            "I count him braver who overcomes his desires than him who conquers his enemies.",
                            "Aristotle"
                    ),
                    Quote(
                            "Even if you fall on your face, you're still moving forward.",
                            "Victor Kiam"
                    ),
                    Quote(
                            "Everything you’ve ever wanted is on the other side of fear.",
                            "George Addair"
                    ),
                    Quote("Don't wait. The time will never be just right.", "Napoleon Hill"),
                    Quote(
                            "Our greatest glory is not in never falling, but in rising every time we fall.",
                            "Oliver Goldsmith"
                    ),
                    Quote(
                            "Freedom is what you do with what's been done to you.",
                            "Jean-Paul Sartre"
                    ),
                    Quote(
                            "Your potential is endless. Go do what you were created to do.",
                            "Unknown"
                    ),
                    Quote(
                            "The river cuts through rock, not because of its power, but because of its persistence.",
                            "Jim Watkins"
                    ),
                    Quote(
                            "Your life does not get better by chance, it gets better by change.",
                            "Jim Rohn"
                    ),
                    Quote("A healthy outside starts from the inside.", "Robert Urich"),
                    Quote(
                            "Whether you think you can or you think you can’t, you’re right.",
                            "Henry Ford"
                    ),
                    Quote(
                            "Start where you are. Use what you have. Do what you can.",
                            "Arthur Ashe"
                    ),
                    Quote(
                            "Every human being is the author of his own health or disease.",
                            "Buddha"
                    ),
                    Quote("Energy and persistence conquer all things.", "Benjamin Franklin"),
                    Quote("Clear your mind of can't.", "Samuel Johnson"),
                    Quote("Trees that are slow to grow bear the best fruit.", "Molière"),
                    Quote("The groundwork for all happiness is health.", "Leigh Hunt"),
                    Quote(
                            "Change your thoughts and you change your world.",
                            "Norman Vincent Peale"
                    ),
                    Quote("Love yourself enough to live a healthy lifestyle.", "Jules Robson"),
                    Quote("Patience is bitter, but its fruit is sweet.", "Aristotle"),
                    Quote("The mind is everything. What you think you become.", "Buddha"),
                    Quote(
                            "To ensure good health: eat lightly, breathe deeply, live moderately, cultivate cheerfulness, and maintain an interest in life.",
                            "William Londen"
                    ),
                    Quote(
                            "Every action you take is a vote for the type of person you wish to become.",
                            "James Clear"
                    ),
                    Quote(
                            "Addiction is giving up everything for one thing. Recovery is giving up one thing for everything.",
                            "Unknown"
                    ),
                    Quote(
                            "The chains of habit are too light to be felt until they are too heavy to be broken.",
                            "Warren Buffett"
                    ),
                    Quote("No man is free who is not master of himself.", "Epictetus"),
                    Quote("Don't wish it were easier. Wish you were better.", "Jim Rohn"),
                    Quote(
                            "It takes courage to grow up and become who you really are.",
                            "E.E. Cummings"
                    ),
                    Quote("One day or Day One. You decide.", "Paulo Coelho"),
                    Quote("The first and best victory is to conquer self.", "Plato"),
                    Quote(
                            "If you do what you’ve always done, you’ll get what you’ve always gotten.",
                            "Tony Robbins"
                    ),
                    Quote(
                            "Obstacles are those frightful things you see when you take your eyes off your goal.",
                            "Henry Ford"
                    ),
                    Quote(
                            "The snake which cannot cast its skin has to die.",
                            "Friedrich Nietzsche"
                    ),
                    Quote(
                            "You don't have to be great to start, but you have to start to be great.",
                            "Zig Ziglar"
                    ),
                    Quote(
                            "Do not wait to strike till the iron is hot; but make it hot by striking.",
                            "William Butler Yeats"
                    ),
                    Quote(
                            "You cannot dream yourself into a character; you must hammer and forge yourself one.",
                            "James A. Froude"
                    ),
                    Quote(
                            "Whatever you hold in your mind will tend to occur in your life.",
                            "Zig Ziglar"
                    )
            )

    suspend fun getDailyQuote(): Quote {
        val (savedDate, savedIndex) = userRepository.quoteState.first()
        val today = LocalDate.now().toString()

        if (savedDate != today) {
            val newIndex = quotes.indices.random()
            userRepository.saveQuoteState(today, newIndex)
            return quotes[newIndex]
        }

        return quotes.getOrElse(savedIndex) { quotes[0] }
    }

    suspend fun forceRefreshQuote(): Quote {
        val today = LocalDate.now().toString()
        val newIndex = quotes.indices.random()
        userRepository.saveQuoteState(today, newIndex)
        return quotes[newIndex]
    }
}
