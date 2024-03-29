package co.uk.postoffice.apps.parcelshop.services

import android.graphics.Bitmap
import com.mazenrashed.printooth.data.PrintingImagesHelper
import com.mazenrashed.printooth.data.converter.Converter
import com.mazenrashed.printooth.data.converter.DefaultConverter
import com.mazenrashed.printooth.data.printer.Printer
import com.starmicronics.starioextension.ICommandBuilder.CutPaperAction
import com.starmicronics.starioextension.StarIoExt



class StarPrinter: Printer(){
        override fun useConverter(): Converter {
        return DefaultConverter()
    }

    override fun initLineSpacingCommand(): ByteArray = byteArrayOf(0x1B, 0x33)

    override fun initInitPrinterCommand(): ByteArray = byteArrayOf(0x1b, 0x40)

    override fun initJustificationCommand(): ByteArray = byteArrayOf(27, 97)

    override fun initFontSizeCommand(): ByteArray = byteArrayOf(29, 33)

    override fun initEmphasizedModeCommand(): ByteArray = byteArrayOf(27, 69) //1 on , 0 off

    override fun initUnderlineModeCommand(): ByteArray = byteArrayOf(27, 45) //1 on , 0 off

    override fun initCharacterCodeCommand(): ByteArray = byteArrayOf(27, 116)

    override fun initFeedLineCommand(): ByteArray = byteArrayOf(27, 100)

    override fun initPrintingImagesHelper(): PrintingImagesHelper = StarPrintingImagesHelper()

    companion object {
        val ALLIGMENT_REGHT: Byte = 2
        val ALLIGMENT_LEFT: Byte = 0
        val ALLIGMENT_CENTER: Byte = 1
        val EMPHASISED_MODE_BOLD: Byte = 1
        val EMPHASISED_MODE_NORMAL: Byte = 0
        val UNDELINED_MODE_ON: Byte = 1
        val UNDELINED_MODE_OFF: Byte = 0
        val CHARACTER_CODE_USA_CP437: Byte = 0
        val CHARACTER_CODE_ARABIC_FARISI: Byte = 42
        val LINE_SPACING_60: Byte = 60
        val LINE_SPACING_30: Byte = 30
        val FONT_SIZE_NORMAL: Byte = 0x00
        val FONT_SIZE_LARGE: Byte = 0x10
    }
}

class StarPrintingImagesHelper : PrintingImagesHelper {
    override fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        return convertBitmapToByteArray(StarIoExt.Emulation.StarPRNT,bitmap)
    }

    private fun convertBitmapToByteArray(emulation: StarIoExt.Emulation, bitmap: Bitmap): ByteArray {
        val builder = StarIoExt.createCommandBuilder(emulation)
        builder.beginDocument()
        builder.appendBitmap(bitmap, false)
        builder.appendCutPaper(CutPaperAction.PartialCutWithFeed)
        builder.endDocument()
        return builder.commands
    }
}