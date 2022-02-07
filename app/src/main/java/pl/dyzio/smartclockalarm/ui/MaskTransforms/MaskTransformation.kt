package pl.dyzio.smartclockalarm.ui.MaskTransforms

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MaskTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = if (text.text.length > 4) text.text.substring(0..3) else text.text
        var out = ""
        for(i in trimmed.indices){
            out+= trimmed[i]
            if (i==1) out+=":"
        }

        val numberOffsetTranslation = object : OffsetMapping{
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if(offset <= 3) return offset+1
                return 5
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 4) return offset - 1
                return 4
            }

        }

        return TransformedText(AnnotatedString(out), numberOffsetTranslation)
    }
}


class MaskIPTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Making XXX.XXX.XXX.XXX string.
        val trimmed = if (text.text.length >= 12) text.text.substring(0..11) else text.text
        var out = ""
        for(i in trimmed.indices){
            out+= trimmed[i]
            if (i % 3 == 2 && i != 11) out+="."
        }

        val numberOffsetTranslation = object : OffsetMapping{
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset + 1
                if (offset <= 8) return offset + 2
                if (offset <= 12) return offset + 3
                return 15
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                if (offset <= 7) return offset - 1
                if (offset <= 11) return offset - 2
                if (offset <= 15) return offset - 3
                return 12
            }

        }
        return TransformedText(AnnotatedString(out),numberOffsetTranslation)
    }

}

class PortNumberMask : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        // Making NNNN string.
        val trimmed = if (text.text.length >= 4) text.text.substring(0..3) else text.text

        val numberOffsetTranslation = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 3) return offset
                return 4
            }

            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 3) return offset
                return 4
            }
        }
        return TransformedText(AnnotatedString(trimmed), numberOffsetTranslation)
    }
}