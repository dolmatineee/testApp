package com.example.testapp.ui.screens

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.testapp.R
import com.example.testapp.ui.viewmodels.SupervisorProfileScreenViewModel
import com.example.testapp.utils.toImageBitmap

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogoutClickListener: () -> Unit,
    onSignatureCardClickListener: () -> Unit,
    viewModel: SupervisorProfileScreenViewModel = hiltViewModel()
) {

    val sharedPreferences =
        LocalContext.current.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val fullName = sharedPreferences.getString("fullName", null)
    val position = sharedPreferences.getString("position", null)

    val signatureBase64 = viewModel.getSignature()
    val signatureBitmap = remember { mutableStateOf<ImageBitmap?>(null) }

    val context = LocalContext.current

    LaunchedEffect(signatureBase64) {
        if (signatureBase64 != null) {
            signatureBitmap.value = signatureBase64.toImageBitmap()
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = "Профиль",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        IconButton(
                            onClick = {
                                viewModel.clearUserData()
                                onLogoutClickListener()
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_logout_24),
                                contentDescription = "logout",
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 12.dp)
        ) {
            item {
                UserProfileRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    fullName = fullName,
                    position = position
                )

            }

            item {

                if (position !in listOf("Мастер ГРП ООО ЛРС", "Администратор")) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.surface)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Подпись",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onSignatureCardClickListener() },
                            contentAlignment = Alignment.Center
                        ) {
                            if (signatureBitmap.value != null) {
                                Image(
                                    bitmap = signatureBitmap.value!!,
                                    contentDescription = "Подпись",
                                    modifier = Modifier
                                        .size(200.dp)
                                        .background(Color.Transparent)
                                )
                            } else {
                                Text(
                                    text = "Добавьте подпись",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.surface)
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "Поддержка",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                SupportRow(
                    onClick = {
                        context.sendMail(to = "dolmatineee@gmail.com", subject = "Техподдержка")
                    },
                    icon = painterResource(id = R.drawable.frame_66),
                    text = "Нужна помощь? Мы поможем!"
                )

                Spacer(modifier = Modifier.height(20.dp))

                SupportRow(
                    onClick = {
                        context.sendMail(to = "dolmatineee@gmail.com", subject = "Ошибка")
                    },
                    icon = painterResource(id = R.drawable.frame_67),
                    text = "Нашел ошибку? Дай нам знать об этом!"
                )

                Spacer(modifier = Modifier.height(20.dp))


                SupportRow(
                    onClick = {
                        context.sendMail(to = "dolmatineee@gmail.com", subject = "Улучшения")
                    },
                    icon = painterResource(id = R.drawable.frame_68),
                    text = "Есть идеи для улучшения? Давай сотрудничать!"
                )
            }


        }
    }
}


@Composable
fun UserProfileRow(
    modifier: Modifier = Modifier,
    fullName: String?,
    position: String?,
    avatarColor: Color = MaterialTheme.colorScheme.primary
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    )
    {
        // Аватар с первой буквой имени
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(avatarColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = fullName?.firstOrNull()?.toString() ?: "?",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Колонка с именем и позицией
        Column {
            if (!fullName.isNullOrBlank()) {
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            if (!position.isNullOrBlank()) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun SupportRow(
    onClick: () -> Unit,
    icon: Painter,
    text: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(32.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f) // Занимает всё доступное пространство
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterVertically) // Прижимаем к правому краю
        )
    }
}


@SuppressLint("QueryPermissionsNeeded")
fun Context.sendMail(to: String, subject: String) {
    try {
        // Создаём Uri для отправки письма
        val mailtoUri = Uri.parse("mailto:$to?subject=${Uri.encode(subject)}")

        // Создаём Intent для отправки письма
        val queryIntent = Intent(Intent.ACTION_VIEW, mailtoUri)
        startActivity(queryIntent)

    } catch (e: ActivityNotFoundException) {
        // Обработка случая, если нет почтового клиента
        Toast.makeText(this, "Почтовый клиент не найден", Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
        // Обработка других ошибок
        Toast.makeText(this, "Произошла ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
    }
}
