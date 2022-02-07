package pl.dyzio.smartclockalarm

import androidx.core.text.isDigitsOnly
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {

        var initialValue : String? = "it:it"
        val result = initialValue?.let {
            var result = 0
            if (it.isDigitsOnly())
            {
                result = it.filterIndexed{ind , _ -> ind in 0..1}.toInt()
            }
            result
        } ?: 0
        assertEquals(result,0)
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("pl.dyzio.smartclockalarm", appContext.packageName)
        assertEquals("80.126.4.56", "80.126.4.56")
    }
}