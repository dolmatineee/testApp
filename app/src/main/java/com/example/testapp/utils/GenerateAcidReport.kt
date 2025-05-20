package com.example.testapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well
import org.apache.poi.util.Units
import org.apache.poi.xwpf.usermodel.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun generateAcidReport(
    customer: Customer,
    field: Field,
    layer: Layer,
    well: Well,
    signatureBitmap: ImageBitmap,
    context: Context,
    photo5000General: Uri,
    photo5000AfterPour_25_75: Uri,
    photo5000AfterPour_50_50: Uri,
    photo5000AfterPour_75_25: Uri,
    photo5000AfterPour_spent: Uri,
    photo2000General: Uri,
    photo2000AfterPour_25_75: Uri,
    photo2000AfterPour_50_50: Uri,
    photo2000AfterPour_75_25: Uri,
    photo2000AfterPour_spent: Uri
): File {
    // Создаем новый документ
    val document = XWPFDocument()

    // Добавляем титульный лист
    val titlePage = document.createParagraph()
    titlePage.alignment = ParagraphAlignment.CENTER
    val titleRun = titlePage.createRun()
    titleRun.setText("Лабораторный отчет по кислоте")
    titleRun.isBold = true
    titleRun.fontSize = 20
    titleRun.fontFamily = "Times New Roman"
    repeat(6) {
        titleRun.addBreak()
    }

    // Добавляем информацию о месторождении, скважине, пласте и дате проведения
    val infoParagraph = document.createParagraph()
    infoParagraph.alignment = ParagraphAlignment.LEFT
    val infoRun = infoParagraph.createRun()
    infoRun.setText("Месторождение: ${field.name}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    infoRun.setText("Скважина: ${well.wellNumber}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    infoRun.setText("Пласт: ${layer.layerName}")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    infoRun.addBreak()

    // Дата проведения (текущая дата)
    val currentDate = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy г.")
    val formattedDate = currentDate.format(formatter)
    infoRun.setText("Дата проведения: $formattedDate")
    infoRun.fontSize = 16
    infoRun.isBold = true
    infoRun.fontFamily = "Times New Roman"
    repeat(10) {
        infoRun.addBreak()
    }

    // Добавляем информацию о заказчике внизу титульного листа
    val customerParagraph = document.createParagraph()
    customerParagraph.alignment = ParagraphAlignment.CENTER
    val customerRun = customerParagraph.createRun()
    customerRun.setText("Для ${customer.companyName}.")
    customerRun.fontSize = 16
    customerRun.isBold = true
    customerRun.fontFamily = "Times New Roman"
    customerRun.addBreak()
    customerRun.setText("${currentDate.year} г.")
    customerRun.fontSize = 16
    customerRun.fontFamily = "Times New Roman"

    // Добавляем разрыв страницы после титульного листа
    val breakParagraph = document.createParagraph()
    val breakRun = breakParagraph.createRun()
    breakRun.addBreak(BreakType.PAGE)

    // Добавляем таблицу для фотографий
    val photoTable = document.createTable()
    photoTable.setWidth("100%")

    // Максимальная ширина изображения
    val maxImageWidth = 500.0

    // Функция для добавления фотографии в документ
    fun addPhotoToDocument(photoUri: Uri, description: String) {
        val photoRow = photoTable.createRow()
        val photoCell = photoRow.getCell(0)

        val imageParagraph = photoCell.addParagraph()
        imageParagraph.alignment = ParagraphAlignment.CENTER
        val imageRun = imageParagraph.createRun()

        val inputStream = context.contentResolver.openInputStream(photoUri)
        val imageBytes = inputStream?.readBytes()

        if (imageBytes != null) {
            val (width, height) = getImageDimensions(imageBytes)
            val aspectRatio = width.toDouble() / height.toDouble()

            var imageHeight = 400.0
            var imageWidth = imageHeight * aspectRatio

            if (imageWidth > maxImageWidth) {
                imageWidth = maxImageWidth
                imageHeight = imageWidth / aspectRatio
            }

            imageRun.addPicture(
                ByteArrayInputStream(imageBytes),
                XWPFDocument.PICTURE_TYPE_JPEG,
                "image.jpg",
                Units.toEMU(imageWidth),
                Units.toEMU(imageHeight)
            )
        }

        val descriptionRow = photoTable.createRow()
        val descriptionCell = descriptionRow.getCell(0)
        descriptionCell.text = description
        descriptionCell.paragraphs[0].alignment = ParagraphAlignment.CENTER
    }

    // Добавляем фотографии
    addPhotoToDocument(photo5000General, "Фотография плотномера при замере плотности концентрированной кислоты")
    addPhotoToDocument(photo5000AfterPour_25_75, "Пролив 25/75")
    addPhotoToDocument(photo5000AfterPour_50_50, "Пролив 50/50")
    addPhotoToDocument(photo5000AfterPour_75_25, "Пролив 75/25")
    addPhotoToDocument(photo5000AfterPour_spent, "Пролив отраб")

    addPhotoToDocument(photo2000General, "Фотография плотномера при замере плотности 15% HCl кислоты")
    addPhotoToDocument(photo2000AfterPour_25_75, "Пролив 25/75")
    addPhotoToDocument(photo2000AfterPour_50_50, "Пролив 50/50")
    addPhotoToDocument(photo2000AfterPour_75_25, "Пролив 75/25")
    addPhotoToDocument(photo2000AfterPour_spent, "Пролив отраб")

    // Добавляем подписи
    val signatureParagraph = document.createParagraph()
    signatureParagraph.alignment = ParagraphAlignment.CENTER

    val byteArrayOutputStream = ByteArrayOutputStream()
    signatureBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val signatureBytes = byteArrayOutputStream.toByteArray()

    val signatureRun = signatureParagraph.createRun()
    signatureRun.addPicture(
        ByteArrayInputStream(signatureBytes),
        XWPFDocument.PICTURE_TYPE_PNG,
        "signature.png",
        Units.toEMU(120.0),
        Units.toEMU(70.0)
    )

    // Сохраняем документ во временный файл
    val file = File.createTempFile("report", ".docx", context.cacheDir)
    document.write(FileOutputStream(file))
    return file
}

