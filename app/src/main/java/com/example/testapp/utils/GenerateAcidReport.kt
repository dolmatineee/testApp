package com.example.testapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well
import org.apache.commons.imaging.common.ImageMetadata
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
    preparedAcid: String,
    concentratedAcid: String,
    context: Context,
    photoDensimeterConcentratedAcid: Uri,
    photoDensimeterPreparedAcid: Uri,
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
    val document = XWPFDocument()

    // ===== Page 1 (Титульный лист) =====
    val contactParagraph = document.createParagraph()
    val contactRun = contactParagraph.createRun()
    contactRun.setText("Тел:факс: 8(8553) 38-64-36, факс: 8(8553) 38-64-36; E-mail: armykk@tagras.ru")
    contactRun.fontSize = 10
    contactRun.fontFamily = "Times New Roman"
    contactRun.addBreak()

    // Название компании
    val companyParagraph = document.createParagraph()
    companyParagraph.alignment = ParagraphAlignment.CENTER
    val companyRun = companyParagraph.createRun()
    companyRun.setText("ОБЩЕСТВО С ОГРАНИЧЕННОЙ")
    companyRun.isBold = true
    companyRun.fontSize = 12
    companyRun.addBreak()
    companyRun.setText("ОТВЕТСТВЕННОСТЬЮ")
    companyRun.isBold = true
    companyRun.addBreak()
    companyRun.setText("«Лениногорск - РемСервис»")
    companyRun.isBold = true
    companyRun.addBreak()

    val separatorParagraphForTitle = document.createParagraph()
    val separatorRunForTitle = separatorParagraphForTitle.createRun()
    repeat(6) {
        separatorRunForTitle.addBreak()
    }
    val titleParagraph = document.createParagraph()
    titleParagraph.alignment = ParagraphAlignment.CENTER
    val titleRun = titleParagraph.createRun()
    titleRun.setText("Проведение тестов перед проведением")
    titleRun.addBreak()
    titleRun.setText("КГРП согласно стандартам по ОПЗ,")
    titleRun.addBreak()
    titleRun.setText("БОПЗ, ГРП, КГРП")
    titleRun.addBreak()
    titleRun.setText("(Quality Assurance Quality Control)")
    titleRun.fontFamily = "Times New Roman"
    titleRun.isBold = true
    titleRun.fontSize = 24
    titleRun.addBreak()

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

    infoRun.addBreak()

    // Заголовок "Лабораторный отчет"
    val reportTitleParagraph = document.createParagraph()
    reportTitleParagraph.alignment = ParagraphAlignment.CENTER
    val reportTitleRun = reportTitleParagraph.createRun()
    reportTitleRun.setText("Лабораторный отчет")
    reportTitleRun.isBold = true
    reportTitleRun.fontSize = 18

    repeat(6) {
        reportTitleRun.addBreak()
    }

    // Заказчик
    val customerParagraph = document.createParagraph()
    customerParagraph.alignment = ParagraphAlignment.CENTER
    val customerRun = customerParagraph.createRun()
    customerRun.setText("Для ${customer.companyName}")
    customerRun.addBreak()
    customerRun.setText("${currentDate.year} г.")
    customerRun.fontSize = 18
    customerRun.isBold = true
    customerRun.addBreak()

    // Разрыв страницы
    document.createParagraph().createRun().addBreak(BreakType.PAGE)

    // ===== Page 2 =====
    // Таблица с основной информацией
    val infoTable = document.createTable(5, 2)
    infoTable.setWidth("100%")

    val rows = listOf(
        "Компания" to customer.companyName,
        "Месторождение" to field.name,
        "Скважина" to well.wellNumber,
        "Дата" to formattedDate,
        "Лаборант" to "Ханбиков Э.И."
    )

    rows.forEachIndexed { index, (label, value) ->
        val row = infoTable.getRow(index)
        row.getCell(0).setText(label)
        row.getCell(1).setText(value)
    }

    // Приготовление кислоты
    val acidParagraph = document.createParagraph()
    acidParagraph.spacingBefore = 400
    val acidRun = acidParagraph.createRun()
    acidRun.setText("1. Приготовили $preparedAcid раствор синтетической HCl из $concentratedAcid HCl (Кислота соляная синтетическая техническая ГОСТ 857-95).")
    acidRun.addBreak()

    // Таблица с загрузками
    val loadingTable = document.createTable(4, 2)
    loadingTable.setWidth("100%")

    loadingTable.getRow(0).getCell(0).setText("Загрузка: на контроль железа на 5000ppm")
    loadingTable.getRow(0).getCell(1).setText("Загрузка: на контроль железа на 2000ppm")

    loadingTable.getRow(1).getCell(0).setText("Ингибитор коррозии AS-CO - 1 л/м3")
    loadingTable.getRow(1).getCell(1).setText("Ингибитор коррозии AS-CO - 1 л/м3")

    loadingTable.getRow(2).getCell(0).setText("Стабилизатор железа AS-IR - 12 л/м3")
    loadingTable.getRow(2).getCell(1).setText("Стабилизатор железа AS-IR - 7 л/м3")

    loadingTable.getRow(3).getCell(0).setText("Дезмульгатор AS-DA - 8 л/м3")
    loadingTable.getRow(3).getCell(1).setText("Дезмульгатор AS-DA - 8 л/м3")

    // Замеры плотности
    val measureParagraph = document.createParagraph()
    measureParagraph.spacingBefore = 400
    val measureRun = measureParagraph.createRun()
    measureRun.setText("2. Замерили плотность и концентрацию кислоты плотномером приготовленного кислотного состава.")
    measureRun.addBreak()

    // ===== Таблица для фотографий плотномеров 2x1 =====
    val densimeterTable = document.createTable(1, 2)
    densimeterTable.setWidth("100%")

    // Функция для добавления фото в ячейку таблицы
    fun addPhotoToCell(cell: XWPFTableCell, uri: Uri, description: String) {
        val paragraph = cell.addParagraph()
        paragraph.alignment = ParagraphAlignment.CENTER
        val run = paragraph.createRun()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val imageBytes = inputStream.readBytes()
                val (width, height) = getImageDimensions(imageBytes)
                val aspectRatio = width.toDouble() / height.toDouble()

                val targetHeight = 200.0
                val targetWidth = targetHeight * aspectRatio

                run.addPicture(
                    ByteArrayInputStream(imageBytes),
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "image.jpg",
                    Units.toEMU(targetWidth),
                    Units.toEMU(targetHeight)
                )

                // Добавляем описание под фото
                val descParagraph = cell.addParagraph()
                descParagraph.alignment = ParagraphAlignment.CENTER
                descParagraph.createRun().setText(description)
            }
        } catch (e: Exception) {
            cell.text = "Ошибка загрузки изображения"
        }
    }

    // Функция для добавления фото вне таблицы (по центру)
    fun addCenteredPhoto(document: XWPFDocument, uri: Uri, description: String) {
        val paragraph = document.createParagraph()
        paragraph.alignment = ParagraphAlignment.CENTER
        val run = paragraph.createRun()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val imageBytes = inputStream.readBytes()
                val (width, height) = getImageDimensions(imageBytes)
                val aspectRatio = width.toDouble() / height.toDouble()

                val targetHeight = 300.0
                val targetWidth = targetHeight * aspectRatio

                run.addPicture(
                    ByteArrayInputStream(imageBytes),
                    XWPFDocument.PICTURE_TYPE_JPEG,
                    "image.jpg",
                    Units.toEMU(targetWidth),
                    Units.toEMU(targetHeight)
                )

                // Добавляем описание под фото
                val descParagraph = document.createParagraph()
                descParagraph.alignment = ParagraphAlignment.CENTER
                descParagraph.createRun().setText(description)
            }
        } catch (e: Exception) {
            run.setText("Ошибка загрузки изображения")
        }
    }

    // Добавляем фотографии плотномеров
    densimeterTable.getRow(0).getCell(0).apply {
        addPhotoToCell(this, photoDensimeterConcentratedAcid,
            "Фотография плотномера при замере плотности концентрированной кислоты")
    }

    densimeterTable.getRow(0).getCell(1).apply {
        addPhotoToCell(this, photoDensimeterPreparedAcid,
            "Фотография плотномера при замере плотности $preparedAcid HCl кислоты")
    }

    // Разрыв страницы
    document.createParagraph().createRun().addBreak(BreakType.PAGE)

    // ===== Page 3-5 (Тесты) =====
    // Функция для добавления тестовых страниц с таблицами фотографий
    fun addTestPage(title: String, ppm: String, generalPhoto: Uri, photos: List<Pair<String, Uri>>) {
        // Заголовок теста
        val testTitleParagraph = document.createParagraph()
        testTitleParagraph.alignment = ParagraphAlignment.CENTER
        val testTitleRun = testTitleParagraph.createRun()
        testTitleRun.setText(title)
        testTitleRun.isBold = true
        testTitleRun.fontSize = 14
        testTitleRun.addBreak()
        testTitleRun.setText("Кислота, рассчитанная на контроль железа $ppm")
        testTitleRun.isBold = false
        testTitleRun.fontSize = 12
        testTitleRun.addBreak()

        // Пролив через сито
        val sieveParagraph = document.createParagraph()
        sieveParagraph.alignment = ParagraphAlignment.CENTER
        sieveParagraph.spacingBefore = 200
        val sieveRun = sieveParagraph.createRun()
        sieveRun.setText("Пролив через сито 100 меш")
        sieveRun.addBreak()

        // Добавляем общее фото вне таблицы по центру
        addCenteredPhoto(document, generalPhoto, "Общий вид теста $ppm")

        // ===== Таблица для фотографий проливов 4x2 =====
        val pourTable = document.createTable(photos.size, 2)
        pourTable.setWidth("100%")

        // Добавляем фотографии проливов
        photos.forEachIndexed { index, (desc, uri) ->
            val row = pourTable.getRow(index)
            row.getCell(0).apply {
                addPhotoToCell(this, uri, "")
            }
            row.getCell(1).text = "Пролив $desc\nПролив чистый, осадка нет"
        }

        document.createParagraph().createRun().addBreak(BreakType.PAGE)
    }

    // Добавляем тесты для 5000ppm
    addTestPage(
        "Тест на совместимость и распад эмульсии",
        "5000ppm",
        photo5000General,
        listOf(
            "25/75" to photo5000AfterPour_25_75,
            "50/50" to photo5000AfterPour_50_50,
            "75/25" to photo5000AfterPour_75_25,
            "отраб" to photo5000AfterPour_spent
        )
    )

    // Добавляем тесты для 2000ppm
    addTestPage(
        "Тест на совместимость и распад эмульсии",
        "2000ppm",
        photo2000General,
        listOf(
            "25/75" to photo2000AfterPour_25_75,
            "50/50" to photo2000AfterPour_50_50,
            "75/25" to photo2000AfterPour_75_25,
            "отраб" to photo2000AfterPour_spent
        )
    )


    // Сохранение документа
    val file = File.createTempFile("report", ".docx", context.cacheDir)
    document.write(FileOutputStream(file))
    return file
}