import com.example.meteora.db.remote.FakeRemoteDataSource
import com.example.meteora.db.local.FakeLocalDataSource
import com.example.meteora.db.repository.RepositoryImpl
import com.example.meteora.model.Forcast
import com.example.meteora.model.Weather
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFailsWith

class RepositoryImplTest {

    private lateinit var repository: RepositoryImpl
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var fakeLocalDataSource: FakeLocalDataSource

    @Before
    fun setUp() {
        fakeRemoteDataSource = FakeRemoteDataSource()
        fakeLocalDataSource = FakeLocalDataSource()
        repository = RepositoryImpl(fakeRemoteDataSource, fakeLocalDataSource)
    }

    @Test
    fun fetchCurrentWeather_RemoteSuccessful_ReturnsWeatherData() = runTest {
        // Given
        val expectedWeather = Weather()
        //im fetching weather data from remote data source with paramter less constructor
        fakeRemoteDataSource.weather = expectedWeather

        // When
        val result = repository.fetchCurrentWeather(0.0, 0.0, "metric", "en").first()

        // Then
        assertThat(result, `is`(expectedWeather))
    }

    @Test
    fun fetchCurrentWeather_RemoteFailed_ThrowsException() = runTest {
        // Given
        fakeRemoteDataSource.shouldReturnError = true

        // When
        val exception = assertFailsWith<Exception> {
            repository.fetchCurrentWeather(0.0, 0.0, "metric", "en").first()
        }

        // Then
        assertTrue(exception.message?.contains("Failed to fetch weather data") == true)
    }

    @Test
    fun fetchForecast_RemoteSuccessful_ReturnsForecastData() = runTest {
        // Given
        val expectedForecast = Forcast()
        fakeRemoteDataSource.forecast = expectedForecast

        // When
        val result = repository.fetchForecast(0.0, 0.0, "metric", "en").first()

        // Then
        assertThat(result, `is`(expectedForecast))
    }

    @Test
    fun fetchForecast_RemoteFailed_ThrowsException() = runTest {
        // Given
        fakeRemoteDataSource.shouldReturnError = true

        // When
        val exception = assertFailsWith<Exception> {
            repository.fetchForecast(0.0, 0.0, "metric", "en").first()
        }

        // Then
        assertTrue(exception.message?.contains("Failed to fetch forecast data") == true)
    }

}
