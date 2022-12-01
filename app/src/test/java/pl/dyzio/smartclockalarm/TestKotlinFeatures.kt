package pl.dyzio.smartclockalarm

import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import org.junit.Test
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.name
import kotlin.reflect.KProperty

internal object UNINITIALIZED

class Delegator {
    private var valueData : Any? = UNINITIALIZED
    operator fun getValue(ref: Any?, prop: KProperty<*>) : String {
        return if (valueData !== UNINITIALIZED) {
            "$ref, Access Data with ${prop.name} return '$valueData'"
        } else {
            "Value is still not set "
        }
    }

    operator fun setValue(ref: Any?, prop: KProperty<*>, v: String){
        valueData = v;
        println("Set value by $v using $ref and ${prop.name}")
    }
}

class StoreData {
    var from: String by Delegator()
    var to: String = "Control"
}

class TestKotlinFeatures {

    //@Ignore
    @Test
    fun testClassFeature()
    {
        var svals = StoreData()

        println(svals.from)
        println(svals.to)
        svals.from = "Acme"

        println(svals.from)

        val chek = listOf("192", "168", "000", "201").joinToString(".") {
                    testChars ->
                val text = testChars.trimStart('0')
                if (text.isEmpty())
                {
                    return@joinToString "0"
                }
                return@joinToString text
            }

        println(chek)

        try {
            svals.from = "aaaa"
        }
        catch (exc:Exception)
        {
            println(exc.stackTrace)
        }
    }

    @Test
    fun testExtractPDF()
    {
        val fileCheck  = Files.list(Paths.get("./src/test/java/pl/dyzio/smartclockalarm"))
        val file = Files.newInputStream(fileCheck.filter{ it.name.contains("pdf") }.findFirst().get(), StandardOpenOption.READ)
        val reader = PdfReader(file)
        val numPages = reader.numberOfPages
        val textBuilder = StringBuilder()
        for (idx in 1 .. numPages)
        {
            textBuilder.append(PdfTextExtractor.getTextFromPage(reader, idx))
        }
        val numberfinder = "\\d+".toRegex()
        val find = numberfinder.findAll(textBuilder.toString())
        val result = find.filterIndexed{ xx, _ -> xx != 0 }
        result.forEach { matchResult ->  println(matchResult.value) }
        println(textBuilder.toString())
    }
}