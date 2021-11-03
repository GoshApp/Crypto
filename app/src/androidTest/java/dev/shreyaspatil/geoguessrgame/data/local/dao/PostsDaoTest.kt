import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.goshapp.geoguessrgame.data.local.FoodiumPostsDatabase
import com.goshapp.geoguessrgame.model.Post
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.jvm.Throws

@RunWith(AndroidJUnit4::class)
class PostsDaoTest {

    private lateinit var mDatabase: FoodiumPostsDatabase

    @Before
    fun init() {
        mDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            FoodiumPostsDatabase::class.java
        ).build()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insert_and_select_posts() = runBlocking {
        val posts = listOf(
            Post(1, "Test 1", "Test 1", "Test 1"),
            Post(2, "Test 2", "Test 2", "Test 3")
        )

        mDatabase.getPostsDao().insertPosts(posts)

        val dbPosts = mDatabase.getPostsDao().getAllPosts().toList()[0]

        assertThat(dbPosts, equalTo(posts))
    }

    @Test
    @Throws(InterruptedException::class)
    fun select_post_by_id() = runBlocking {
        val posts = listOf(
            Post(1, "Test 1", "Test 1", "Test 1"),
            Post(2, "Test 2", "Test 2", "Test 3")
        )

        mDatabase.getPostsDao().insertPosts(posts)

        var dbPost = mDatabase.getPostsDao().getPostById(1)
        assertThat(dbPost.toList()[0], equalTo(posts[0]))

        dbPost = mDatabase.getPostsDao().getPostById(1)
        assertThat(dbPost.toList()[1], equalTo(posts[1]))
    }

    @After
    fun cleanup() {
        mDatabase.close()
    }
}
